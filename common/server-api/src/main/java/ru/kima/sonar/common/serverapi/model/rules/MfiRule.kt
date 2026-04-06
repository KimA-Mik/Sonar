package ru.kima.sonar.common.serverapi.model.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
@SerialName("mfi")
data class MfiRule(
    override val requiredCount: Int,
    override val lowThreshold: BigDecimalJson,
    override val highThreshold: BigDecimalJson,
) : SimpleIndicatorRule {
    override val defaultHighThreshold: Double
        get() = 80.0
    override val defaultLowThreshold: Double
        get() = 20.0
}