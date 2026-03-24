package ru.kima.sonar.common.serverapi.clientrequests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val password: String
)
