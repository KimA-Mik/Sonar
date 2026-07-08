package ru.kima.sonar.data.applicationconfig.updateconfig.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ProviderUpdate {
    @Serializable
    @SerialName("firebase")
    data class FirebaseUpdate(val token: String) : ProviderUpdate
}