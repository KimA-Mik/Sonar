package ru.kima.sonar.server.data.user.scema.portfolio

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

internal class RulesEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<RulesEntity>(RulesTable)

    var portfolioId by RulesTable.portfolioId
    var rulesMode by RulesTable.rulesMode
    var rule by RulesTable.rule

    var portfolio by PortfolioEntity referencedOn RulesTable.portfolioId
}