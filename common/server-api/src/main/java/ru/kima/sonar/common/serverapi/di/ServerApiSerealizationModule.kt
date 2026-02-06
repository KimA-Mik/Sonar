package ru.kima.sonar.common.serverapi.di

import kotlinx.serialization.modules.SerializersModule
import ru.kima.sonar.common.serverapi.clientrequests.RegisterClientRequest

val serverApiSerializationModule = SerializersModule {
    include(RegisterClientRequest.serializationModule)
}