package ru.kima.sonar.server.data.market.marketdata

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemFuture
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemShare
import ru.kima.sonar.server.data.market.marketdata.local.LocalDataSource
import ru.kima.sonar.server.data.market.marketdata.remote.TinkoffDataSource
import java.math.BigDecimal
import kotlin.time.Instant

internal class MarketDataRepositoryImpl(
    private val localDataSource: LocalDataSource,
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
}