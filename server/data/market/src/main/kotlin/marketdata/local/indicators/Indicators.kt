package ru.kima.sonar.server.data.market.marketdata.local.indicators

import org.ta4j.core.BarSeries
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.StochasticRSIIndicator
import org.ta4j.core.indicators.adx.ADXIndicator
import org.ta4j.core.indicators.averages.SMAIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator
import org.ta4j.core.indicators.volume.MoneyFlowIndexIndicator

class Indicators(
    series: BarSeries
) {
    private val closeIndicator: ClosePriceIndicator = ClosePriceIndicator(series)
    private val rsiIndicator: RSIIndicator = RSIIndicator(closeIndicator, DEFAULT_BAR_COUNT)
    private val mfiIndicator: MoneyFlowIndexIndicator =
        MoneyFlowIndexIndicator(series, DEFAULT_BAR_COUNT)
    private val srsiIndicator: SMAIndicator = SMAIndicator(
        StochasticRSIIndicator(rsiIndicator, DEFAULT_BAR_COUNT),
        STOCHASTIC_SMOOTHING_STEPS
    )
    private val adxIndicator: ADXIndicator = ADXIndicator(series, DEFAULT_BAR_COUNT)
    private val bollingerBandsMiddleIndicator: BollingerBandsMiddleIndicator =
        BollingerBandsMiddleIndicator(srsiIndicator)
    private val bollingerBandsLowerIndicator: BollingerBandsLowerIndicator
    private val bollingerBandsUpperIndicator: BollingerBandsUpperIndicator

    init {
        val deviation = StandardDeviationIndicator(closeIndicator, DEFAULT_BAR_COUNT)
        bollingerBandsLowerIndicator = BollingerBandsLowerIndicator(
            bollingerBandsMiddleIndicator, deviation
        )
        bollingerBandsUpperIndicator = BollingerBandsUpperIndicator(
            bollingerBandsMiddleIndicator, deviation
        )
    }

    fun rsi(): Double = rsiIndicator.lastDouble()
    fun close(): Double = closeIndicator.lastDouble()
    fun mfi(): Double = mfiIndicator.lastDouble()
    fun srsi(): Double = srsiIndicator.lastDouble()
    fun adx(): Double = adxIndicator.lastDouble()
    fun bbMiddle(): Double = bollingerBandsMiddleIndicator.lastDouble()
    fun bbLower(): Double = bollingerBandsLowerIndicator.lastDouble()
    fun bbUpper(): Double = bollingerBandsUpperIndicator.lastDouble()
}