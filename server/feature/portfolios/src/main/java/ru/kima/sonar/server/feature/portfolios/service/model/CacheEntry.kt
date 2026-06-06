package ru.kima.sonar.server.feature.portfolios.service.model

import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.adx.ADXIndicator
import org.ta4j.core.indicators.averages.SMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.volume.MoneyFlowIndexIndicator
import ru.kima.sonar.server.feature.portfolios.techanalysis.BollingerBands
import ru.kima.sonar.server.feature.portfolios.util.lastDouble

class CacheEntry(
    private val min15RsiIndicator: RSIIndicator,
    private val hourlyRsiIndicator: RSIIndicator,
    private val hour4RsiIndicator: RSIIndicator,
    private val dailyRsiIndicator: RSIIndicator,
    private val min15Close: ClosePriceIndicator,
    private val hourlyClose: ClosePriceIndicator,
    private val hour4Close: ClosePriceIndicator,
    private val dailyClose: ClosePriceIndicator,
    private val min15MfiIndicator: MoneyFlowIndexIndicator,
    private val hourlyMfiIndicator: MoneyFlowIndexIndicator,
    private val hour4MfiIndicator: MoneyFlowIndexIndicator,
    private val dailyMfiIndicator: MoneyFlowIndexIndicator,
    private val min15SrsiIndicator: SMAIndicator,
    private val hourlySrsiIndicator: SMAIndicator,
    private val hour4SrsiIndicator: SMAIndicator,
    private val dailySrsiIndicator: SMAIndicator,
    private val min15AdxIndicator: ADXIndicator,
    private val hourlyAdxIndicator: ADXIndicator,
    private val hour4AdxIndicator: ADXIndicator,
    private val dailyAdxIndicator: ADXIndicator,
) {
    val min15Rsi: Double by lazy { min15RsiIndicator.lastDouble() }
    val hourlyRsi: Double by lazy { hourlyRsiIndicator.lastDouble() }
    val hour4Rsi: Double by lazy { hour4RsiIndicator.lastDouble() }
    val dailyRsi: Double by lazy { dailyRsiIndicator.lastDouble() }
    val min15bb: BollingerBands.BollingerBandsData by lazy { BollingerBands.calculate(min15Close) }
    val hourlyBb: BollingerBands.BollingerBandsData by lazy { BollingerBands.calculate(hourlyClose) }
    val hour4Bb: BollingerBands.BollingerBandsData by lazy { BollingerBands.calculate(hour4Close) }
    val dailyBb: BollingerBands.BollingerBandsData by lazy { BollingerBands.calculate(dailyClose) }
    val min15Mfi: Double by lazy { min15MfiIndicator.lastDouble() }
    val hourlyMfi: Double by lazy { hourlyMfiIndicator.lastDouble() }
    val hour4Mfi: Double by lazy { hour4MfiIndicator.lastDouble() }
    val dailyMfi: Double by lazy { dailyMfiIndicator.lastDouble() }
    val min15Srsi: Double by lazy { min15SrsiIndicator.lastDouble() }
    val hourlySrsi: Double by lazy { hourlySrsiIndicator.lastDouble() }
    val hour4Srsi: Double by lazy { hour4SrsiIndicator.lastDouble() }
    val dailySrsi: Double by lazy { dailySrsiIndicator.lastDouble() }
    val min15Adx: Double by lazy { min15AdxIndicator.lastDouble() }
    val hourlyAdx: Double by lazy { hourlyAdxIndicator.lastDouble() }
    val hour4Adx: Double by lazy { hour4AdxIndicator.lastDouble() }
    val dailyAdx: Double by lazy { dailyAdxIndicator.lastDouble() }
}
