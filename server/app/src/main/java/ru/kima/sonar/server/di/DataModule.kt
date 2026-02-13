package ru.kima.sonar.server.di

import org.koin.dsl.module
import ru.kima.sonar.server.data.market.marketdata.di.marketDataModule
import ru.kima.sonar.server.data.user.di.userModule

fun dataModule(
    marketDataDbName: String,
    tToken: String
) = module {
    includes(userModule())
    includes(marketDataModule(marketDataDbName, tToken))
}