package ru.kima.sonar.common.serverapi.dto.indicaators

import kotlinx.serialization.Serializable

@Serializable
class IndicatorDataPoint(
    val value: Double,
    val time: Long
)