package ru.kima.sonar.common.serverapi.events.model

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
data class BollingerBandsData(
    val lower: BigDecimalJson,
    val middle: BigDecimalJson,
    val upper: BigDecimalJson
)
