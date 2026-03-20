package ru.kima.sonar.feature.notifications.notifications

import android.content.res.Resources
import ru.kima.sonar.common.serverapi.events.NotificationEvent
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

interface EventNotificationFormat<T : NotificationEvent> {
    fun title(event: T, resources: Resources): String
    fun body(event: T, resources: Resources): String

    fun decimalFormat() = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.getDefault()))
}