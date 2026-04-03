package ru.kima.sonar.feature.notifications.notifications

import android.content.res.Resources
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

internal interface EventNotificationFormat {
    fun title(resources: Resources): String
    fun body(resources: Resources): String

    fun decimalFormat() = DecimalFormat("##0.###", DecimalFormatSymbols(Locale.getDefault()))
}