package ru.kima.sonar.di

import org.koin.dsl.module
import ru.kima.sonar.common.serverapi.di.serverApiModule

fun commonModule() = module {
    includes(serverApiModule())
}