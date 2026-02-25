package ru.kima.sonar.server.di

import org.koin.dsl.module
import ru.kima.sonar.server.data.market.marketdata.di.marketDataModule
import ru.kima.sonar.server.data.user.di.userModule

fun dataModule(
    usersDbName: String,
    marketDataDbName: String,
    tToken: String
) = module {
    includes(userModule(usersDbName))
    includes(marketDataModule(marketDataDbName, tToken))
}