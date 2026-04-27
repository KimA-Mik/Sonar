package ru.kima.sonar.data.homeapi.datasource

import kotlinx.coroutines.flow.Flow
import ru.kima.sonar.common.serverapi.dto.portfolio.request.AddPortfolioEntryRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.response.ListItemPortfolio
import ru.kima.sonar.common.serverapi.dto.portfolio.response.ResourceCreatedResponse
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemFuture
import ru.kima.sonar.common.serverapi.dto.securitieslist.response.ListItemShare
import ru.kima.sonar.common.serverapi.model.portfolio.PortfolioEntry
import ru.kima.sonar.common.serverapi.model.portfolio.RuleEditPortfolio
import ru.kima.sonar.common.serverapi.model.portfolio.SonarPortfolio
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.data.applicationconfig.local.model.LocalNotificationProvider
import ru.kima.sonar.data.homeapi.error.HomeApiError
import java.math.BigDecimal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface HomeApiDataSource {
    suspend fun login(
        login: String,
        password: String,
        localNotificationProvider: LocalNotificationProvider?,
        notificationProviderClientId: String?
    ): SonarResult<String, HomeApiError>

    fun tradableShares(period: Duration = 5.seconds): Flow<SonarResult<List<ListItemShare>, HomeApiError>>
    fun tradableFutures(period: Duration = 5.seconds): Flow<SonarResult<List<ListItemFuture>, HomeApiError>>

    suspend fun portfolios(): SonarResult<List<ListItemPortfolio>, HomeApiError>
    suspend fun createPortfolio(name: String): SonarResult<Unit, HomeApiError>
    suspend fun getPortfolio(portfolioId: Long): SonarResult<SonarPortfolio, HomeApiError>
    suspend fun updatePortfolio(portfolioId: Long, name: String): SonarResult<Unit, HomeApiError>
    suspend fun deletePortfolio(portfolioId: Long): SonarResult<Unit, HomeApiError>
    suspend fun getPortfolioEntry(entryId: Long): SonarResult<PortfolioEntry, HomeApiError>

    suspend fun addEntry(
        portfolioId: Long,
        entries: List<AddPortfolioEntryRequest.Entry>
    ): SonarResult<Unit, HomeApiError>

    suspend fun updateEntry(
        entryId: Long,
        name: String,
        targetDeviation: BigDecimal,
        lowPrice: BigDecimal,
        highPrice: BigDecimal,
        note: String
    ): SonarResult<Unit, HomeApiError>

    suspend fun deleteEntry(entryId: Long): SonarResult<Unit, HomeApiError>

    suspend fun getPortfolioRule(portfolioId: Long): SonarResult<RuleEditPortfolio, HomeApiError>
    suspend fun createStopLoss(entryId: Long): SonarResult<ResourceCreatedResponse, HomeApiError>
    suspend fun createTakeProfit(entryId: Long): SonarResult<ResourceCreatedResponse, HomeApiError>
    suspend fun updatePortfolioRule(
        portfolioId: Long,
        mode: RulesMode,
        rule: Rule
    ): SonarResult<Unit, HomeApiError>
}