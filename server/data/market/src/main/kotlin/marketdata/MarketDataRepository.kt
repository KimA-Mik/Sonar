package ru.kima.sonar.server.data.market.marketdata

import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemFuture
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemShare
import ru.kima.sonar.common.serverapi.model.CandleInterval
import ru.kima.sonar.common.serverapi.model.HistoricCandle
import ru.kima.sonar.common.serverapi.model.LastPrice
import ru.kima.sonar.common.serverapi.model.schema.CandleSource
import ru.kima.sonar.common.serverapi.model.security.Future
import ru.kima.sonar.common.serverapi.model.security.Share
import ru.kima.sonar.common.util.SonarResult
import kotlin.time.Instant

interface MarketDataRepository {
    fun tradableShares(): Flow<List<ListItemShare>>
    fun tradableFutures(): Flow<List<ListItemFuture>>
    fun shares(): List<Share>
    fun futures(): List<Future>
    suspend fun getCandles(
        uid: String,
        from: Instant,
        to: Instant,
        interval: CandleInterval,
        candleSource: CandleSource = CandleSource.INCLUDE_WEEKEND
    ): SonarResult<List<HistoricCandle>, Exception>

    suspend fun getMaxCandles(
        uid: String,
        interval: CandleInterval,
        candleSource: CandleSource = CandleSource.INCLUDE_WEEKEND
    ): SonarResult<List<HistoricCandle>, Exception>

    suspend fun getLastPrices(uids: List<String>): SonarResult<List<LastPrice>, Exception>
}