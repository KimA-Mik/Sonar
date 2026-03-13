package ru.kima.sonar.server.feature.portfolios.techanalysis

import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.averages.SMAIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator
import ru.kima.sonar.server.feature.portfolios.util.MathUtil
import ru.kima.sonar.server.feature.portfolios.util.lastDecimal
import java.math.BigDecimal

object BollingerBands {
    fun calculate(series: BarSeries, barsCount: Int = MathUtil.BOLLINGER_BARS_COUNT) =
        calculate(ClosePriceIndicator(series), barsCount)

    fun calculate(
        closes: ClosePriceIndicator,
        barsCount: Int = MathUtil.BOLLINGER_BARS_COUNT
    ): BollingerBandsData {
        val middle = BollingerBandsMiddleIndicator(SMAIndicator(closes, barsCount))

        val deviation = StandardDeviationIndicator(closes, barsCount)
        val lower = BollingerBandsLowerIndicator(middle, deviation)
        val upper = BollingerBandsUpperIndicator(middle, deviation)

        return BollingerBandsData(
            lower = lower.lastDecimal(),
            middle = middle.lastDecimal(),
            upper = upper.lastDecimal()
        )
    }

    data class BollingerBandsData(
        val lower: BigDecimal,
        val middle: BigDecimal,
        val upper: BigDecimal
    )
}