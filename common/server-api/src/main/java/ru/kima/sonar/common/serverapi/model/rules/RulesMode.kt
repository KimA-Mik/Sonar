package ru.kima.sonar.common.serverapi.model.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RulesMode {
    /**
     * Rules are disabled, securities notifications work as usual
     */
    @SerialName("disabled")
    RULES_DISABLED,

    /**
     * Rules are used to limit securities notifications
     */
    @SerialName("limit")
    LIMIT_SECURITIES,

    /**
     * Only rule notifications
     */
    @SerialName("rules_notifications")
    RULES_NOTIFICATIONS,

    /**
     * Enables two type of notifications
     */
    @SerialName("rules_and_securities")
    RULES_AND_SECURITIES
}