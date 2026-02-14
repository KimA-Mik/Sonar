package ru.kima.sonar.server.data.market.marketdata.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepositoryImpl
import ru.kima.sonar.server.data.market.marketdata.local.DatabaseConnector
import ru.kima.sonar.server.data.market.marketdata.local.LocalSqlDataSource
import ru.kima.sonar.server.data.market.marketdata.local.SqliteDatabaseConnector
import ru.kima.sonar.server.data.market.marketdata.remote.TinkoffDataSource

fun marketDataModule(
    marketDataDbName: String,
    tToken: String
) = module {
    single { SqliteDatabaseConnector(marketDataDbName) } bind DatabaseConnector::class
    singleOf(::LocalSqlDataSource) bind LocalSqlDataSource::class
    singleOf(::MarketDataRepositoryImpl) bind MarketDataRepository::class
    single(createdAtStart = true) { TinkoffDataSource(tToken) }
}