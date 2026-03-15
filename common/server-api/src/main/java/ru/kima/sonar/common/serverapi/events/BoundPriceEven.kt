package ru.kima.sonar.common.serverapi.events

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
class BoundPriceEven : NotificationEvent {
    @Serializable
    sealed interface PriceType {
        @Serializable
        data class All(
            val lowDeviation: BigDecimalJson,
            val highDeviation: BigDecimalJson,
        ) : PriceType

        @Serializable
        data class High(val deviation: BigDecimalJson) : PriceType

        @Serializable
        data class Low(val deviation: BigDecimalJson) : PriceType
    }
}