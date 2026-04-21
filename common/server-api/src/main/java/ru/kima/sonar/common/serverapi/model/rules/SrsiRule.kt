package ru.kima.sonar.common.serverapi.model.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson


@Serializable
@SerialName("srsi")
data class SrsiRule(
    override val requiredCount: Int,
    override val lowThreshold: BigDecimalJson,
    override val highThreshold: BigDecimalJson,
) : SimpleIndicatorRule
