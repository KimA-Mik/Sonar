package ru.kima.sonar.feature.notifications.notifications

import ru.kima.sonar.common.util.MathUtil

internal object PresentationUtil {
    const val GREEN = "🟢"
    const val RED = "🔴"
    const val YELLOW = "🟡"

    fun rsiColor(
        value: Double,
        low: Double = MathUtil.RSI_LOW,
        high: Double = MathUtil.RSI_HIGH
    ): String {
        return when {
            value >= high -> RED
            value <= low -> GREEN
            else -> YELLOW
        }
    }

    fun markupBbColor(
        value: Double,
        low: Double,
        high: Double,
        lowPercent: Double = MathUtil.BB_CRITICAL_LOW,
        highPercent: Double = MathUtil.BB_CRITICAL_HIGH
    ): String {
        if (high == low) return YELLOW // avoid division by zero
        val percent = (value - low) / (high - low)
        return when {
            percent < lowPercent -> GREEN
            percent > highPercent -> RED
            else -> YELLOW
        }
    }
}