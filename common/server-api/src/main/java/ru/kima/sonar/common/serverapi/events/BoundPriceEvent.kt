package ru.kima.sonar.common.serverapi.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.events.model.Indicators
import ru.kima.sonar.common.serverapi.model.LastPrice
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
@SerialName("bound_price_event")
data class BoundPriceEvent(
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
        @SerialName("all")
        data class All(
            val lowDeviation: BigDecimalJson,
            val highDeviation: BigDecimalJson,
        ) : PriceType

        @Serializable
        @SerialName("high")
        data class High(val deviation: BigDecimalJson) : PriceType

        @Serializable
        @SerialName("low")
        data class Low(val deviation: BigDecimalJson) : PriceType
    }
}