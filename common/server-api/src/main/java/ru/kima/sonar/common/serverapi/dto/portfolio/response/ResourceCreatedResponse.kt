package ru.kima.sonar.common.serverapi.dto.portfolio.response

import kotlinx.serialization.Serializable

@Serializable
data class ResourceCreatedResponse(
    val id: Long
)