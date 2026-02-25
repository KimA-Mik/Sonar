package ru.kima.sonar.server.data.user.scema.portfolio

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

internal class PortfolioEntryEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PortfolioEntryEntity>(PortfolioEntryTable)

    var portfolioId by PortfolioEntryTable.portfolioId
    var securityUid by PortfolioEntryTable.securityUid
    var name by PortfolioEntryTable.name
    var lowPrice by PortfolioEntryTable.lowPrice
    var highPrice by PortfolioEntryTable.highPrice
    var note by PortfolioEntryTable.note

    var portfolio by PortfolioEntity referencedOn PortfolioEntryTable.portfolioId
}

