package ru.kima.sonar.server.data.user.datasource.portfolio

import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntity
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryEntity


internal fun JdbcTransaction.deletePortfolioEntity(entity: PortfolioEntity) {
    entity.rules.forEach { it.delete() }
    entity.entries.forEach { deletePortfolioEntryEntity(it) }
    entity.delete()
}

internal fun JdbcTransaction.deletePortfolioEntryEntity(entity: PortfolioEntryEntity) {
    entity.stopLosses.forEach { it.delete() }
    entity.takeProfits.forEach { it.delete() }
    entity.delete()
}