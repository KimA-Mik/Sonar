package ru.kima.sonar.common.serverapi.events.model

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson


@Serializable
data class Indicators(
    val min15Rsi: BigDecimalJson,
    val hourlyRsi: BigDecimalJson,
    val hour4Rsi: BigDecimalJson,
    val dailyRsi: BigDecimalJson,
    val min15bb: BollingerBandsData,
    val hourlyBb: BollingerBandsData,
    val hour4Bb: BollingerBandsData,
    val dailyBb: BollingerBandsData,
    val min15Mfi: BigDecimalJson,
    val hourlyMfi: BigDecimalJson,
    val hour4Mfi: BigDecimalJson,
    val dailyMfi: BigDecimalJson,
    val min15Srsi: BigDecimalJson,
    val hourlySrsi: BigDecimalJson,
    val hour4Srsi: BigDecimalJson,
    val dailySrsi: BigDecimalJson,
)
