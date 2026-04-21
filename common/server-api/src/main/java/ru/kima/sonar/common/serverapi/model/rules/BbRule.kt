package ru.kima.sonar.common.serverapi.model.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
@SerialName("bb")
data class BbRule(
    override val requiredCount: Int,
    override val lowThreshold: BigDecimalJson,
    override val highThreshold: BigDecimalJson,
) : SimpleIndicatorRule {
    override val defaultHighThreshold
        get() = 90.0f
    override val defaultLowThreshold
        get() = 10.0f
}