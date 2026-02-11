package ru.kima.sonar.data.homeapi.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource
import ru.kima.sonar.data.homeapi.datasource.KtorHomeApiDataSource

fun homeApiModule() = module {
    single {
        KtorHomeApiDataSource(
            get(),
            //TODO: factor out somewhere using named
            CoroutineScope(Dispatchers.IO)
        )
    } bind HomeApiDataSource::class
}