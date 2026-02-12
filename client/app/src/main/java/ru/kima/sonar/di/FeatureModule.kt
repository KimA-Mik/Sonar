package ru.kima.sonar.di

import org.koin.dsl.module
import ru.kima.sonar.feature.authentication.di.authModule

fun featureModule() = module {
    includes(authModule())
}