package ru.kima.sonar.server.data.market.marketdata.util

import ru.kima.sonar.common.util.SonarResult
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

class CachedValue<T>(
    private val cacheLifetime: Duration,
    private val updateMethod: suspend () -> T
) {
    private var cacheUpdated: Instant = Clock.System.now()
    private var cachedValue: T? = null

    suspend fun getValue(): SonarResult<T, Throwable> {
        if (cachedValue == null ||
            (Clock.System.now() - cacheUpdated) > cacheLifetime
        ) {
            try {
                cachedValue = updateMethod()
            } catch (e: Exception) {
                cachedValue?.let { return SonarResult.Success(it) }
                return SonarResult.Error(e)
            }
        }

        cachedValue?.let { return SonarResult.Success(it) }
        return SonarResult.Error(NullPointerException())
    }
}