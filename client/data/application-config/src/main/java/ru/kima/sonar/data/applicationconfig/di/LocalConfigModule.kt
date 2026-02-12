package ru.kima.sonar.data.applicationconfig.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.data.applicationconfig.local.datasource.ProtoDataStoreDataSourceImpl

fun localConfigModule() = module {
    singleOf(::ProtoDataStoreDataSourceImpl) bind LocalConfigDataSource::class
}