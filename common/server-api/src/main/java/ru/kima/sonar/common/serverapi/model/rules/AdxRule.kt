package ru.kima.sonar.common.serverapi.model.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
@SerialName("adx")
data class AdxRule(
    override val requiredCount: Int,
    override val lowThreshold: BigDecimalJson,
    override val highThreshold: BigDecimalJson,
) : SimpleIndicatorRule {
    override val defaultHighThreshold
        get() = 75.0f
    override val defaultLowThreshold
        get() = 25.0f
}