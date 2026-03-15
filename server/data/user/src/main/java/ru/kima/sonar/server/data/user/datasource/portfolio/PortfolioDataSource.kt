package ru.kima.sonar.server.data.user.datasource.portfolio

import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.data.user.model.UserDataError
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioWithEntries

interface PortfolioDataSource {
    suspend fun insertPortfolio(portfolio: Portfolio): SonarResult<Portfolio, UserDataError>
    suspend fun updatePortfolio(portfolio: Portfolio): SonarResult<Portfolio, UserDataError>
    suspend fun getPortfoliosByUserId(userId: Long): SonarResult<List<Portfolio>, UserDataError>
    suspend fun getPortfolioById(id: Long): SonarResult<Portfolio, UserDataError>
    suspend fun deletePortfolioById(id: Long): SonarResult<Unit, UserDataError>

    suspend fun insertPortfolioEntry(portfolioEntry: PortfolioEntry): SonarResult<PortfolioEntry, UserDataError>
    suspend fun updatePortfolioEntry(portfolioEntry: PortfolioEntry): SonarResult<PortfolioEntry, UserDataError>
    suspend fun updatePortfolioEntries(entries: List<PortfolioEntry>): SonarResult<Unit, UserDataError>
    suspend fun deletePortfolioEntry(id: Long): SonarResult<Unit, UserDataError>
    suspend fun getEntryById(id: Long): SonarResult<PortfolioEntry, UserDataError>

    suspend fun getPortfolioWithEntriesById(id: Long): SonarResult<PortfolioWithEntries, UserDataError>
    suspend fun getPortfolios(): SonarResult<List<PortfolioWithEntries>, UserDataError>
}