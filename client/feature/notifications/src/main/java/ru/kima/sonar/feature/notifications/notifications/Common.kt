package ru.kima.sonar.feature.notifications.notifications

import android.content.res.Resources
import ru.kima.sonar.common.serverapi.events.model.Indicators
import ru.kima.sonar.common.util.MathUtil
import ru.kima.sonar.feature.notifications.R
import java.text.DecimalFormat


fun StringBuilder.appendIndicatorsToSecurityAlert(
    i: Indicators?, currentPrice: Double,
    //TODO: Have fun with mfi
    renderMFI: Boolean = false,
    renderSrsi: Boolean = false,
    df: DecimalFormat,
    resources: Resources,
    rsiLow: Double = MathUtil.RSI_LOW, rsiHigh: Double = MathUtil.RSI_HIGH,
//    bbLow: Double = MathUtil.BB_CRITICAL_LOW, bbHigh: Double = MathUtil.BB_CRITICAL_HIGH
) {
    if (i == null) return

    val m15 = resources.getString(R.string.candle_interval_15m)
    val h1 = resources.getString(R.string.candle_interval_1h)
    val h4 = resources.getString(R.string.candle_interval_4h)
    val d1 = resources.getString(R.string.candle_interval_1d)

    appendLine(resources.getString(R.string.indicators_headline))
    var color = PresentationUtil.rsiColor(i.min15Rsi, rsiLow, rsiHigh)
    var indicatorName = resources.getString(R.string.indicator_rsi, m15)
    append(color, indicatorName, df.format(i.min15Rsi), '\n')
    color = PresentationUtil.rsiColor(i.hourlyRsi, rsiLow, rsiHigh)
    indicatorName = resources.getString(R.string.indicator_rsi, h1)
    append(color, indicatorName, df.format(i.hourlyRsi), '\n')
    color = PresentationUtil.rsiColor(i.hour4Rsi, rsiLow, rsiHigh)
    indicatorName = resources.getString(R.string.indicator_rsi, h4)
    append(color, indicatorName, df.format(i.hour4Rsi), '\n')
    color = PresentationUtil.rsiColor(i.dailyRsi, rsiLow, rsiHigh)
    indicatorName = resources.getString(R.string.indicator_rsi, d1)
    append(color, indicatorName, df.format(i.dailyRsi), '\n')

    if (renderSrsi) {
        color = PresentationUtil.rsiColor(i.min15Srsi, MathUtil.SRSI_LOW, MathUtil.SRSI_HIGH)
        indicatorName = resources.getString(R.string.indicator_srsi, m15)
        append(color, indicatorName, df.format(i.min15Srsi), '\n')
        color = PresentationUtil.rsiColor(i.hourlySrsi, MathUtil.SRSI_LOW, MathUtil.SRSI_HIGH)
        indicatorName = resources.getString(R.string.indicator_srsi, h1)
        append(color, indicatorName, df.format(i.hourlySrsi), '\n')
        color = PresentationUtil.rsiColor(i.hour4Srsi, MathUtil.SRSI_LOW, MathUtil.SRSI_HIGH)
        indicatorName = resources.getString(R.string.indicator_srsi, h4)
        append(color, indicatorName, df.format(i.hour4Srsi), '\n')
        color = PresentationUtil.rsiColor(i.dailySrsi, MathUtil.SRSI_LOW, MathUtil.SRSI_HIGH)
        indicatorName = resources.getString(R.string.indicator_srsi, d1)
        append(color, indicatorName, df.format(i.dailySrsi), '\n')
    }

    if (renderMFI) {
        color = PresentationUtil.rsiColor(i.min15Mfi)
        indicatorName = resources.getString(R.string.indicator_mfi, m15)
        append(color, indicatorName, df.format(i.min15Mfi), '\n')
        color = PresentationUtil.rsiColor(i.hourlyMfi)
        indicatorName = resources.getString(R.string.indicator_mfi, h1)
        append(color, indicatorName, df.format(i.hourlyMfi), '\n')
        color = PresentationUtil.rsiColor(i.hour4Mfi)
        indicatorName = resources.getString(R.string.indicator_mfi, h4)
        append(color, indicatorName, df.format(i.hour4Mfi), '\n')
        color = PresentationUtil.rsiColor(i.dailyMfi)
        indicatorName = resources.getString(R.string.indicator_mfi, d1)
        append(color, indicatorName, df.format(i.dailyMfi), '\n')
    }

//    renderBb(indicators.min15bb, currentPrice, "15м", bbLow, bbHigh)
//    renderBb(indicators.hourlyBb, currentPrice, "1ч", bbLow, bbHigh)
//    renderBb(indicators.hour4Bb, currentPrice, "4ч", bbLow, bbHigh)
//    renderBb(indicators.dailyBb, currentPrice, "1д", bbLow, bbHigh)
}

//fun StringBuilder.renderBb(
//    bollingerBandsData: BollingerBandsData,
//    currentPrice: Double,
//    intervalsString: String,
//    lowPercent: Double = MathUtil.BB_CRITICAL_LOW,
//    highPercent: Double = MathUtil.BB_CRITICAL_HIGH,
//) {
//    val bbColor = PresentationUtil.markupBbColor(
//        currentPrice,
//        bollingerBandsData.lower,
//        bollingerBandsData.upper,
//        lowPercent,
//        highPercent
//    )
//    append(
//        '*',
//        bbColor,
//        "BB (",
//        intervalsString,
//        "):* ",
//        bollingerBandsData.lower.formatToRu(),
//        " - "
//    )
//    append('*', bollingerBandsData.middle.formatToRu(), "* - ")
//    appendLine(bollingerBandsData.upper.formatToRu())
//}
