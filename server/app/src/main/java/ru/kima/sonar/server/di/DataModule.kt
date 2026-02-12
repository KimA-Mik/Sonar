package ru.kima.sonar.server.di

import org.koin.dsl.module
import ru.kima.sonar.server.data.user.di.userModule

fun dataModule() = module {
    includes(userModule())
}