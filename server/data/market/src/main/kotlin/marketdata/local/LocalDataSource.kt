package ru.kima.sonar.server.data.market.marketdata.local

import ru.kima.sonar.common.serverapi.model.Candle
import ru.kima.sonar.common.serverapi.model.CandleInterval
import kotlin.time.Instant

internal interface LocalDataSource {
    suspend fun getCandles(ticker: String, interval: CandleInterval): List<Candle>
    suspend fun getCandle(ticker: String, interval: CandleInterval, time: Instant): Candle?
    suspend fun getUnfinishedCandle(ticker: String, interval: CandleInterval): List<Candle>
    suspend fun saveCandles(candles: List<Candle>): List<Candle>
    suspend fun saveCandle(candle: Candle): Candle
    suspend fun updateCandle(candle: Candle): Candle?
}