package ru.kima.sonar.server.data.user.di

import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.kima.sonar.server.common.util.databaseutil.DatabaseConnector
import ru.kima.sonar.server.data.user.database.UsersDatabaseConnector
import ru.kima.sonar.server.data.user.datasource.ExposedUserDataSource
import ru.kima.sonar.server.data.user.datasource.UserDataSource

fun userModule(usersDbName: String) = module {
    single(named("users")) { UsersDatabaseConnector(usersDbName) } bind DatabaseConnector::class
    single { ExposedUserDataSource(get(named("users"))) } bind UserDataSource::class
}