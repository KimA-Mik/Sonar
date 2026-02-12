package ru.kima.sonar.server.data.user.scema

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

internal class SessionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SessionEntity>(SessionTable)

    var userId by SessionTable.userId
    var token by SessionTable.token
    var notificationProvider by SessionTable.notificationProvider
    var notificationProviderId by SessionTable.notificationProviderId
    var createdAt by SessionTable.createdAt
    var lastAccessed by SessionTable.lastAccessed
    var device by SessionTable.device

    var user by UserEntity referencedOn SessionTable.userId
}

