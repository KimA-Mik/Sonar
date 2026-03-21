package ru.kima.sonar.server.feature.portfolios.service.provider

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import ru.kima.sonar.common.serverapi.events.NotificationEvent

class FirebaseNotificationProvider : NotificationProvider {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val firebaseMessaging = FirebaseMessaging.getInstance()
    override suspend fun provideNotification(
        deviceId: String,
        event: NotificationEvent
    ) {
        val message = Message.builder()
            .setToken(deviceId)
            .putData(NotificationEvent.DATA_KEY, Json.encodeToString(event))
            .build()

        withContext(Dispatchers.IO) {
            try {
                firebaseMessaging.send(message)
            } catch (e: FirebaseMessagingException) {
                logger.error("Unable to send firebase message: $e")
            }
        }
    }
}