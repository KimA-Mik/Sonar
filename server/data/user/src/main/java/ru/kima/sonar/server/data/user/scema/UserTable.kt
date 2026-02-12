package ru.kima.sonar.server.data.user.scema

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object UserTable : LongIdTable() {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
}