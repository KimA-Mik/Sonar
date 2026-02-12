package ru.kima.sonar.common.serverapi.serverresponse

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationResult(
    val token: String
)
