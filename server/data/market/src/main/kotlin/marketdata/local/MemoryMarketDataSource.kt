package ru.kima.sonar.server.data.market.marketdata.local

import com.google.common.collect.Comparators.min
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import org.ta4j.core.Bar
import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBarSeriesBuilder
import org.ta4j.core.ConcurrentBarSeriesBuilder
import org.ta4j.core.bars.TimeBarBuilder
import org.ta4j.core.num.DoubleNum
import org.ta4j.core.num.DoubleNumFactory
import ru.kima.sonar.common.serverapi.model.CandleInterval
import ru.kima.sonar.common.serverapi.model.HistoricCandle
import ru.kima.sonar.common.serverapi.model.schema.CandleSource
import ru.kima.sonar.common.util.valueOr
import ru.kima.sonar.server.data.market.marketdata.local.consumer.calculateCandleTime
import ru.kima.sonar.server.data.market.marketdata.local.consumer.newDoubleBar
import ru.kima.sonar.server.data.market.marketdata.local.consumer.validIntervals
import ru.kima.sonar.server.data.market.marketdata.local.indicators.Indicators
import ru.kima.sonar.server.data.market.marketdata.remote.TinkoffDataSource
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinInstant

internal class MemoryMarketDataSource(
    private val tinkoffDataSource: TinkoffDataSource
) : LocalDataSource {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val graphs: MutableMap<Key, GraphEntry> =
        mutableMapOf<Key, GraphEntry>().withDefault { GraphEntry() }
    private val indicators = mutableMapOf<Key, Indicators>()
    override suspend fun getCandles(
        ticker: String,
        interval: CandleInterval
    ): BarSeries? {
        val key = ticker with interval
        val entry = graphs[key] ?: return null
        if (!entry.initialized) {
            if (!entry.initialize(key)) return null
        }

        return entry.barSeries
    }

    override suspend fun getIndicators(
        ticker: String,
        interval: CandleInterval
    ): Indicators? {
        val key = ticker with interval
        val entry = graphs[key] ?: return null
        if (!entry.initialized) {
            if (!entry.initialize(key)) return null
        }

        return indicators[key]
    }

    override suspend fun onTick(
        ticker: String,
        price: Double,
        time: Instant
    ) {
        for (interval in validIntervals) {
            val candleTime = calculateCandleTime(time, interval)
            val key = ticker with interval
            val entry = graphs.getOrPut(key) { GraphEntry() }
            if (entry.barSeries.isEmpty ||
                entry.barSeries.lastBar.endTime.toKotlinInstant() < candleTime
            ) {
                val bar = newDoubleBar(
                    interval = interval,
                    candleTime = candleTime,
                    price = price
                )
                entry.barSeries.addBar(bar)
            } else {
                entry.barSeries.addTrade(DoubleNum.ONE, DoubleNum.valueOf(price))
            }
        }
    }

    private fun HistoricCandle.toBar(duration: Duration): Bar =
        TimeBarBuilder(DoubleNumFactory.getInstance())
            .timePeriod(duration.toJavaDuration())
            .openPrice(open)
            .highPrice(high)
            .lowPrice(low)
            .closePrice(close)
            .volume(volume)
            .build()


    private infix fun String.with(that: CandleInterval): Key = Key(this, that)
    private data class Key(val ticker: String, val interval: CandleInterval)
    private inner class GraphEntry(
        var barSeries: BarSeries = ConcurrentBarSeriesBuilder()
            .withNumFactory(DoubleNumFactory.getInstance())
            .build(),
    ) {
        private val mutex = Mutex()
        var initialized: Boolean = false
            private set

        suspend fun initialize(
            key: Key
        ): Boolean = mutex.withLock {
            if (initialized) return true
            val security = tinkoffDataSource.findSecurity(key.ticker) ?: return@withLock false
            //TODO: Report error if security is null
            val windowSize = key.interval.duration * key.interval.limit
            val fetchEnd = if (!barSeries.isEmpty) {
                barSeries.lastBar.endTime.toKotlinInstant()
            } else {
                Clock.System.now()
            }
            var cursorStart = security.firstTradeDate
            var cursorEnd = min(cursorStart + windowSize, fetchEnd)
            val bars = mutableListOf<Bar>()

            while (cursorStart < fetchEnd) {
                val candles = tinkoffDataSource.getCandles(
                    security.uid,
                    cursorStart,
                    cursorEnd,
                    key.interval,
                    CandleSource.INCLUDE_WEEKEND
                ).valueOr {
                    logger.error("Unable to fetch candles $it")
                    return@withLock false
                }

                bars += candles.map { it.toBar(key.interval.duration) }
                cursorStart = cursorEnd
                cursorEnd = min(cursorEnd + windowSize, fetchEnd)
            }

            val series = BaseBarSeriesBuilder()
                .withNumFactory(DoubleNumFactory.getInstance())
                .build()

            for (bar in bars) series.addBar(bar)
            for (i in 1 until barSeries.barCount) {
                series.addBar(barSeries.getBar(i))
            }

            if (series.isEmpty) return@withLock false
            indicators[key] = Indicators(series)
            barSeries = series
            initialized = true
            return@withLock true
        }
    }
}