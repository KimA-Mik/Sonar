package ru.kima.sonar.server.data.user.di

import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.kima.sonar.server.data.user.database.DatabaseConnector
import ru.kima.sonar.server.data.user.datasource.ExposedUserDataSource
import ru.kima.sonar.server.data.user.datasource.UserDataSource

fun userModule() = module {
    single(named("users")) { DatabaseConnector("users") }
    single { ExposedUserDataSource(get(named("users"))) } bind UserDataSource::class
}