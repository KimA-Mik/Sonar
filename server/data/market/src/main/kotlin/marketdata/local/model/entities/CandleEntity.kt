package ru.kima.sonar.server.data.market.marketdata.local.model.entities

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass
import ru.kima.sonar.server.data.market.marketdata.local.model.tables.CandleTable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class CandleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CandleEntity>(CandleTable)

    var time by CandleTable.time
    var instrumentUid by CandleTable.instrumentUid
    var interval by CandleTable.interval
    var open by CandleTable.open
    var high by CandleTable.high
    var low by CandleTable.low
    var close by CandleTable.close
    var volume by CandleTable.volume
    var lastTrade by CandleTable.lastTrade
    var isComplete by CandleTable.isComplete
    override fun toString() =
        "CandleEntity(id=$id, time=$time, instrumentUid=$instrumentUid, " +
                "interval=$interval, open=$open, high=$high, low=$low, close=$close, " +
                "volume=$volume, lastTrade=$lastTrade, isComplete=$isComplete)"
}
