package ru.kima.sonar.server.data.market.marketdata.local

import org.ta4j.core.BarSeries
import ru.kima.sonar.common.serverapi.model.CandleInterval
import kotlin.time.Instant

internal interface LocalDataSource {
    suspend fun getCandles(ticker: String, interval: CandleInterval): BarSeries?
    suspend fun onTick(ticker: String, price: Double, time: Instant)
}