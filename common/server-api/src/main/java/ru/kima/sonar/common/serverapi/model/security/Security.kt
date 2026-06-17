package ru.kima.sonar.common.serverapi.model.security

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
sealed interface Security {
    val uid: String
    val ticker: String
    val name: String
    val lot: Int
    val firstTradeDate: Instant
}
