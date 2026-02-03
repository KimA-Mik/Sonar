package ru.kima.sonar.server.data.market.marketdata.local.model.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.timestamp
import ru.kima.sonar.common.serverapi.model.CandleInterval
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal object CandleTable : LongIdTable("candles") {
    val time = timestamp("time")
    val instrumentUid = varchar("instrument_uid", length = 255)
    val interval = enumeration<CandleInterval>("interval")
    val open = decimal("open", 19, 4)
    val high = decimal("high", 19, 4)
    val low = decimal("low", 19, 4)
    val close = decimal("close", 19, 4)
    val volume = long("volume")
    val lastTrade = timestamp("last_trade")
    val isComplete = bool("is_complete")
    val primaryKeys = PrimaryKey(time, instrumentUid, lastTrade)
}