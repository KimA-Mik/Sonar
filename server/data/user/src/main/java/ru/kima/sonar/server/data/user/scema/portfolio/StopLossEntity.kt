package ru.kima.sonar.server.data.user.scema.portfolio

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

internal class StopLossEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<StopLossEntity>(StopLossTable)

    var entryId by StopLossTable.entryId
    var price by StopLossTable.price
    var note by StopLossTable.note
    var shouldNotify by StopLossTable.shouldNotify
    var lastUnboundUpdate by StopLossTable.lastUnboundUpdate
    var lastUnboundUpdatePrice by StopLossTable.lastUnboundUpdatePrice

    val entry by PortfolioEntryEntity referencedOn StopLossTable.entryId
}