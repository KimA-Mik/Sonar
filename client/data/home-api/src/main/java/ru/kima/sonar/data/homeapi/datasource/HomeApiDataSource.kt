package ru.kima.sonar.data.homeapi.datasource

import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider
import ru.kima.sonar.data.homeapi.error.HomeApiError

interface HomeApiDataSource {
    suspend fun login(
        login: String,
        password: String,
        localNotificationProvider: LocalNotificationProvider?,
        notificationProviderClientId: String?
    ): SonarResult<String, HomeApiError>
}