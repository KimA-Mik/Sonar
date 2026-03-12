package ru.kima.sonar.feature.notifications.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.feature.notifications.R

class SonarNotificationsManager(
    private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                portfolioEventsNotificationChannel(),
            )
            notificationManager.createNotificationChannels(channels)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun portfolioEventsNotificationChannel(): NotificationChannel {
        val name = context.getString(R.string.notification_channel_name_portfolio_events)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(PORTFOLIO_EVENTS_NOTIFICATION_CHANNEL_ID, name, importance)
        channel.description =
            context.getString(R.string.notification_channel_description_portfolio_events)
        return channel
    }

    fun showPortfolioNotification(id: Int, title: String, text: String) {
        val notification = NotificationCompat
            .Builder(context, PORTFOLIO_EVENTS_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(CommonDrawables.settings_24px)
            .setContentText(title)
            .setContentText(text)
            .build()
        notificationManager.notify(id, notification)
    }

    companion object {
        private const val PORTFOLIO_EVENTS_NOTIFICATION_CHANNEL_ID =
            "portfolio_events_notification_channel"
    }
}