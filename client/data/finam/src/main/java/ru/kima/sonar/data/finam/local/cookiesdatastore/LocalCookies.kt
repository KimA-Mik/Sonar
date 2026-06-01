package ru.kima.sonar.data.finam.local.cookiesdatastore

import kotlinx.serialization.Serializable

@Serializable
internal data class LocalCookies(
    val cookies: List<CookieWithTimestamp>,
    val oldestCookie: Long
) {
    companion object {
        fun default(
            cookies: List<CookieWithTimestamp> = emptyList(),
            oldestCookie: Long = 0L
        ) = LocalCookies(
            cookies = cookies,
            oldestCookie = oldestCookie
        )
    }
}
