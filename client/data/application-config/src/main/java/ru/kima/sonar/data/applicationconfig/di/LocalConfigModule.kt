package ru.kima.sonar.data.applicationconfig.di

import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.data.applicationconfig.local.datasource.ProtoDataStoreDataSourceImpl
import ru.kima.sonar.data.applicationconfig.updateconfig.datasource.ProtoDataStoreUpdateConfigDataSourceImpl
import ru.kima.sonar.data.applicationconfig.updateconfig.datasource.UpdateConfigDataSource

fun localConfigModule() = module {
    single<ProtoDataStoreDataSourceImpl>() bind LocalConfigDataSource::class
    single<ProtoDataStoreUpdateConfigDataSourceImpl>() bind UpdateConfigDataSource::class
}