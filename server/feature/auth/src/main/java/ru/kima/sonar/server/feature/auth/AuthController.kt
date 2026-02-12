package ru.kima.sonar.server.feature.auth

import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest
import ru.kima.sonar.common.serverapi.model.NotificationProvider

internal class AuthController(
    private val authManager: AuthManager,
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
}