package ru.kima.sonar.common.serverapi.model.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RulesMode {
    @SerialName("rules_only")
    RULES_ONLY,

    @SerialName("securities_only")
    LIMIT_SECURITIES,

    @SerialName("rules_and_securities")
    RULES_AND_SECURITIES
}