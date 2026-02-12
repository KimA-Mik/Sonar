package ru.kima.sonar.server.data.user.scema

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

internal class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UserTable)

    var email by UserTable.email
    var passwordHash by UserTable.passwordHash

    val sessions by SessionEntity referrersOn SessionTable.userId
}

