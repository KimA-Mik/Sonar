package ru.kima.sonar.server.feature.portfolios.service.model

import ru.kima.sonar.server.feature.portfolios.techanalysis.BollingerBands

data class CacheEntry(
    val min15Rsi: Double,
    val hourlyRsi: Double,
    val hour4Rsi: Double,
    val dailyRsi: Double,
    val min15bb: BollingerBands.BollingerBandsData,
    val hourlyBb: BollingerBands.BollingerBandsData,
    val hour4Bb: BollingerBands.BollingerBandsData,
    val dailyBb: BollingerBands.BollingerBandsData,
    val min15Mfi: Double,
    val hourlyMfi: Double,
    val hour4Mfi: Double,
    val dailyMfi: Double,
    val min15Srsi: Double,
    val hourlySrsi: Double,
    val hour4Srsi: Double,
    val dailySrsi: Double,
)
