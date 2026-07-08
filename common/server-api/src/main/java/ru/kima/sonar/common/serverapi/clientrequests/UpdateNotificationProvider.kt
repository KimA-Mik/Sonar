package ru.kima.sonar.common.serverapi.clientrequests

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.model.NotificationProvider

@Serializable
data class UpdateNotificationProvider(
    val token: String,
    val notificationProvider: NotificationProvider
)