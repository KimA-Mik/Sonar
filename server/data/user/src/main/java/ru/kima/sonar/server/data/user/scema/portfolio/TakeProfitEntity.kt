package ru.kima.sonar.server.data.user.scema.portfolio

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

internal class TakeProfitEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<TakeProfitEntity>(TakeProfitTable)

    var entryId by TakeProfitTable.entryId
    var price by TakeProfitTable.price
    var note by TakeProfitTable.note
    var shouldNotify by TakeProfitTable.shouldNotify
    var lastUnboundUpdate by TakeProfitTable.lastUnboundUpdate
    var lastUnboundUpdatePrice by TakeProfitTable.lastUnboundUpdatePrice

    val entry by PortfolioEntryEntity referencedOn TakeProfitTable.entryId
}