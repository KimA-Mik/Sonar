package ru.kima.sonar.server.data.market.marketdata

import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemFuture
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemShare

interface MarketDataRepository {
    fun tradableShares(): Flow<List<ListItemShare>>
    fun tradableFutures(): Flow<List<ListItemFuture>>
}