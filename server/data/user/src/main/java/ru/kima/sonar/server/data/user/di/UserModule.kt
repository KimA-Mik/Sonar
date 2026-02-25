package ru.kima.sonar.server.data.user.di

import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.kima.sonar.server.common.util.databaseutil.DatabaseConnector
import ru.kima.sonar.server.data.user.database.UsersDatabaseConnector
import ru.kima.sonar.server.data.user.datasource.ExposedUserDataSource
import ru.kima.sonar.server.data.user.datasource.UserDataSource
import ru.kima.sonar.server.data.user.datasource.portfolio.ExposedPortfolioDataSource
import ru.kima.sonar.server.data.user.datasource.portfolio.PortfolioDataSource

fun userModule(usersDbName: String) = module {
    val usersDbQualifier = named("users")
    single(usersDbQualifier) { UsersDatabaseConnector(usersDbName) } bind DatabaseConnector::class
    single { ExposedUserDataSource(get(usersDbQualifier)) } bind UserDataSource::class
    single { ExposedPortfolioDataSource(get(usersDbQualifier)) } bind PortfolioDataSource::class
}