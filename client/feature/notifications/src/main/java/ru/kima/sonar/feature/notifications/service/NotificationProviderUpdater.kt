package ru.kima.sonar.feature.notifications.service

import kotlinx.coroutines.flow.firstOrNull
import ru.kima.sonar.common.util.isSuccess
import ru.kima.sonar.data.applicationconfig.local.datasource.LocalConfigDataSource
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider
import ru.kima.sonar.data.applicationconfig.updateconfig.datasource.UpdateConfigDataSource
import ru.kima.sonar.data.applicationconfig.updateconfig.model.ProviderUpdate
import ru.kima.sonar.data.homeapi.datasource.HomeApiDataSource

class NotificationProviderUpdater(
    private val homeApiDataSource: HomeApiDataSource,
    private val localConfigDataSource: LocalConfigDataSource,
    private val updateConfigDataSource: UpdateConfigDataSource
) {
    suspend fun retainedCheck() {
        val providerUpdate = updateConfigDataSource.configFlow().firstOrNull()?.providerUpdate
        if (providerUpdate is ProviderUpdate.FirebaseUpdate) {
            updateFirebaseToken(providerUpdate.token)
        }
    }

    suspend fun updateFirebaseToken(token: String) {
        val localConfig = localConfigDataSource.localConfig().firstOrNull() ?: return
        if (localConfig.apiAccessToken == null) {
            localConfigDataSource.upgradeNotificationProvider(
                LocalNotificationProvider.FIREBASE,
                token
            )
            updateConfigDataSource.setProviderUpdate(null)
            return
        }


        val result = homeApiDataSource.updateNotificationProvider(
            LocalNotificationProvider.FIREBASE, token
        )

        if (result.isSuccess()) {
            localConfigDataSource.upgradeNotificationProvider(
                LocalNotificationProvider.FIREBASE,
                token
            )
            updateConfigDataSource.setProviderUpdate(null)
        } else {
            updateConfigDataSource.setProviderUpdate(ProviderUpdate.FirebaseUpdate(token))
        }
    }
}