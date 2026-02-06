package ru.kima.sonar.common.serverapi.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun serverApiModule() = module {
    single { Json { serializersModule = serverApiSerializationModule } }
}