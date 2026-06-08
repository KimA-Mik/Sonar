package ru.kima.sonar.feature.notifications.notifications

import android.content.Context
import android.content.res.Resources
import androidx.core.app.NotificationCompat
import ru.kima.sonar.common.serverapi.events.RulesEvent
import ru.kima.sonar.data.finam.repository.FinamRepository
import ru.kima.sonar.feature.notifications.R

internal class RulesNotification(
    private val event: RulesEvent,
    private val finamRepository: FinamRepository
) : EventNotificationFormat {
    override fun title(resources: Resources): String {
        return resources.getString(
            R.string.event_rules_title,
            event.ticker
        )
    }

    override fun body(resources: Resources): String = buildString(NOTIFICATION_BODY_CAPACITY) {
        append(resources.getString(R.string.portfolio_headline))
        appendLine(event.portfolioName)

        val df = decimalFormat()
        val price = df.format(event.lastPrice.price)
        appendLine(resources.getString(R.string.event_current_security_price, price))

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

    override fun actions(context: Context): List<NotificationCompat.Action> {
        return listOf(getTInvestAction(context, event.ticker, event.securityType))
    }

    override suspend fun deferredActions(context: Context): List<NotificationCompat.Action> {
        return buildList {
            getFinamAction(context, event.ticker, event.securityType, finamRepository)?.let {
                add(it)
            }
        }
    }
}