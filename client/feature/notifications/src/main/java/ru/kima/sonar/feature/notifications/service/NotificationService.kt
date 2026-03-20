package ru.kima.sonar.feature.notifications.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import ru.kima.sonar.common.serverapi.events.NotificationEvent
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider
import ru.kima.sonar.feature.notifications.manager.SonarNotificationsManager

private const val TAG = "NotificationService"

class NotificationService : FirebaseMessagingService() {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception: ${throwable.message}", throwable)
    }
    private val scope = CoroutineScope(SupervisorJob() + exceptionHandler)
    private val applicationConfig: LocalConfigDataSource by inject()
    private val notificationsManager: SonarNotificationsManager by inject()
    override fun onNewToken(token: String) {
        scope.launch {
            applicationConfig.upgradeNotificationProvider(LocalNotificationProvider.FIREBASE, token)
//            val config = applicationConfig.localConfig().first()
//            if (config.apiAccessToken != null) {
//                //TODO: Update token on server
//            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val messageId = message.sentTime.toInt()
        val raw = message.data[NotificationEvent.DATA_KEY]
        if (raw == null) {
            Log.e(TAG, "No ${NotificationEvent.DATA_KEY} enty in $message")
            return
        }
        val event = try {
            Json.decodeFromString<NotificationEvent>(raw)
        } catch (e: Exception) {
            Log.e(TAG, "Unable to decode event because of $e")
            return
        }

        notificationsManager.showNotificationEvent(messageId, event)
    }
}
