package ru.kima.sonar.data.finam.remote.cookies

import android.content.Context
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.fillDefaults
import io.ktor.client.plugins.cookies.matches
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.kima.sonar.data.finam.local.cookiesdatastore.CookieWithTimestamp
import ru.kima.sonar.data.finam.local.cookiesdatastore.cookiesDataStore
import kotlin.math.min

internal class LocalCookieStorage(
    context: Context,
    private val clock: () -> Long = { getTimeMillis() },
) : CookiesStorage {
    private val dataStore = context.cookiesDataStore
    private val mutex = Mutex()

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        val storage = dataStore.data.firstOrNull() ?: return@withLock emptyList()
        val now = clock()
        val cookies = if (now >= storage.oldestCookie) cleanup(now)
        else storage.cookies

        return@withLock cookies.filter { it.cookie.matches(requestUrl) }.map { it.cookie }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        with(cookie) {
            if (name.isBlank()) return
        }

        mutex.withLock {
            val storage = dataStore.data.firstOrNull() ?: return@withLock
            val container = storage.cookies.toMutableList()

            container.removeAll { (existingCookie, _) ->
                existingCookie.name == cookie.name && existingCookie.matches(requestUrl)
            }
            val createdAt = clock()
            container.add(CookieWithTimestamp(cookie.fillDefaults(requestUrl), createdAt))

            var oldestCookie = storage.oldestCookie
            cookie.maxAgeOrExpires(createdAt)?.let {
                if (oldestCookie > it) {
                    oldestCookie = it
                }
            }

            dataStore.updateData {
                it.copy(
                    cookies = container,
                    oldestCookie = oldestCookie
                )
            }
        }
    }


    override fun close() {
    }

    private suspend fun cleanup(timestamp: Long): List<CookieWithTimestamp> {
        val storage = dataStore.data.firstOrNull() ?: return emptyList()
        val container = storage.cookies.toMutableList()

        container.removeAll { (cookie, createdAt) ->
            val expires = cookie.maxAgeOrExpires(createdAt) ?: return@removeAll false
            expires < timestamp
        }

        val newOldest = container.fold(Long.MAX_VALUE) { acc, (cookie, createdAt) ->
            cookie.maxAgeOrExpires(createdAt)?.let { min(acc, it) } ?: acc
        }

        dataStore.updateData {
            it.copy(
                cookies = container,
                oldestCookie = newOldest
            )
        }

        return container
    }

    private fun Cookie.maxAgeOrExpires(createdAt: Long): Long? =
        maxAge?.let { createdAt + it * 1000L } ?: expires?.timestamp
}