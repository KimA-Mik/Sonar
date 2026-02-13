package ru.kima.sonar.server.data.market.marketdata

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.kima.sonar.common.serverapi.model.security.Future
import ru.kima.sonar.common.serverapi.model.security.Share
import ru.kima.sonar.server.data.market.marketdata.local.LocalDataSource

internal class MarketDataRepositoryImpl(
    private val localDataSource: LocalDataSource
) : MarketDataRepository {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    override fun startPolling() {
        TODO("Not yet implemented")
    }

    override fun stopPolling() {
        job.cancel()
    }

    override suspend fun getShares(): List<Share> {
        TODO("Not yet implemented")
    }

    override suspend fun getFutures(): List<Future> {
        TODO("Not yet implemented")
    }
}