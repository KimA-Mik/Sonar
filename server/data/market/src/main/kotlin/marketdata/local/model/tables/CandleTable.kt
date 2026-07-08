package ru.kima.sonar.server.data.market.marketdata.local.model.tables

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import ru.kima.sonar.common.serverapi.model.CandleInterval
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal object CandleTable : LongIdTable("candles") {
    val ticker = varchar("ticker", length = 32)
    val timestamp = long("timestamp")
    val interval = enumeration<CandleInterval>("interval")
    val open = decimal("open", 19, 4)
    val high = decimal("high", 19, 4)
    val low = decimal("low", 19, 4)
    val close = decimal("close", 19, 4)
    val volume = long("volume")
    val isComplete = bool("is_complete")

    //    val primaryKeys = PrimaryKey(ticker, interval)
    val index = index(
        isUnique = false,
        ticker, timestamp
    )
}