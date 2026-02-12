package ru.kima.sonar.server.feature.auth.impl

import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.random.Random

internal fun generateToken(
    email: String,
    timestamp: Long = System.currentTimeMillis(),
    randomSalt: Long = Random.nextLong()
): String {
    val tokenVersion: Byte = 1
    val emailBytes = email.encodeToByteArray()
    val buffer = ByteArrayOutputStream(1 + emailBytes.size + 16)
    buffer.write(byteArrayOf(tokenVersion))
    buffer.write(emailBytes)
    buffer.writeLong(timestamp)
    buffer.writeLong(randomSalt)

    return Base64.encode(buffer.toByteArray())
}

private fun ByteArrayOutputStream.writeLong(value: Long) {
    val buff = ByteArray(8)

    for (i in 7 downTo 0) {
        buff[7 - i] = (value shr (i * 8)).toByte()
    }

    write(buff)
}