package ru.kima.sonar.data.applicationconfig.local.util

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import ru.kima.sonar.data.applicationconfig.BuildConfig
import ru.kima.sonar.data.applicationconfig.local.model.LocalConfig
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalSerializationApi::class)
object LocalConfigSerializer : Serializer<LocalConfig> {
    override val defaultValue: LocalConfig
        get() = LocalConfig(
            apiUrl = BuildConfig.API_ENDPOINT,
            apiAccessToken = null
        )

    override suspend fun readFrom(input: InputStream): LocalConfig = try {
        withContext(Dispatchers.IO) {
            return@withContext ProtoBuf.decodeFromByteArray<LocalConfig>(
                input.readBytes()
            )
        }
    } catch (exception: SerializationException) {
        throw CorruptionException("Cannot read proto.", exception)
    }

    override suspend fun writeTo(t: LocalConfig, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(ProtoBuf.encodeToByteArray(t))
        }
    }
}