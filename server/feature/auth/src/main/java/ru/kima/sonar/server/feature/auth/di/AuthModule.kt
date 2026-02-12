package ru.kima.sonar.server.feature.auth.di

import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.server.feature.auth.AuthController
import ru.kima.sonar.server.feature.auth.AuthManager

fun authModule() = module {
    single<AuthManager>()
    single<AuthController>()
}