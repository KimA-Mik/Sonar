package ru.kima.sonar.data.applicationconfig.local.datasource

import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.data.applicationconfig.local.model.LocalConfig
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider

interface LocalConfigDataSource {
    fun localConfig(): Flow<LocalConfig>
    suspend fun updateLoginAndPassword(login: String?, password: String?)
    suspend fun upgradeNotificationProvider(
        notificationProvider: LocalNotificationProvider?,
        clientId: String?
    )

    suspend fun updateApiUri(apiUrl: String)
    suspend fun updateApiAccessToken(apiAccessToken: String?)
}