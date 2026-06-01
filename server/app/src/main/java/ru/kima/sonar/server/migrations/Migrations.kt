package ru.kima.sonar.server.migrations

import io.ktor.server.application.Application
import org.koin.ktor.ext.inject
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository
import ru.kima.sonar.server.data.user.datasource.portfolio.PortfolioDataSource

suspend fun Application.migrations() {
    val portfolioDatSource: PortfolioDataSource by inject()
    val marketDataRepository: MarketDataRepository by inject()
    notificationActionsMigration(portfolioDatSource, marketDataRepository)
}