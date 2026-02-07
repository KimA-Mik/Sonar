package ru.kima.sonar.common.serverapi.clientrequests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

sealed interface AuthenticateClientRequest {
    val login: String
    val password: String

    @Serializable
    @SerialName("firebase")
    data class FirebaseLoginRequest(
        override val login: String,
        override val password: String,
        val notificationProviderClientId: String
    ) : AuthenticateClientRequest

    @Serializable
    @SerialName("huawei_push_kit")
    data class HuaweiPushKitLoginRequest(
        override val login: String,
        override val password: String,
        val notificationProviderClientId: String
    ) : AuthenticateClientRequest

    @Serializable
    @SerialName("no_notification_provider")
    data class NoNotificationProviderLoginRequest(
        override val login: String,
        override val password: String
    ) : AuthenticateClientRequest

    companion object {
        val serializationModule = SerializersModule {
            polymorphic(AuthenticateClientRequest::class) {
                subclass(FirebaseLoginRequest::class)
                subclass(HuaweiPushKitLoginRequest::class)
                subclass(NoNotificationProviderLoginRequest::class)
            }
        }
    }
}
