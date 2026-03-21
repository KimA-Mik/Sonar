package ru.kima.sonar.server.feature.portfolios.util

import org.ta4j.core.Indicator
import org.ta4j.core.num.Num
import java.math.BigDecimal

fun Indicator<Num>.lastDecimal(): BigDecimal {
    return getValue(barSeries.endIndex).bigDecimalValue()
}

fun Indicator<Num>.lastDouble() = getValue(barSeries.endIndex).doubleValue()