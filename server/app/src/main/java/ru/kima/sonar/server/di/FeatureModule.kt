package ru.kima.sonar.server.di

import org.koin.dsl.module
import ru.kima.sonar.server.feature.auth.di.authModule
import ru.kima.sonar.server.feature.securities.di.securitiesModule

fun featureModule() = module {
    includes(authModule())
    includes(securitiesModule())
}