package ru.kima.sonar.common.serverapi.model.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("group")
data class GroupRule(
    val truthThreshold: Int,
    val rules: List<Rule>
) : Rule