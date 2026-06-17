package ru.kima.sonar.server.data.market.marketdata.local.consumer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.kima.sonar.common.serverapi.model.Candle
import ru.kima.sonar.common.serverapi.model.CandleInterval
import ru.kima.sonar.server.data.market.marketdata.local.LocalDataSource
import java.math.BigDecimal
import kotlin.time.Instant

internal class CandleConsumer(
    private val dataSource: LocalDataSource,
    private val coroutineScope: CoroutineScope,
    private val ioScope: CoroutineScope
) {
    private val activeCandles = mutableMapOf<String, MutableMap<CandleInterval, MutableCandle>>()

    private val eventQueue = MutableSharedFlow<ConsumerEvent>(extraBufferCapacity = 1024)
    private val updateQueue = MutableSharedFlow<MutableCandle>(extraBufferCapacity = 512)
    suspend fun consume(event: ConsumerEvent) {
        eventQueue.emit(event)
    }

    init {
        coroutineScope.launch {
            eventQueue.collect { event ->
                processEvent(event)
            }
        }
        coroutineScope.launch {
            updateQueue.collect { mutableCandle ->
                val old = dataSource.getCandle(
                    ticker = mutableCandle.ticker,
                    interval = mutableCandle.interval,
                    time = mutableCandle.time
                )
                if (old == null) {
                    val newCandle = mutableCandle.toCandle()
                    dataSource.saveCandle(newCandle)
                } else {
                    mutableCandle.id = old.id
                    val candle = mutableCandle.toCandle()
                    dataSource.updateCandle(candle)
                }
            }
        }
    }

    private val validIntervals =
//        CandleInterval.entries.filter { it != CandleInterval.CANDLE_INTERVAL_UNSPECIFIED && it != CandleInterval.UNRECOGNIZED }
        listOf(
            CandleInterval.CANDLE_INTERVAL_15_MIN,
            CandleInterval.CANDLE_INTERVAL_HOUR,
            CandleInterval.CANDLE_INTERVAL_4_HOUR,
            CandleInterval.CANDLE_INTERVAL_DAY,
        )

    private suspend fun processEvent(event: ConsumerEvent) {
        // Get or create candle container for this ticker
        val tickerCandles = activeCandles.getOrPut(event.ticker) { mutableMapOf() }

        // Process event for each candle interval timeframe
        validIntervals.forEach { interval ->
            val candleTime = calculateCandleTime(event.time, interval)

            // Get or create mutable candle
            val mutableCandle = tickerCandles.getOrPut(interval) {
                MutableCandle.create(
                    time = candleTime,
                    ticker = event.ticker,
                    interval = interval,
                    price = event.price
                )
            }

            // Update candle if event is for the same candle time, otherwise save and create new
            if (mutableCandle.time == candleTime) {
                mutableCandle.update(event.price)
            } else {
                // Save the previous candle
                mutableCandle.isComplete = true
                updateQueue.emit(mutableCandle)

                // Create new candle for the new time
                tickerCandles[interval] = MutableCandle.create(
                    time = candleTime,
                    ticker = event.ticker,
                    interval = interval,
                    price = event.price
                )
            }
        }
    }

    private fun calculateCandleTime(time: Instant, interval: CandleInterval): Instant {
        val intervalSeconds = interval.duration.inWholeSeconds
        val timeSeconds = time.epochSeconds
        val candleStartSeconds = timeSeconds - (timeSeconds % intervalSeconds)
        return Instant.fromEpochSeconds(candleStartSeconds)
    }

    private data class MutableCandle(
        var id: Long,
        var time: Instant,
        var ticker: String,
        var interval: CandleInterval,
        var open: BigDecimal,
        var high: BigDecimal,
        var low: BigDecimal,
        var close: BigDecimal,
        var volume: Long,
        var isComplete: Boolean
    ) {
        fun update(price: BigDecimal) {
            high = maxOf(high, price)
            low = minOf(low, price)
            close = price
            volume += 1L
        }

        fun toCandle() = Candle(
            id = id,
            time = time,
            ticker = ticker,
            interval = interval,
            open = open,
            high = high,
            low = low,
            close = close,
            volume = volume,
            isComplete = isComplete
        )

        companion object {
            fun create(
                time: Instant,
                ticker: String,
                interval: CandleInterval,
                price: BigDecimal
            ) = MutableCandle(
                id = 0L,
                time = time,
                ticker = ticker,
                interval = interval,
                open = price,
                high = price,
                low = price,
                close = price,
                volume = 1L,
                isComplete = false
            )
        }
    }
}