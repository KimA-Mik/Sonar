package ru.kima.sonar.server.migrations

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.slf4j.LoggerFactory
import ru.kima.sonar.common.serverapi.model.portfolio.SecurityType
import ru.kima.sonar.common.util.valueOr
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository
import ru.kima.sonar.server.data.user.datasource.portfolio.PortfolioDataSource
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import kotlin.time.Duration.Companion.seconds
import kotlin.time.times

private const val RETRY_COUNT = 5
suspend fun notificationActionsMigration(
    portfolioDataSource: PortfolioDataSource,
    marketDataRepository: MarketDataRepository
) {
    val logger = LoggerFactory.getLogger("AddTickersMigration")
    delay(1.seconds)
    var shares = marketDataRepository.tradableShares().first()
    var futures = marketDataRepository.tradableFutures().first()
    var retryCount = 0
    while (shares.isEmpty() || futures.isEmpty()) {
        retryCount += 1
        if (retryCount > RETRY_COUNT) {
            logger.error("Failed to fetch tradable shares or futures after $RETRY_COUNT attempts. Aborting migration.")
            return
        }

        delay(retryCount * 2.seconds)
        if (shares.isEmpty()) {
            shares = marketDataRepository.tradableShares().first()
        }
        if (futures.isEmpty()) {
            futures = marketDataRepository.tradableFutures().first()
        }
    }

    val tickersMap = buildMap(shares.size + futures.size) {
        shares.forEach { put(it.uid, it.ticker) }
        futures.forEach { put(it.uid, it.ticker) }
    }

    val typesMap = buildMap(shares.size + futures.size) {
        shares.forEach { put(it.uid, SecurityType.SHARE) }
        futures.forEach { put(it.uid, SecurityType.FUTURE) }
    }

    val entries = portfolioDataSource.allEntries().valueOr {
        logger.error("Failed to fetch portfolio entries: $it")
        return
    }

    val toUpdate = mutableListOf<PortfolioEntry>()
    entries.forEach { entry ->
        val ticker = tickersMap[entry.securityUid] ?: return@forEach
        val type = typesMap[entry.securityUid] ?: return@forEach
        if (ticker != entry.ticker || type != entry.securityType) {
            toUpdate.add(entry.copy(ticker = ticker, securityType = type))
        }
    }

    if (toUpdate.isNotEmpty()) {
        portfolioDataSource.updatePortfolioEntries(toUpdate).valueOr {
            logger.error("Failed to update portfolio entries with tickers: $it")
        }
    }
}