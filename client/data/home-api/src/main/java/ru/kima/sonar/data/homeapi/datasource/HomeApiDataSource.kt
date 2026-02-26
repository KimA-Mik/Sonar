package ru.kima.sonar.data.homeapi.datasource

import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.common.serverapi.dto.portfolio.response.ListItemPortfolio
import ru.kima.sonar.common.serverapi.dto.portfolio.response.PortfolioResponse
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemFuture
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemShare
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider
import ru.kima.sonar.data.homeapi.error.HomeApiError
import java.math.BigDecimal

interface HomeApiDataSource {
    suspend fun login(
        login: String,
        password: String,
        localNotificationProvider: LocalNotificationProvider?,
        notificationProviderClientId: String?
    ): SonarResult<String, HomeApiError>

    fun tradableShares(): Flow<SonarResult<List<ListItemShare>, HomeApiError>>
    fun tradableFutures(): Flow<SonarResult<List<ListItemFuture>, HomeApiError>>

    suspend fun portfolios(): SonarResult<List<ListItemPortfolio>, HomeApiError>
    suspend fun createPortfolio(name: String): SonarResult<Unit, HomeApiError>
    suspend fun getPortfolio(portfolioId: Long): SonarResult<PortfolioResponse, HomeApiError>
    suspend fun updatePortfolio(portfolioId: Long, name: String): SonarResult<Unit, HomeApiError>
    suspend fun deletePortfolio(portfolioId: Long): SonarResult<Unit, HomeApiError>

    suspend fun addEntry(
        portfolioId: Long,
        name: String,
        securityUid: String,
        lowPrice: BigDecimal,
        highPrice: BigDecimal,
        note: String
    ): SonarResult<Unit, HomeApiError>

    suspend fun updateEntry(
        entryId: Long,
        name: String,
        lowPrice: BigDecimal,
        highPrice: BigDecimal,
        note: String
    ): SonarResult<Unit, HomeApiError>

    suspend fun deleteEntry(entryId: Long): SonarResult<Unit, HomeApiError>
}