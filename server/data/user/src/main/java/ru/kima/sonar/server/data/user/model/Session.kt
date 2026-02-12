package ru.kima.sonar.server.data.user.model

import ru.kima.sonar.common.serverapi.model.NotificationProvider
import kotlin.time.Instant

data class Session(
    val id: Long,
    val userId: Long,
    val token: String,
    val notificationProvider: NotificationProvider?,
    val notificationProviderId: String?,
    val createdAt: Instant,
    val lastAccessed: Instant,
    val device: String
)
