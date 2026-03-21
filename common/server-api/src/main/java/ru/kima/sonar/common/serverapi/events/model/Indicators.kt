package ru.kima.sonar.common.serverapi.events.model

import kotlinx.serialization.Serializable


@Serializable
data class Indicators(
    val min15Rsi: Double,
    val hourlyRsi: Double,
    val hour4Rsi: Double,
    val dailyRsi: Double,
    val min15bb: BollingerBandsData,
    val hourlyBb: BollingerBandsData,
    val hour4Bb: BollingerBandsData,
    val dailyBb: BollingerBandsData,
    val min15Mfi: Double,
    val hourlyMfi: Double,
    val hour4Mfi: Double,
    val dailyMfi: Double,
    val min15Srsi: Double,
    val hourlySrsi: Double,
    val hour4Srsi: Double,
    val dailySrsi: Double,
)
