package ru.kima.sonar.common.serverapi.model.rules

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
sealed interface SimpleIndicatorRule : Rule {
    val requiredCount: Int
    val lowThreshold: BigDecimalJson
    val highThreshold: BigDecimalJson

    val defaultLowThreshold
        get() = 30.0
    val defaultHighThreshold
        get() = 70.0
}