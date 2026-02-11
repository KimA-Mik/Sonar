package ru.kima.sonar.data.homeapi.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest
import ru.kima.sonar.common.serverapi.model.NotificationProvider
import ru.kima.sonar.common.serverapi.routing.AuthRoute.Login
import ru.kima.sonar.common.serverapi.serverresponse.AuthorizationResult
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider
import ru.kima.sonar.data.homeapi.error.HomeApiError
import ru.kima.sonar.data.homeapi.model.mapper.toNotificationProvider

internal class KtorHomeApiDataSource(
    private val localConfigDataSource: LocalConfigDataSource,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : HomeApiDataSource {
    val client = HttpClient(OkHttp) {
        install(Resources)
        install(ContentNegotiation) {
            json(Json)
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
        //idk if i need to cancel it when activity is stopped
        localConfigDataSource.localConfig()
            .map { it.apiUrl }
            .onEach {
                client.config {
                    defaultRequest {
                        url(it)
                    }
                }
            }.launchIn(coroutineScope)
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
                password
            )
        } else when (notificationProvider) {
            NotificationProvider.FIREBASE ->
                AuthenticateClientRequest.FirebaseLoginRequest(
                    login,
                    password,
                    notificationProviderClientId
                )

            NotificationProvider.HUAWEI_PUSH_KIT ->
                AuthenticateClientRequest.HuaweiPushKitLoginRequest(
                    login,
                    password,
                    notificationProviderClientId
                )

            null -> AuthenticateClientRequest.NoNotificationProviderLoginRequest(
                login,
                password
            )
        }

        return when (val result = safeApiCall<AuthorizationResult>(logOutOnUnauthorized = false) {
            client.get(Login()) {
                setBody(request)
                contentType(ContentType.Application.Json)
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
    } catch (_: IOException) {
        SonarResult.Error(HomeApiError.NetworkError)
    } catch (e: Exception) {
        SonarResult.Error(HomeApiError.UnknownError(e))
    }
}