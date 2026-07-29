package ru.kima.sonar.server.data.market.marketdata.di

import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.server.common.util.di.CommonQualifiers
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepository
import ru.kima.sonar.server.data.market.marketdata.MarketDataRepositoryImpl
import ru.kima.sonar.server.data.market.marketdata.local.LocalDataSource
import ru.kima.sonar.server.data.market.marketdata.local.MemoryMarketDataSource
import ru.kima.sonar.server.data.market.marketdata.local.consumer.CandleConsumer
import ru.kima.sonar.server.data.market.marketdata.remote.TinkoffDataSource

fun marketDataModule(
    tToken: String
) = module {
    single(createdAtStart = true) {
        TinkoffDataSource(tToken, get(CommonQualifiers.DEFAULT_SCOPE), get())
    }
    single<MarketDataRepositoryImpl>() bind MarketDataRepository::class
    single<MemoryMarketDataSource>() bind LocalDataSource::class
    single {
        CandleConsumer(
//            dataSource = get(),
            coroutineScope = get(CommonQualifiers.DEFAULT_SCOPE),
        )
    }
}