package ru.kima.sonar.server.data.market.marketdata.local.indicators

import org.ta4j.core.Indicator
import org.ta4j.core.num.Num
import java.math.BigDecimal

const val DEFAULT_BAR_COUNT = 14
const val STOCHASTIC_SMOOTHING_STEPS = 3

fun Indicator<Num>.lastDecimal(): BigDecimal {
    return getValue(barSeries.endIndex).bigDecimalValue()
}

fun Indicator<Num>.lastDouble() = getValue(barSeries.endIndex).doubleValue()