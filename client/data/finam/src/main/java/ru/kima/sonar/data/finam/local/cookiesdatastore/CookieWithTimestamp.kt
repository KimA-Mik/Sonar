package ru.kima.sonar.data.finam.local.cookiesdatastore

import io.ktor.http.Cookie
import kotlinx.serialization.Serializable

@Serializable
internal data class CookieWithTimestamp(
    val cookie: Cookie, val createdAt: Long
)