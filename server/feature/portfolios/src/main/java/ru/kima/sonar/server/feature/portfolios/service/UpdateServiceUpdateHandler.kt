package ru.kima.sonar.server.feature.portfolios.service

import ru.kima.sonar.common.serverapi.model.NotificationProvider
import ru.kima.sonar.server.feature.portfolios.service.mapper.toNotificationEvent
import ru.kima.sonar.server.feature.portfolios.service.provider.FirebaseNotificationProvider

class UpdateServiceUpdateHandler(
    firebaseNotificationProvider: FirebaseNotificationProvider
) {
    private val providers = mapOf(
        NotificationProvider.FIREBASE to firebaseNotificationProvider
    )

    suspend fun consume(update: UpdateServiceEvent) {
        for (session in update.user.sessions) {
            val deviceId = session.notificationProviderId ?: continue
            val provider = session.notificationProvider?.let { providers[it] } ?: continue

            provider.provideNotification(deviceId, update.toNotificationEvent())
        }
    }
}