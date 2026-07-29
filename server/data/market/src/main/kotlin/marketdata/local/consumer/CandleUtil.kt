package ru.kima.sonar.server.data.market.marketdata.local.consumer

import org.ta4j.core.Bar
import org.ta4j.core.bars.TimeBarBuilder
import org.ta4j.core.num.DecimalNumFactory
import org.ta4j.core.num.DoubleNum
import org.ta4j.core.num.DoubleNumFactory
import ru.kima.sonar.common.serverapi.model.CandleInterval
import java.math.BigDecimal
import kotlin.time.Instant
import kotlin.time.toJavaDuration
import kotlin.time.toJavaInstant

val validIntervals =
    listOf(
        CandleInterval.CANDLE_INTERVAL_15_MIN,
        CandleInterval.CANDLE_INTERVAL_HOUR,
        CandleInterval.CANDLE_INTERVAL_4_HOUR,
        CandleInterval.CANDLE_INTERVAL_DAY,
    )

fun calculateCandleTime(time: Instant, interval: CandleInterval): Instant {
    val intervalSeconds = interval.duration.inWholeSeconds
    val timeSeconds = time.epochSeconds
    val candleStartSeconds = timeSeconds - (timeSeconds % intervalSeconds)
    return Instant.fromEpochSeconds(candleStartSeconds)
}

fun newDoubleBar(
    interval: CandleInterval,
    candleTime: Instant,
    price: Double
): Bar = TimeBarBuilder(DoubleNumFactory.getInstance())
    .timePeriod(interval.duration.toJavaDuration())
    .endTime(candleTime.toJavaInstant())
    .openPrice(DoubleNum.valueOf(price))
    .highPrice(DoubleNum.valueOf(price))
    .lowPrice(DoubleNum.valueOf(price))
    .closePrice(DoubleNum.valueOf(price))
    .volume(1.0)
    .amount(price)
    .build()

fun newDecimalBar(
    interval: CandleInterval,
    candleTime: Instant,
    price: BigDecimal
): Bar = TimeBarBuilder(DecimalNumFactory.getInstance())
    .timePeriod(interval.duration.toJavaDuration())
    .endTime(candleTime.toJavaInstant())
    .openPrice(DoubleNum.valueOf(price))
    .highPrice(DoubleNum.valueOf(price))
    .lowPrice(DoubleNum.valueOf(price))
    .closePrice(DoubleNum.valueOf(price))
    .volume(1.0)
    .amount(price)
    .build()
