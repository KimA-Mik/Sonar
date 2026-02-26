package ru.kima.sonar.server.data.user.scema.portfolio

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import ru.kima.sonar.server.data.user.scema.UserTable

internal object PortfolioTable : LongIdTable() {
    val userId = long("user_id").references(UserTable.id)
    val name = varchar("name", 255)
}

