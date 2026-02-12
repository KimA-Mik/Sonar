package ru.kima.sonar.server.data.user.scema

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.timestamp
import ru.kima.sonar.common.serverapi.model.NotificationProvider

object SessionTable : LongIdTable() {
    val userId = long("user_id").references(UserTable.id)
    val token = varchar("token", 500).uniqueIndex()
    val notificationProvider = enumeration<NotificationProvider>("notification_provider").nullable()
    val notificationProviderId = varchar("notification_provider_id", 255).nullable()
    val createdAt = timestamp("created_at")
    val lastAccessed = timestamp("last_accessed")
    val device = varchar("device", 255)
}