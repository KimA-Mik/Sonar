package ru.kima.sonar.server.data.user.datasource.portfolio

import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.data.user.model.UserDataError
import ru.kima.sonar.server.data.user.model.portfolio.LightPortfolioWithRule
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioRule
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioWithEntries
import ru.kima.sonar.server.data.user.model.portfolio.StopLoss
import ru.kima.sonar.server.data.user.model.portfolio.TakeProfit
import java.math.BigDecimal

interface PortfolioDataSource {
    suspend fun insertPortfolio(portfolio: Portfolio): SonarResult<Portfolio, UserDataError>
    suspend fun updatePortfolio(portfolio: Portfolio): SonarResult<Portfolio, UserDataError>
    suspend fun getPortfoliosByUserId(userId: Long): SonarResult<List<Portfolio>, UserDataError>
    suspend fun getPortfolioById(id: Long): SonarResult<Portfolio, UserDataError>
    suspend fun deletePortfolioById(id: Long): SonarResult<Unit, UserDataError>

    suspend fun insertPortfolioEntry(portfolioEntry: PortfolioEntry): SonarResult<PortfolioEntry, UserDataError>
    suspend fun insertPortfolioEntries(entries: List<PortfolioEntry>): SonarResult<Unit, UserDataError>
    suspend fun updatePortfolioEntry(portfolioEntry: PortfolioEntry): SonarResult<PortfolioEntry, UserDataError>
    suspend fun updatePortfolioEntryTransaction(
        id: Long,
        name: String,
        targetDeviation: BigDecimal,
        newStopLosses: List<StopLoss>,
        newTakeProfits: List<TakeProfit>,
        stopLossesToDelete: List<Long>,
        takeProfitsToDelete: List<Long>,
        takeProfitsToUpdate: List<TakeProfit>,
        stopLossesToUpdate: List<StopLoss>
    ): SonarResult<PortfolioEntry, UserDataError>
    suspend fun updatePortfolioEntries(entries: List<PortfolioEntry>): SonarResult<Unit, UserDataError>
    suspend fun deletePortfolioEntry(id: Long): SonarResult<Unit, UserDataError>
    suspend fun getEntryById(id: Long): SonarResult<PortfolioEntry, UserDataError>
    suspend fun createStopLoss(entryId: Long): SonarResult<Long, UserDataError>
    suspend fun createTakeProfit(entryId: Long): SonarResult<Long, UserDataError>
    suspend fun getStopLossById(id: Long): SonarResult<StopLoss, UserDataError>
    suspend fun getTakeProfitById(id: Long): SonarResult<TakeProfit, UserDataError>
    suspend fun deleteStopLoss(id: Long): SonarResult<Unit, UserDataError>
    suspend fun deleteTakeProfit(id: Long): SonarResult<Unit, UserDataError>

    suspend fun getPortfolioWithEntriesById(id: Long): SonarResult<PortfolioWithEntries, UserDataError>
    suspend fun getPortfolios(): SonarResult<List<PortfolioWithEntries>, UserDataError>

    suspend fun getPortfolioRule(portfolioId: Long): SonarResult<LightPortfolioWithRule, UserDataError>
    suspend fun updatePortfolioRule(rule: PortfolioRule): SonarResult<LightPortfolioWithRule, UserDataError>
}