package ru.kima.sonar.di

import org.koin.dsl.module
import ru.kima.sonar.feature.authentication.di.authModule
import ru.kima.sonar.feature.portfolios.di.portfoliosModule
import ru.kima.sonar.feature.securities.di.securitiesModule

val featureModule = module {
    includes(authModule())
    includes(portfoliosModule())
    includes(securitiesModule())
}