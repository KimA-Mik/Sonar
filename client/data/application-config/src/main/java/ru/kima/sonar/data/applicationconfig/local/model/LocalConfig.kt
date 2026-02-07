package ru.kima.sonar.data.applicationconfig.local.model

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.model.NotificationProvider

@Serializable
data class LocalConfig(
    val login: String?,
    val password: String?,
    val notificationProvider: NotificationProvider?,
    val notificationProviderClientId: String?,
    val apiUrl: String,
    val apiAccessToken: String?
)