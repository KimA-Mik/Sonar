package ru.kima.sonar.server.data.market.marketdata.local

import ru.kima.sonar.common.serverapi.model.Candle
import ru.kima.sonar.common.serverapi.model.CandleInterval

internal interface LocalDataSource {
    suspend fun getCandles(uuid: String, candleInterval: CandleInterval): List<Candle>
}