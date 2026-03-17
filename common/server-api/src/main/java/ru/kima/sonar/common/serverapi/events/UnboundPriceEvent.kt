package ru.kima.sonar.common.serverapi.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.events.model.Indicators
import ru.kima.sonar.common.serverapi.model.LastPrice

@Serializable
@SerialName("UnboundPriceEvent")
data class UnboundPriceEvent(
    val portfolioId: Long,
    val portfolioName: String,
    val securityName: String,
    val indicators: Indicators,
    val lastPrice: LastPrice,
    val priceType: PriceType,
    val note: String
) : NotificationEvent {
    enum class PriceType {
        ABOVE, BELOW
    }
}