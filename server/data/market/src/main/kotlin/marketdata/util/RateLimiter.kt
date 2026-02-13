package ru.kima.sonar.server.data.market.marketdata.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

@OptIn(ExperimentalAtomicApi::class)
class RateLimiter(
    val limit: Int,
    val rateWindow: Duration
) {
    private val current = AtomicInt(0)
    private var lastWindowFrame: Instant = Clock.System.now()
    private val mutex = Mutex()

    suspend fun rateLimitedAction(action: suspend () -> Unit) {
        var now = Clock.System.now()
        if (current.load() < limit) {
            current.exchange(current.load() + 1)
            action()
        } else {
            while (current.load() >= limit) {
                val timeToDelay = rateWindow - (now - lastWindowFrame)
                if (timeToDelay.inWholeMilliseconds > 0L) {
                    delay(timeToDelay)
                    now = Clock.System.now()
                } else {
                    resetRate()
                }
            }
            current.exchange(current.load() + 1)
            action()
        }

        if (now - lastWindowFrame > rateWindow) resetRate()
    }

    suspend fun <T> rateLimitedResult(action: suspend () -> T): T {
        var res: T
        var now = Clock.System.now()
        if (current.load() < limit) {
            current.exchange(current.load() + 1)
            res = action()
        } else {
            while (current.load() >= limit) {
                val timeToDelay = rateWindow - (now - lastWindowFrame)
                if (timeToDelay.inWholeMilliseconds > 0L) {
                    delay(timeToDelay)
                    now = Clock.System.now()
                } else {
                    resetRate()
                }
            }
            current.exchange(current.load() + 1)
            res = action()
        }

        if (now - lastWindowFrame > rateWindow) resetRate()
        return res
    }

    suspend fun resetRate() {
        val now = Clock.System.now()
        if (!mutex.isLocked &&
            now - lastWindowFrame > rateWindow
        ) {
            mutex.lock()

            current.store(0)
            lastWindowFrame = Clock.System.now()

            mutex.unlock()
        }
    }
}