package ru.kima.sonar.common.serverapi.model.security

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
@SerialName("Future")
data class Future(
    override val uid: String = "",
    override val ticker: String = "",
    override val name: String = "",
    override val lot: Int = 0,
    val expirationDate: Instant,
    val basicAsset: String
) : Security
