package ru.kima.sonar.server.feature.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest
import ru.kima.sonar.common.serverapi.clientrequests.UpdateNotificationProvider
import ru.kima.sonar.common.serverapi.model.NotificationProvider
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.server.data.user.datasource.UserDataSource
import ru.kima.sonar.server.data.user.model.UserAndSession

internal class AuthController(
    private val authManager: AuthManager,
    private val userDataSource: UserDataSource
) {

    suspend fun login(request: AuthenticateClientRequest): String? {
        var notificationProvider: NotificationProvider?
        var notificationProviderId: String?

        when (request) {
            is AuthenticateClientRequest.FirebaseLoginRequest -> {
                notificationProvider = NotificationProvider.FIREBASE
                notificationProviderId = request.notificationProviderClientId
            }

            is AuthenticateClientRequest.HuaweiPushKitLoginRequest -> {
                notificationProvider = NotificationProvider.HUAWEI_PUSH_KIT
                notificationProviderId = request.notificationProviderClientId
            }

            is AuthenticateClientRequest.NoNotificationProviderLoginRequest -> {
                notificationProvider = null
                notificationProviderId = null
            }
        }

        return authManager.logInUser(
            email = request.login,
            password = request.password,
            device = request.device,
            notificationProvider = notificationProvider,
            notificationProviderClientId = notificationProviderId
        )
    }

    suspend fun register(login: String, password: String): Boolean {
        return authManager.createUser(login, password)
    }

    suspend fun updateNotificationProviderToken(
        call: RoutingCall,
    ) {
        val request = try {
            call.receive<UpdateNotificationProvider>()
        } catch (_: Exception) {
            call.respond(HttpStatusCode.BadRequest)
            return
        }
        val userAndSession = call.getUserOrISE { return }
        val result = userDataSource.updateSession(
            userAndSession.session.copy(
                notificationProvider = request.notificationProvider,
                notificationProviderId = request.token
            )
        )
        if (result.isSuccess()) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    private suspend inline fun RoutingCall.getUserOrISE(actionReturn: () -> Nothing): UserAndSession {
        val user = principal<UserAndSession>()
        return if (user != null) {
            user
        } else {
            respond(HttpStatusCode.InternalServerError)
            actionReturn()
        }
    }
}