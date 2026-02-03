package ru.kima.sonar.data.applicationconfig.local.model

import kotlinx.serialization.Serializable

@Serializable
data class LocalConfig(
    val apiUrl: String,
    val apiAccessToken: String?
)