package ru.kima.sonar.data.applicationconfig.local.datasource

import android.content.Context
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider
import ru.kima.sonar.data.applicationconfig.local.util.localConfigDataStore

class ProtoDataStoreDataSourceImpl(context: Context) : LocalConfigDataSource {
    private val dataStore = context.localConfigDataStore
    override fun localConfig() = dataStore.data

    override suspend fun updateLoginAndPassword(login: String?, password: String?) {
        dataStore.updateData {
            it.copy(
                login = login,
                password = password
            )
        }
    }

    override suspend fun upgradeNotificationProvider(
        notificationProvider: LocalNotificationProvider?,
        clientId: String?
    ) {
        dataStore.updateData {
            it.copy(
                notificationProvider = notificationProvider,
                notificationProviderClientId = clientId
            )
        }
    }

    override suspend fun updateApiUri(apiUrl: String) {
        dataStore.updateData {
            it.copy(
                apiUrl = apiUrl
            )
        }
    }

    override suspend fun updateApiAccessToken(apiAccessToken: String?) {
        dataStore.updateData {
            it.copy(
                apiAccessToken = apiAccessToken
            )
        }
    }
}