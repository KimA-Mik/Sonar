package ru.kima.sonar.data.homeapi.datasource

import android.os.Build
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest
import ru.kima.sonar.common.serverapi.model.NotificationProvider
import ru.kima.sonar.common.serverapi.routing.AuthRoute
import ru.kima.sonar.common.serverapi.routing.SecurityRoute
import ru.kima.sonar.common.serverapi.serverresponse.AuthorizationResult
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemFuture
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemShare
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.common.util.sonarRunCaching
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider
import ru.kima.sonar.data.homeapi.error.HomeApiError
import ru.kima.sonar.data.homeapi.model.mapper.toNotificationProvider
import java.net.SocketTimeoutException

private const val TAG = "KtorHomeApiDataSource"

internal class KtorHomeApiDataSource(
    private val localConfigDataSource: LocalConfigDataSource,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : HomeApiDataSource {
    var client = HttpClient(OkHttp) {
        install(Logging) {
            logger = Logger.DEFAULT
        }
        install(Resources)
        install(ContentNegotiation) {
            json(Json)
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    localConfigDataSource
                        .localConfig()
                        .map { it.apiAccessToken }
                        .firstOrNull()
                        ?.let { BearerTokens(it, null) }
                }
            }
        }
    }

    init {
        coroutineScope.launch {
            localConfigDataSource.localConfig()
                .map { it.apiUrl }
                .collect { url ->
                    val oldClient = client
                    client = client.config {
                        defaultRequest {
                            url(url)
                        }
                    }
                    oldClient.close()
                }
        }
    }

    override suspend fun login(
        login: String,
        password: String,
        localNotificationProvider: LocalNotificationProvider?,
        notificationProviderClientId: String?
    ): SonarResult<String, HomeApiError> {
        val notificationProvider = localNotificationProvider?.toNotificationProvider()
        val request = if (notificationProviderClientId == null) {
            AuthenticateClientRequest.NoNotificationProviderLoginRequest(
                login,
                password,
                //TODO: maybe abstract it away
                Build.MODEL
            )
        } else when (notificationProvider) {
            NotificationProvider.FIREBASE ->
                AuthenticateClientRequest.FirebaseLoginRequest(
                    login,
                    password,
                    Build.MODEL,
                    notificationProviderClientId
                )

            NotificationProvider.HUAWEI_PUSH_KIT ->
                AuthenticateClientRequest.HuaweiPushKitLoginRequest(
                    login,
                    password,
                    Build.MODEL,
                    notificationProviderClientId
                )

            null -> AuthenticateClientRequest.NoNotificationProviderLoginRequest(
                login,
                password,
                Build.MODEL,
            )
        }

        return when (val result = safeApiCall<AuthorizationResult>(logOutOnUnauthorized = false) {
            client.post(AuthRoute.Login()) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }) {
            is SonarResult.Error -> SonarResult.Error(result.data)
            is SonarResult.Success -> SonarResult.Success(result.data.token)
        }
    }

    private suspend inline fun <reified T> safeApiCall(
        logOutOnUnauthorized: Boolean = true,
        apiCall: suspend () -> HttpResponse
    ): SonarResult<T, HomeApiError> = try {
        val response = apiCall()
        when (response.status) {
            HttpStatusCode.OK -> SonarResult.Success(response.body<T>())
            HttpStatusCode.Unauthorized -> {
                if (logOutOnUnauthorized) localConfigDataSource.updateApiAccessToken(null)
                SonarResult.Error(HomeApiError.Unauthorized)
            }

            else -> SonarResult.Error(HomeApiError.UnknownApiError(response.status.value))
        }
    } catch (e: IOException) {
        Log.d(TAG, "IOException: $e")
        SonarResult.Error(HomeApiError.NetworkError)
    } catch (e: Exception) {
        Log.d(TAG, "Exception: $e")
        SonarResult.Error(HomeApiError.UnknownError(e))
    }

    override fun tradableShares(): Flow<SonarResult<List<ListItemShare>, HomeApiError>> =
        channelFlow {
            try {
                client.webSocket(path = SecurityRoute.Shares.PATH) {
                    while (isActive) {
                        val message = sonarRunCaching { receiveDeserialized<List<ListItemShare>>() }
                        if (message.isSuccess()) {
                            send(SonarResult.Success(message.data))
                        } else {
                            send(SonarResult.Error(HomeApiError.UnknownError(message.data)))
                            break
                        }
                    }
                    close()
                }
            } catch (_: SocketTimeoutException) {
                send(SonarResult.Error(HomeApiError.NetworkError))
            }
        }

    override fun tradableFutures(): Flow<SonarResult<List<ListItemFuture>, HomeApiError>> =
        channelFlow {
            try {
                client.webSocket(path = SecurityRoute.Futures.PATH) {
                    while (isActive) {
                        val message =
                            sonarRunCaching { receiveDeserialized<List<ListItemFuture>>() }
                        if (message.isSuccess()) {
                            send(SonarResult.Success(message.data))
                        } else {
                            send(SonarResult.Error(HomeApiError.UnknownError(message.data)))
                            break
                        }
                    }
                    close()
                }
            } catch (_: SocketTimeoutException) {
                send(SonarResult.Error(HomeApiError.NetworkError))
            }
        }
}