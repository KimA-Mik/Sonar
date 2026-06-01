package ru.kima.sonar.data.finam.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Error(
    @SerialName("msg") val message: String,
    val code: String
)

@Serializable
internal data class FinamResponse(
    val html: String,
    val error: Error
)