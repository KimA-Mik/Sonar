package ru.kima.sonar.server.data.user.datasource.portfolio

import org.jetbrains.exposed.v1.core.eq
import org.slf4j.LoggerFactory
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.common.util.databaseutil.DatabaseConnector
import ru.kima.sonar.server.data.user.mappers.putInside
import ru.kima.sonar.server.data.user.mappers.toDomainModel
import ru.kima.sonar.server.data.user.model.UserDataError
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioWithEntries
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntity
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryEntity
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioTable

internal class ExposedPortfolioDataSource(
    private val databaseConnector: DatabaseConnector
) : PortfolioDataSource {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun insertPortfolio(portfolio: Portfolio): SonarResult<Portfolio, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                val portfolioEntity = PortfolioEntity.new {
                    putInside(portfolio)
                }
                portfolioEntity.toDomainModel()
            }
            SonarResult.Success(result)
        } catch (e: Exception) {
            logger.error("Error inserting portfolio", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun updatePortfolio(portfolio: Portfolio): SonarResult<Portfolio, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                PortfolioEntity.findByIdAndUpdate(portfolio.id) {
                    it.putInside(portfolio)
                }?.toDomainModel()
            }
            if (result != null) {
                SonarResult.Success(result)
            } else {
                SonarResult.Error(UserDataError.NotFound)
            }
        } catch (e: Exception) {
            logger.error("Error updating portfolio", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getPortfoliosByUserId(userId: Long): SonarResult<List<Portfolio>, UserDataError> {
        return try {
            val portfolios = databaseConnector.suspendTransaction {
                PortfolioEntity.find { PortfolioTable.userId eq userId }.map { it.toDomainModel() }
            }
            SonarResult.Success(portfolios)
        } catch (e: Exception) {
            logger.error("Error getting portfolios by user id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getPortfolioById(id: Long): SonarResult<Portfolio, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                PortfolioEntity.findById(id)?.toDomainModel()
            }
            if (result != null) {
                SonarResult.Success(result)
            } else {
                SonarResult.Error(UserDataError.NotFound)
            }
        } catch (e: Exception) {
            logger.error("Error getting portfolio by id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun deletePortfolioById(id: Long): SonarResult<Unit, UserDataError> {
        return try {
            var found = false
            databaseConnector.suspendTransaction {
                val entity = PortfolioEntity.findById(id)
                if (entity != null) {
                    //TODO: Explore exposed onDelete
//                    entity.entries.forEach { it.delete() }
                    entity.delete()
                    found = true
                }
            }
            if (found) SonarResult.Success(Unit)
            else SonarResult.Error(UserDataError.NotFound)
        } catch (e: Exception) {
            logger.error("Error deleting portfolio by id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun insertPortfolioEntry(portfolioEntry: PortfolioEntry): SonarResult<PortfolioEntry, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                val entryEntity = PortfolioEntryEntity.new {
                    putInside(portfolioEntry)
                }
                entryEntity.toDomainModel()
            }
            SonarResult.Success(result)
        } catch (e: Exception) {
            logger.error("Error inserting portfolio entry", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun updatePortfolioEntry(portfolioEntry: PortfolioEntry): SonarResult<PortfolioEntry, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                PortfolioEntryEntity.findByIdAndUpdate(portfolioEntry.id) {
                    it.putInside(portfolioEntry)
                }?.toDomainModel()
            }
            if (result != null) {
                SonarResult.Success(result)
            } else {
                SonarResult.Error(UserDataError.NotFound)
            }
        } catch (e: Exception) {
            logger.error("Error updating portfolio entry", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun updatePortfolioEntries(entries: List<PortfolioEntry>): SonarResult<Unit, UserDataError> {
        return try {
            // Didn't find a way to perform bach update using dao api
            // 1.0 release btw
            databaseConnector.suspendTransaction {
                for (entry in entries) {
                    PortfolioEntryEntity.findByIdAndUpdate(entry.id) { entity ->
                        entity.putInside(entry)
                    }
                }
            }

            SonarResult.Success(Unit)
        } catch (e: Exception) {
            logger.error("Error updating portfolio entries", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun deletePortfolioEntry(id: Long): SonarResult<Unit, UserDataError> {
        return try {
            var found = false
            databaseConnector.suspendTransaction {
                val entity = PortfolioEntryEntity.findById(id)
                if (entity != null) {
                    entity.delete()
                    found = true
                }
            }
            if (found) {
                SonarResult.Success(Unit)
            } else {
                SonarResult.Error(UserDataError.NotFound)
            }
        } catch (e: Exception) {
            logger.error("Error deleting portfolio entry", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getEntryById(id: Long): SonarResult<PortfolioEntry, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                PortfolioEntryEntity.findById(id)?.toDomainModel()
            }
            if (result != null) {
                SonarResult.Success(result)
            } else {
                SonarResult.Error(UserDataError.NotFound)
            }
        } catch (e: Exception) {
            logger.error("Error getting portfolio entry by id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getPortfolioWithEntriesById(id: Long): SonarResult<PortfolioWithEntries, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                PortfolioEntity.findById(id)?.let { portfolioEntity ->
                    val entries = portfolioEntity.entries.map { it.toDomainModel() }
                    PortfolioWithEntries(
                        portfolio = portfolioEntity.toDomainModel(),
                        entries = entries
                    )
                }
            }
            if (result != null) {
                SonarResult.Success(result)
            } else {
                SonarResult.Error(UserDataError.NotFound)
            }
        } catch (e: Exception) {
            logger.error("Error getting portfolio with entries by id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getPortfolios(): SonarResult<List<PortfolioWithEntries>, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                val portfolios = PortfolioEntity.all()
                portfolios.map { portfolio ->
                    PortfolioWithEntries(
                        portfolio = portfolio.toDomainModel(),
                        entries = portfolio.entries.map { it.toDomainModel() }
                    )
                }
            }

            SonarResult.Success(result)
        } catch (e: Exception) {
            logger.error("Error getting portfolio with entries by id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }
}