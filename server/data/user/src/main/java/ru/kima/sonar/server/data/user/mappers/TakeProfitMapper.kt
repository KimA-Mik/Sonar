package ru.kima.sonar.server.data.user.mappers

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import ru.kima.sonar.server.data.user.model.portfolio.TakeProfit
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryTable
import ru.kima.sonar.server.data.user.scema.portfolio.TakeProfitEntity
import kotlin.time.Instant


internal fun TakeProfitEntity.toDomainModel() = TakeProfit(
    id = id.value,
    entryId = entryId.value,
    price = price,
    note = note,
    shouldNotify = shouldNotify,
    lastUnboundUpdate = Instant.fromEpochMilliseconds(lastUnboundUpdate),
    lastUnboundUpdatePrice = lastUnboundUpdatePrice
)

internal fun TakeProfitEntity.putInside(stopLoss: TakeProfit) {
    entryId = EntityID(stopLoss.entryId, PortfolioEntryTable)
    price = stopLoss.price
    note = stopLoss.note
    shouldNotify = stopLoss.shouldNotify
    lastUnboundUpdate = stopLoss.lastUnboundUpdate.toEpochMilliseconds()
    lastUnboundUpdatePrice = stopLoss.lastUnboundUpdatePrice
}