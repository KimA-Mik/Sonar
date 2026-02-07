package ru.kima.sonar.data.homeapi.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.DigestAuthCredentials
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.auth.providers.digest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest
import ru.kima.sonar.common.serverapi.model.NotificationProvider
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource

class KtorHomeApiDataSource(
    private val localConfigDataSource: LocalConfigDataSource,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : HomeApiDataSource {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json)
        }

        install(Resources)
        install(Auth) {
            digest {
                //Adsaojdoas;dh;dfjs;fjdk
                credentials {
                    val localConfig = localConfigDataSource
                        .localConfig()
                        .firstOrNull() ?: return@credentials null

                    val login = localConfig.login ?: return@credentials null
                    val password = localConfig.password ?: return@credentials null
                    DigestAuthCredentials(login, password)
                }
            }

            bearer {
                loadTokens {
                    localConfigDataSource
                        .localConfig()
                        .map { it.apiAccessToken }
                        .firstOrNull()
                        ?.let { BearerTokens(it, null) }
                }

                refreshTokens {
                    val localConfig = localConfigDataSource
                        .localConfig()
                        .firstOrNull() ?: return@refreshTokens null

                    val login = localConfig.login ?: return@refreshTokens null
                    val password = localConfig.password ?: return@refreshTokens null

                    val clientId = localConfig.notificationProviderClientId
                    val request =
                        if (clientId == null) {
                            AuthenticateClientRequest.NoNotificationProviderLoginRequest(
                                login,
                                password
                            )
                        } else when (localConfig.notificationProvider) {
                            NotificationProvider.FIREBASE ->
                                AuthenticateClientRequest.FirebaseLoginRequest(
                                    login,
                                    password,
                                    clientId
                                )

                            NotificationProvider.HUAWEI_PUSH_KIT ->
                                AuthenticateClientRequest.HuaweiPushKitLoginRequest(
                                    login,
                                    password,
                                    clientId
                                )

                            null -> AuthenticateClientRequest.NoNotificationProviderLoginRequest(
                                login,
                                password
                            )
                        }

                    val response = client.get(Auth) {
                        setBody(request)
                    }

//                    client.get("")
                    null
                }

//                sendWithoutRequest {
//                    false
//                }
            }
        }
    }

    init {
        //idk if i need to cancel it when activity is stopped
        localConfigDataSource.localConfig()
            .map { it.apiUrl }
            .onEach {
                val url = if (!it.endsWith('/')) "$it/" else it
                client.config {
                    defaultRequest {
                        url(url)
                    }
                }
            }.launchIn(coroutineScope)
    }

    override suspend fun login(
        login: String,
        password: String
    ): SonarResult<Unit, Unit> {

        return SonarResult.Error(Unit)
    }
}