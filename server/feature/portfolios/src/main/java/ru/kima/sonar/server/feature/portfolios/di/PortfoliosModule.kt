package ru.kima.sonar.server.feature.portfolios.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.server.feature.portfolios.controller.PortfoliosController
import ru.kima.sonar.server.feature.portfolios.service.UpdateService
import ru.kima.sonar.server.feature.portfolios.service.UpdateServiceUpdateHandler
import ru.kima.sonar.server.feature.portfolios.service.provider.FirebaseNotificationProvider

fun portfoliosModule() = module {
    single<PortfoliosController>()
    single<UpdateServiceUpdateHandler>()
    single<UpdateService>()
    single<UpdateServiceUpdateHandler>()
    single<FirebaseNotificationProvider>()
}