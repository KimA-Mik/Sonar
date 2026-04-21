package ru.kima.sonar.common.serverapi.model.rules

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
sealed interface SimpleIndicatorRule : Rule {
    val requiredCount: Int
    val lowThreshold: BigDecimalJson
    val highThreshold: BigDecimalJson

    val defaultLowThreshold: Float
        get() = 30.0f
    val defaultHighThreshold: Float
        get() = 70.0f
}