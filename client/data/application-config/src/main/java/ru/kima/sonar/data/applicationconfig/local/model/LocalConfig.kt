package ru.kima.sonar.data.applicationconfig.local.model

import kotlinx.serialization.Serializable

@Serializable
data class LocalConfig(
    val login: String?,
    val password: String?,
    val notificationProvider: LocalNotificationProvider?,
    val notificationProviderClientId: String?,
    val apiUrl: String,
    val apiAccessToken: String?
)