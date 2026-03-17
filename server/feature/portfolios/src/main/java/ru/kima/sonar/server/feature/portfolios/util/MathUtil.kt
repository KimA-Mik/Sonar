package ru.kima.sonar.server.feature.portfolios.util

import ru.kima.sonar.common.util.BigDecimalUtil
import ru.kima.sonar.server.feature.portfolios.techanalysis.BollingerBands
import java.math.BigDecimal
import kotlin.math.abs

object MathUtil {
    const val PRICE_ZERO = 0.0
    const val EPSILON = 1e-5

    const val RSI_LOW = 29.0
    const val RSI_HIGH = 71.0
    const val BB_FOR_RSI_HIGH = 80.0
    const val BB_FOR_RSI_LOW = 20.0
    fun isRsiCritical(rsi: Double, low: Double = RSI_LOW, high: Double = RSI_HIGH) =
        rsi <= low || rsi >= high

    const val BOLLINGER_BARS_COUNT = 20
    const val BB_CRITICAL_HIGH = 0.95
    const val BB_CRITICAL_LOW = 0.05
    const val RSI_FOR_BB_HIGH = 69.0
    const val RSI_FOR_BB_LOW = 31.0
    fun isBbCritical(
        value: Double,
        bb: BollingerBands.BollingerBandsData,
        lowPercent: Double = BB_CRITICAL_LOW,
        highPercent: Double = BB_CRITICAL_HIGH
    ): Boolean {
        if (bb.upper == bb.lower) return false // avoid division by zero
        val percent = (value - bb.lower) / (bb.upper - bb.lower)
        return percent <= lowPercent || percent >= highPercent
    }

    const val MFI_LOW = 20.0
    const val MFI_HIGH = 80.0
    fun isMfiCritical(mfi: Double, low: Double = MFI_LOW, high: Double = MFI_HIGH) =
        mfi <= low || mfi >= high

    const val SRSI_LOW = 20.0
    const val SRSI_HIGH = 80.0
    const val BB_FOR_SRSI_HIGH = 80.0
    const val BB_FOR_SRSI_LOW = 20.0
    fun isSrsiCritical(srsi: Double, low: Double = SRSI_LOW, high: Double = SRSI_HIGH) =
        srsi <= low || srsi >= high

    fun absolutePercentageDifference(oldValue: BigDecimal, newValue: BigDecimal): BigDecimal {
        if ((oldValue + newValue).compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO
        //https://www.calculatorsoup.com/calculators/algebra/percent-difference-calculator.php
        return (newValue - oldValue).abs() * BigDecimalUtil.TWO / (newValue + oldValue) * BigDecimalUtil.HUNDRED
    }

    fun anotherAbsolutePercentageDifference(oldValue: Double, newValue: Double): Double {
        if (oldValue == 0.0) {
            return 0.0
        }
        return abs((newValue - oldValue) / oldValue) * 100
    }
}

