package ru.kima.sonar.data.homeapi.datasource

import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemFuture
import ru.kima.sonar.common.serverapi.serverresponse.securitieslist.ListItemShare
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

    fun tradableShares(): Flow<SonarResult<List<ListItemShare>, HomeApiError>>
    fun tradableFutures(): Flow<SonarResult<List<ListItemFuture>, HomeApiError>>
}