package ru.kima.sonar.common.serverapi.events.model

import kotlinx.serialization.Serializable

@Serializable
data class BollingerBandsData(
    val lower: Double,
    val middle: Double,
    val upper: Double
)
