package ru.kima.sonar.common.serverapi.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.events.model.Indicators
import ru.kima.sonar.common.serverapi.model.LastPrice
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

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
    @Serializable
    sealed interface PriceType {
        @Serializable
        @SerialName("Above")
        data class Above(val price: BigDecimalJson) : PriceType

        @Serializable
        @SerialName("Below")
        data class Below(val price: BigDecimalJson) : PriceType
    }
}