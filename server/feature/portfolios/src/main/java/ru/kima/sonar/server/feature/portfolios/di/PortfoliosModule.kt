package ru.kima.sonar.server.feature.portfolios.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.server.feature.portfolios.controller.PortfoliosController

fun portfoliosModule() = module {
    single<PortfoliosController>()
}