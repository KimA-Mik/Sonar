package ru.kima.sonar.feature.notifications.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ru.kima.sonar.common.serverapi.events.BoundPriceEvent
import ru.kima.sonar.common.serverapi.events.NotificationEvent
import ru.kima.sonar.common.serverapi.events.UnboundPriceEvent
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.feature.notifications.R
import ru.kima.sonar.feature.notifications.notifications.BoundPriceNotification
import ru.kima.sonar.feature.notifications.notifications.UnboundPriceNotification

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

    fun showBasicNotification(id: Int, title: String, text: String) {
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_small)
        val notificationLayoutExpanded =
            RemoteViews(context.packageName, R.layout.notification_large)
        notificationLayout.setTextViewText(R.id.notification_small_title, title)
        notificationLayoutExpanded.setTextViewText(R.id.notification_large_title, title)
        notificationLayoutExpanded.setTextViewText(R.id.notification_body, text)
        val notification = NotificationCompat
            .Builder(context, PORTFOLIO_EVENTS_NOTIFICATION_CHANNEL_ID)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(CommonDrawables.exclamation_24px)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .build()
        notificationManager.notify(id, notification)
    }

    fun showNotificationEvent(messageId: Int, event: NotificationEvent) {
        val format = when (event) {
            is BoundPriceEvent -> BoundPriceNotification(event)
            is UnboundPriceEvent -> UnboundPriceNotification(event)
        }
        val title = format.title(context.resources)
        val text = format.body(context.resources)
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_small)
        val notificationLayoutExpanded =
            RemoteViews(context.packageName, R.layout.notification_large)
        notificationLayout.setTextViewText(R.id.notification_small_title, title)
        notificationLayoutExpanded.setTextViewText(R.id.notification_large_title, title)
        notificationLayoutExpanded.setTextViewText(R.id.notification_body, text)

        val notification = NotificationCompat
            .Builder(context, PORTFOLIO_EVENTS_NOTIFICATION_CHANNEL_ID)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(CommonDrawables.exclamation_24px)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .build()
        notificationManager.notify(messageId, notification)
    }

    companion object {
        private const val PORTFOLIO_EVENTS_NOTIFICATION_CHANNEL_ID =
            "portfolio_events_notification_channel"
    }
}