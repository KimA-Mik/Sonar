package ru.kima.sonar.data.applicationconfig.updateconfig.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateConfig(
    val providerUpdate: ProviderUpdate?
)