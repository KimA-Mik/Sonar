package ru.kima.sonar.server.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

fun rootModule() = module {
    single {
        CoroutineScope(Dispatchers.Default + SupervisorJob())
    }
}