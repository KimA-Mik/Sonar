package ru.kima.sonar.server.data.user.scema.portfolio

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import ru.kima.sonar.server.data.user.scema.UserEntity

internal class PortfolioEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PortfolioEntity>(PortfolioTable)

    var userId by PortfolioTable.userId
    var name by PortfolioTable.name

    var user by UserEntity referencedOn PortfolioTable.userId
    val entries by PortfolioEntryEntity referrersOn PortfolioEntryTable.portfolioId
}

