package ru.kima.sonar.data.applicationconfig.di

import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.data.applicationconfig.local.datasource.ProtoDataStoreDataSourceImpl

fun localConfigModule() = module {
    single<ProtoDataStoreDataSourceImpl>() bind LocalConfigDataSource::class
}