package ru.kima.sonar.server.feature.portfolios.service.rules

import ru.kima.sonar.common.serverapi.model.rules.BbRule
import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.MfiRule
import ru.kima.sonar.common.serverapi.model.rules.RsiRule
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.SimpleIndicatorRule
import ru.kima.sonar.common.serverapi.model.rules.SrsiRule
import ru.kima.sonar.common.util.MathUtil
import ru.kima.sonar.server.feature.portfolios.service.model.CacheEntry
import java.math.BigDecimal

fun Rule.execute(currentPrice: Double, cacheEntry: CacheEntry): Boolean {
    return when (this) {
        is GroupRule -> execute(currentPrice, cacheEntry)
        is SimpleIndicatorRule -> execute(currentPrice, cacheEntry)
    }
}

fun GroupRule.execute(currentPrice: Double, cacheEntry: CacheEntry): Boolean {
    return rules.count { it.execute(currentPrice, cacheEntry) } >= truthThreshold
}

fun SimpleIndicatorRule.execute(currentPrice: Double, cacheEntry: CacheEntry): Boolean {
    val array = when (this) {
        is BbRule -> listOf(
            BigDecimal(
                MathUtil.bbPercent(
                    currentPrice,
                    cacheEntry.min15bb.lower,
                    cacheEntry.min15bb.upper
                )
            ),
            BigDecimal(
                MathUtil.bbPercent(
                    currentPrice,
                    cacheEntry.hourlyBb.lower,
                    cacheEntry.hourlyBb.upper
                )
            ),
            BigDecimal(
                MathUtil.bbPercent(
                    currentPrice,
                    cacheEntry.hour4Bb.lower,
                    cacheEntry.hour4Bb.upper
                )
            ),
            BigDecimal(
                MathUtil.bbPercent(
                    currentPrice,
                    cacheEntry.dailyBb.lower,
                    cacheEntry.dailyBb.upper
                )
            )
        )

        is MfiRule -> listOf(
            BigDecimal(cacheEntry.min15Mfi),
            BigDecimal(cacheEntry.hourlyMfi),
            BigDecimal(cacheEntry.hour4Mfi),
            BigDecimal(cacheEntry.dailyMfi)
        )

        is RsiRule -> listOf(
            BigDecimal(cacheEntry.min15Rsi),
            BigDecimal(cacheEntry.hourlyRsi),
            BigDecimal(cacheEntry.hour4Rsi),
            BigDecimal(cacheEntry.dailyRsi)
        )

        is SrsiRule -> listOf(
            BigDecimal(cacheEntry.min15Srsi),
            BigDecimal(cacheEntry.hourlySrsi),
            BigDecimal(cacheEntry.hour4Srsi),
            BigDecimal(cacheEntry.dailySrsi)
        )
    }

    return array.count { it >= this.highThreshold || it <= this.lowThreshold } >= this.requiredCount
}