package ru.kima.sonar.server.feature.portfolios.service.provider

import ru.kima.sonar.common.serverapi.events.NotificationEvent

interface NotificationProvider {
    suspend fun provideNotification(deviceId: String, event: NotificationEvent)
}