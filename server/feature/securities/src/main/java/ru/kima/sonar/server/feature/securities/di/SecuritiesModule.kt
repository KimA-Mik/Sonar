package ru.kima.sonar.server.feature.securities.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.server.feature.securities.controller.SecuritiesController

fun securitiesModule() = module {
    single<SecuritiesController>()
}