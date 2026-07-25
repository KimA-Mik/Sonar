package ru.kima.sonar.common.serverapi.dto.indicaators

import kotlinx.serialization.Serializable

@Serializable
data class BollingerBandDataPoint(
    val time: Long,
    val upperBand: Double,
    val middleBand: Double,
    val lowerBand: Double
)