package ru.kima.sonar.server.data.market.marketdata

import ru.kima.sonar.common.serverapi.model.security.Future
import ru.kima.sonar.common.serverapi.model.security.Share

interface MarketDataRepository {
    fun startPolling()
    fun stopPolling()

    suspend fun getShares(): List<Share>
    suspend fun getFutures(): List<Future>
}