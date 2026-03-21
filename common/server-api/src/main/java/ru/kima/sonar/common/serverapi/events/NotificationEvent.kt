package ru.kima.sonar.common.serverapi.events

import kotlinx.serialization.Serializable

@Serializable
sealed interface NotificationEvent {
    companion object {
        const val DATA_KEY = "notification_event"
    }
}