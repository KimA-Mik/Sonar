package ru.kima.sonar.server.data.user.mappers

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import ru.kima.sonar.server.data.user.model.portfolio.StopLoss
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryTable
import ru.kima.sonar.server.data.user.scema.portfolio.StopLossEntity
import kotlin.time.Instant

internal fun StopLossEntity.toDomainModel() = StopLoss(
    id = id.value,
    entryId = entryId.value,
    price = price,
    note = note,
    shouldNotify = shouldNotify,
    lastUnboundUpdate = Instant.fromEpochMilliseconds(lastUnboundUpdate),
    lastUnboundUpdatePrice = lastUnboundUpdatePrice
)

internal fun StopLossEntity.putInside(stopLoss: StopLoss) {
    entryId = EntityID(stopLoss.entryId, PortfolioEntryTable)
    price = stopLoss.price
    note = stopLoss.note
    shouldNotify = stopLoss.shouldNotify
    lastUnboundUpdate = stopLoss.lastUnboundUpdate.toEpochMilliseconds()
    lastUnboundUpdatePrice = stopLoss.lastUnboundUpdatePrice
}
