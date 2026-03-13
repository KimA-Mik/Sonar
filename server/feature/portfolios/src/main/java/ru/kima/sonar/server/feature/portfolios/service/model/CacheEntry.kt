package ru.kima.sonar.server.feature.portfolios.service.model

import ru.kima.sonar.server.feature.portfolios.techanalysis.BollingerBands
import java.math.BigDecimal

data class CacheEntry(
    val min15Rsi: BigDecimal,
    val hourlyRsi: BigDecimal,
    val hour4Rsi: BigDecimal,
    val dailyRsi: BigDecimal,
    val min15bb: BollingerBands.BollingerBandsData,
    val hourlyBb: BollingerBands.BollingerBandsData,
    val hour4Bb: BollingerBands.BollingerBandsData,
    val dailyBb: BollingerBands.BollingerBandsData,
    val min15Mfi: BigDecimal,
    val hourlyMfi: BigDecimal,
    val hour4Mfi: BigDecimal,
    val dailyMfi: BigDecimal,
    val min15Srsi: BigDecimal,
    val hourlySrsi: BigDecimal,
    val hour4Srsi: BigDecimal,
    val dailySrsi: BigDecimal,
)
