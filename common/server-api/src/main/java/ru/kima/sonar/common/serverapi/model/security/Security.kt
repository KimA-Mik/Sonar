package ru.kima.sonar.common.serverapi.model.security

import kotlinx.serialization.Serializable

@Serializable
sealed interface Security {
    val uid: String
    val ticker: String
    val name: String
    val lot: Int
//    val first1MinCandleDate: Instant
//    val first1DayCandleDate: Instant
}
