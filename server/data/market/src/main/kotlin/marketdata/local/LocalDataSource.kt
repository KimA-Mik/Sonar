package ru.kima.sonar.server.data.market.marketdata.local

import org.ta4j.core.BarSeries
import ru.kima.sonar.common.serverapi.model.CandleInterval
import ru.kima.sonar.server.data.market.marketdata.local.indicators.Indicators
import kotlin.time.Instant

internal interface LocalDataSource {
    suspend fun getCandles(ticker: String, interval: CandleInterval): BarSeries?
    suspend fun getIndicators(ticker: String, interval: CandleInterval): Indicators?
    suspend fun onTick(ticker: String, price: Double, time: Instant)
}