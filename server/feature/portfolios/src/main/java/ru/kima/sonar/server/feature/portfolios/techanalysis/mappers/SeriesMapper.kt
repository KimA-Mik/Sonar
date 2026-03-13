package ru.kima.sonar.server.feature.portfolios.techanalysis.mappers

import org.ta4j.core.BarSeries
import org.ta4j.core.BaseBarSeriesBuilder
import org.ta4j.core.bars.TimeBarBuilder
import org.ta4j.core.num.DecimalNumFactory
import ru.kima.sonar.common.serverapi.model.HistoricCandle
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlin.time.toJavaInstant

fun List<HistoricCandle>.toSeries(
    duration: Duration,
    seriesName: String = ""
): BarSeries {
    val series = BaseBarSeriesBuilder()
        .withNumFactory(DecimalNumFactory.getInstance())
        .withName(seriesName).build()
    val barBuilder = TimeBarBuilder()
    for (candle in this) {
        val bar = barBuilder
            .timePeriod(duration.toJavaDuration())
            .endTime(candle.time.toJavaInstant())
            .openPrice(candle.open)
            .highPrice(candle.high)
            .lowPrice(candle.low)
            .closePrice(candle.close)
            .volume(candle.volume)
            .build()

        series.addBar(bar)
    }

    return series
}