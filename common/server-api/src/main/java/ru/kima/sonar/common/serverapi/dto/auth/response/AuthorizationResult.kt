package ru.kima.sonar.common.serverapi.dto.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationResult(
    val token: String
)