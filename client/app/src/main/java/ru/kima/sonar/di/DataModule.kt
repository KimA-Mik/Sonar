package ru.kima.sonar.di

import org.koin.dsl.module
import ru.kima.sonar.data.applicationconfig.di.localConfigModule

fun dataModule() = module {
    includes(localConfigModule())
}