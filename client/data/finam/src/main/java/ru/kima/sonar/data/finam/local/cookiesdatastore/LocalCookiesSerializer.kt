package ru.kima.sonar.data.finam.local.cookiesdatastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalSerializationApi::class)
internal object LocalCookiesSerializer : Serializer<LocalCookies> {
    override val defaultValue: LocalCookies
        get() = LocalCookies.default()

    override suspend fun readFrom(input: InputStream): LocalCookies = try {
        withContext(Dispatchers.IO) {
            return@withContext ProtoBuf.decodeFromByteArray<LocalCookies>(
                input.readBytes()
            )
        }
    } catch (exception: SerializationException) {
        throw CorruptionException("Cannot read proto.", exception)
    }

    override suspend fun writeTo(t: LocalCookies, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(ProtoBuf.encodeToByteArray(t))
        }
    }
}