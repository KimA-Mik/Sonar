package ru.kima.sonar.server.data.market.marketdata.di

import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.server.common.util.databaseutil.DatabaseConnector
import ru.kima.sonar.server.common.util.di.CommonQualifiers
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepositoryImpl
import ru.kima.sonar.server.data.market.marketdata.local.LocalDataSource
import ru.kima.sonar.server.data.market.marketdata.local.LocalSqlDataSource
import ru.kima.sonar.server.data.market.marketdata.local.SqliteDatabaseConnector
import ru.kima.sonar.server.data.market.marketdata.local.consumer.CandleConsumer
import ru.kima.sonar.server.data.market.marketdata.remote.TinkoffDataSource

fun marketDataModule(
    marketDataDbName: String,
    tToken: String
) = module {
    val marketDbQualifier = named("marketData")
    single(marketDbQualifier) { SqliteDatabaseConnector(marketDataDbName) } bind DatabaseConnector::class
    single { LocalSqlDataSource(get(marketDbQualifier)) } bind LocalDataSource::class
    single {
        CandleConsumer(
            dataSource = get(),
            coroutineScope = get(CommonQualifiers.DEFAULT_SCOPE),
            ioScope = get(CommonQualifiers.IO_SCOPE)
        )
    }
    single<MarketDataRepositoryImpl>() bind MarketDataRepository::class
    single(createdAtStart = true) {
        TinkoffDataSource(tToken, get(CommonQualifiers.DEFAULT_SCOPE), get())
    }
}