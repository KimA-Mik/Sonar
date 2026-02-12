package ru.kima.sonar.server.di

import org.koin.dsl.module
import ru.kima.sonar.server.feature.auth.di.authModule

fun featureModule() = module {
    includes(authModule())
}