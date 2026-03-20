package ru.kima.sonar.feature.notifications.notifications

import android.content.res.Resources
import ru.kima.sonar.common.serverapi.events.BoundPriceEvent
import ru.kima.sonar.feature.notifications.R

object BoundPriceNotification : EventNotificationFormat<BoundPriceEvent> {
    override fun title(event: BoundPriceEvent, resources: Resources): String {
        val df = decimalFormat()
        val price = df.format(event.priceType)
        return when (event.priceType) {
            is BoundPriceEvent.PriceType.All -> resources.getString(
                R.string.event_security_costs,
                event.securityName, price
            )

            is BoundPriceEvent.PriceType.High -> {
                val deviation =
                    df.format((event.priceType as BoundPriceEvent.PriceType.High).deviation)
                resources.getString(
                    R.string.bound_price_event_title_high,
                    event.securityName, price, deviation
                )
            }

            is BoundPriceEvent.PriceType.Low -> {
                val deviation =
                    df.format((event.priceType as BoundPriceEvent.PriceType.Low).deviation)
                resources.getString(
                    R.string.bound_price_event_title_low,
                    event.securityName, price, deviation
                )
            }
        }
    }

    override fun body(
        event: BoundPriceEvent,
        resources: Resources
    ): String = buildString {
        val df = decimalFormat()
        val price = df.format(event.lastPrice)

        appendLine(resources.getString(R.string.event_current_security_price, price))
        when (event.priceType) {
            is BoundPriceEvent.PriceType.All -> {
                val lowTargetPrice =
                    df.format((event.priceType as BoundPriceEvent.PriceType.All).lowTargetPrice)
                val highTargetPrice =
                    df.format((event.priceType as BoundPriceEvent.PriceType.All).highTargetPrice)

                appendLine(resources.getString(R.string.event_planned_ask_price, lowTargetPrice))
                appendLine(resources.getString(R.string.event_planned_bid_price, highTargetPrice))
            }

            is BoundPriceEvent.PriceType.High -> {
                val targetPrice =
                    df.format(((event.priceType as BoundPriceEvent.PriceType.High).targetPrice))

                appendLine(resources.getString(R.string.event_planned_bid_price, targetPrice))
            }

            is BoundPriceEvent.PriceType.Low -> {
                val targetPrice =
                    df.format((event.priceType as BoundPriceEvent.PriceType.Low).targetPrice)

                appendLine(resources.getString(R.string.event_planned_ask_price, targetPrice))
            }
        }

        when (event.priceType) {
            is BoundPriceEvent.PriceType.All -> {
                val lowDeviation = df
                    .format((event.priceType as BoundPriceEvent.PriceType.All).lowDeviation)
                val highDeviation = df
                    .format((event.priceType as BoundPriceEvent.PriceType.All).highDeviation)

                appendLine(resources.getString(R.string.event_ask_price_deviation, lowDeviation))
                appendLine(resources.getString(R.string.event_bid_price_deviation, highDeviation))
            }

            is BoundPriceEvent.PriceType.High -> {
                val deviation =
                    df.format((event.priceType as BoundPriceEvent.PriceType.High).deviation)

                appendLine(resources.getString(R.string.event_price_deviation, deviation))
            }

            is BoundPriceEvent.PriceType.Low -> {
                val deviation =
                    df.format((event.priceType as BoundPriceEvent.PriceType.Low).deviation)

                appendLine(resources.getString(R.string.event_price_deviation, deviation))
            }
        }

        appendLine()
        appendIndicatorsToSecurityAlert(
            i = event.indicators,
            currentPrice = event.lastPrice.price.toDouble(),
            renderSrsi = true,
            df = df,
            resources = resources,
        )

        if (event.note.isNotBlank()) {
            appendLine(resources.getString(R.string.note_headline))
            appendLine(event.note)
        }
    }
}