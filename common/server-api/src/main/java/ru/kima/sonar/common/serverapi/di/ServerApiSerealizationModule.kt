package ru.kima.sonar.common.serverapi.di

import kotlinx.serialization.modules.SerializersModule
import ru.kima.sonar.common.serverapi.clientrequests.AuthenticateClientRequest

val serverApiSerializationModule = SerializersModule {
    include(AuthenticateClientRequest.serializationModule)
}