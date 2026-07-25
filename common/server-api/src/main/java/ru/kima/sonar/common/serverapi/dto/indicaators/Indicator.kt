package ru.kima.sonar.common.serverapi.dto.indicaators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Indicator {
    @Serializable
    @SerialName("rsi")
    data class Rsi(
        val data: List<IndicatorDataPoint>
    ) : Indicator

    @Serializable
    @SerialName("mfi")
    data class Mfi(
        val data: List<IndicatorDataPoint>
    ) : Indicator

    @Serializable
    @SerialName("srsi")
    data class Srsi(
        val data: List<IndicatorDataPoint>
    ) : Indicator

    @Serializable
    @SerialName("adx")
    data class Adx(
        val data: List<IndicatorDataPoint>
    ) : Indicator

    @Serializable
    @SerialName("bollinger_bands")
    data class BollingerBands(
        val data: List<BollingerBandDataPoint>
    ) : Indicator
}