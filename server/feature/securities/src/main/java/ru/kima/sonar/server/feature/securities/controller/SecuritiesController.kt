package ru.kima.sonar.server.feature.securities.controller

import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemFuture
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemShare
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository

internal class SecuritiesController(
    private val repository: MarketDataRepository
) {
    fun tradableShares(): Flow<List<ListItemShare>> = repository.tradableShares()
    fun tradableFutures(): Flow<List<ListItemFuture>> = repository.tradableFutures()
}