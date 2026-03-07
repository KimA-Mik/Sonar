package ru.kima.sonar.common.util

import java.math.BigDecimal

fun validBigDecimal(input: String): Boolean = try {
    BigDecimal(input)
    true
} catch (_: Exception) {
    false
}