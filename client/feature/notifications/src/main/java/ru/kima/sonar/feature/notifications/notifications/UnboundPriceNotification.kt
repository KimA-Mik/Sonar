package ru.kima.sonar.feature.notifications.notifications

import android.content.res.Resources
import ru.kima.sonar.common.serverapi.events.UnboundPriceEvent
import ru.kima.sonar.feature.notifications.R

internal class UnboundPriceNotification(
    private val event: UnboundPriceEvent
) : EventNotificationFormat {
    override fun title(resources: Resources): String {
        val df = decimalFormat()
        return when (event.priceType) {
            is UnboundPriceEvent.PriceType.Above -> resources.getString(
                R.string.event_unbound_price_above,
                event.securityName,
                df.format((event.priceType as UnboundPriceEvent.PriceType.Above).targetPrice)
            )

            is UnboundPriceEvent.PriceType.Below -> resources.getString(
                R.string.event_unbound_price_below,
                event.securityName,
                df.format((event.priceType as UnboundPriceEvent.PriceType.Below).targetPrice)
            )
        }
    }

    override fun body(resources: Resources): String = buildString(NOTIFICATION_BODY_CAPACITY) {
        val df = decimalFormat()
        val price = df.format(event.lastPrice.price)

        append(resources.getString(R.string.event_current_security_price, price))

        when (event.priceType) {
            is UnboundPriceEvent.PriceType.Above -> {
                val e = event.priceType as UnboundPriceEvent.PriceType.Above
                val str = resources.getString(
                    R.string.event_unbound_price_is_greater,
                    df.format(e.deviation),
                    df.format(e.targetPrice)
                )
                appendLine(str)
            }

            is UnboundPriceEvent.PriceType.Below -> appendLine(
                resources.getString(
                    R.string.event_unbound_price_is_lower,
                    df.format((event.priceType as UnboundPriceEvent.PriceType.Below).deviation),
                    df.format((event.priceType as UnboundPriceEvent.PriceType.Below).targetPrice)
                )
            )
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