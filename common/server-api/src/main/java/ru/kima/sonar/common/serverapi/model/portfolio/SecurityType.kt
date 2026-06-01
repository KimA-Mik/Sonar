package ru.kima.sonar.common.serverapi.model.portfolio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SecurityType {
    @SerialName("share")
    SHARE,

    @SerialName("future")
    FUTURE
}