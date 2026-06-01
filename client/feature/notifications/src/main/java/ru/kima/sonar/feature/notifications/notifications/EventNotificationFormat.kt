package ru.kima.sonar.feature.notifications.notifications

import android.content.Context
import android.content.res.Resources
import androidx.core.app.NotificationCompat
import ru.kima.sonar.common.serverapi.events.BoundPriceEvent
import ru.kima.sonar.common.serverapi.events.NotificationEvent
import ru.kima.sonar.common.serverapi.events.UnboundPriceEvent
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

internal sealed interface EventNotificationFormat {
    fun title(resources: Resources): String
    fun body(resources: Resources): String
    fun actions(context: Context): List<NotificationCompat.Action>
    suspend fun deferredActions(context: Context): List<NotificationCompat.Action>
    fun decimalFormat() = DecimalFormat("##0.###", DecimalFormatSymbols(Locale.getDefault()))

    fun qualifier(): String = when (this) {
        is BoundPriceNotification -> BoundPriceEvent::class.java.simpleName
        is UnboundPriceNotification -> UnboundPriceEvent::class.java.simpleName
    }

    companion object {
        internal fun qualifier(event: NotificationEvent) = when (event) {
            is BoundPriceEvent -> BoundPriceEvent::class.java.simpleName
            is UnboundPriceEvent -> UnboundPriceEvent::class.java.simpleName
        }
    }
}