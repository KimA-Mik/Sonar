package ru.kima.sonar.di

import org.koin.dsl.module
import ru.kima.sonar.data.applicationconfig.di.localConfigModule
import ru.kima.sonar.data.homeapi.di.homeApiModule

fun dataModule() = module {
    includes(localConfigModule())
    includes(homeApiModule())
}