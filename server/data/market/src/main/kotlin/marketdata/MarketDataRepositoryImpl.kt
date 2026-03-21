package ru.kima.sonar.server.data.market.marketdata

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemFuture
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemShare
import ru.kima.sonar.common.serverapi.model.CandleInterval
import ru.kima.sonar.common.serverapi.model.HistoricCandle
import ru.kima.sonar.common.serverapi.model.LastPrice
import ru.kima.sonar.common.serverapi.model.schema.CandleSource
import ru.kima.sonar.common.serverapi.model.security.Future
import ru.kima.sonar.common.serverapi.model.security.Share
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.data.market.marketdata.remote.TinkoffDataSource
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.Instant

internal class MarketDataRepositoryImpl(
//    private val localDataSource: LocalDataSource,
    private val tinkoffDataSource: TinkoffDataSource,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : MarketDataRepository {
    private val tradableShares = MutableStateFlow<List<ListItemShare>>(emptyList())
    private val tradableFutures = MutableStateFlow<List<ListItemFuture>>(emptyList())

    init {
        scope.launch { collectTradableShares() }
        scope.launch { collectTradableFutures() }
    }

    private suspend fun collectTradableShares() {
        combine(
            tinkoffDataSource.shares,
            tinkoffDataSource.sharesLastPrices
        ) { shares, lastProcess ->
            shares.map {
                val price = lastProcess[it.uid]
                ListItemShare(
                    uid = it.uid,
                    ticker = it.ticker,
                    name = it.name,
                    price = price?.price ?: BigDecimal.ZERO,
                    priceTimestamp = price?.time ?: Instant.DISTANT_PAST,
                )
            }
        }.collect {
            tradableShares.value = it
        }
    }

    private suspend fun collectTradableFutures() {
        combine(
            tinkoffDataSource.futures,
            tinkoffDataSource.futuresLastPrices
        ) { futures, lastProcess ->
            futures.map {
                val price = lastProcess[it.uid]
                ListItemFuture(
                    uid = it.uid,
                    ticker = it.ticker,
                    name = it.name,
                    price = price?.price ?: BigDecimal.ZERO,
                    priceTimestamp = price?.time ?: Instant.DISTANT_PAST,
                    expirationDate = it.expirationDate,
                    basicAsset = it.basicAsset
                )
            }
        }.collect {
            tradableFutures.value = it
        }
    }

    override fun tradableShares(): Flow<List<ListItemShare>> = tradableShares
    override fun tradableFutures(): Flow<List<ListItemFuture>> = tradableFutures
    override fun shares(): List<Share> = tinkoffDataSource.shares.value
    override fun futures(): List<Future> = tinkoffDataSource.futures.value
    override suspend fun getCandles(
        uid: String,
        from: Instant,
        to: Instant,
        interval: CandleInterval,
        candleSource: CandleSource
    ): SonarResult<List<HistoricCandle>, Exception> =
        tinkoffDataSource.getCandles(uid, from, to, interval, candleSource)

    override suspend fun getMaxCandles(
        uid: String,
        interval: CandleInterval,
        candleSource: CandleSource
    ): SonarResult<List<HistoricCandle>, Exception> {
        val to = Clock.System.now().plus(1, DateTimeUnit.HOUR)
        val from = to.minus(interval.duration * interval.limit)

        return tinkoffDataSource.getCandles(uid, from, to, interval, candleSource)
    }

    override suspend fun getLastPrices(uids: List<String>): SonarResult<List<LastPrice>, Exception> {
        val sharesPrices = tinkoffDataSource.sharesLastPrices.value
        val futuresPrices = tinkoffDataSource.futuresLastPrices.value
        val res = buildList(uids.size) {
            for (uid in uids) {
                var price = sharesPrices[uid]
                if (price != null) {
                    add(price)
                    continue
                }

                price = futuresPrices[uid]
                if (price != null) add(price)
            }
        }

        return SonarResult.Success(res)
    }
}