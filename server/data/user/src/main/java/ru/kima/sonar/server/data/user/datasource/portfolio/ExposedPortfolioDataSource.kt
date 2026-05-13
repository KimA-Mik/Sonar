package ru.kima.sonar.server.data.user.datasource.portfolio

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.load
import org.jetbrains.exposed.v1.dao.with
import org.slf4j.LoggerFactory
import ru.kima.sonar.common.util.SonarResult
import ru.kima.sonar.server.common.util.databaseutil.DatabaseConnector
import ru.kima.sonar.server.data.user.mappers.pitInside
import ru.kima.sonar.server.data.user.mappers.putInside
import ru.kima.sonar.server.data.user.mappers.toDomainModel
import ru.kima.sonar.server.data.user.mappers.toLightPortfolioWithRule
import ru.kima.sonar.server.data.user.model.UserDataError
import ru.kima.sonar.server.data.user.model.portfolio.LightPortfolioWithRule
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioRule
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioWithEntries
import ru.kima.sonar.server.data.user.model.portfolio.StopLoss
import ru.kima.sonar.server.data.user.model.portfolio.TakeProfit
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntity
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryEntity
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioTable
import ru.kima.sonar.server.data.user.scema.portfolio.RulesEntity
import ru.kima.sonar.server.data.user.scema.portfolio.StopLossEntity
import ru.kima.sonar.server.data.user.scema.portfolio.TakeProfitEntity
import java.math.BigDecimal

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
                    deletePortfolioEntity(entity)
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
                }.load(
                    PortfolioEntryEntity::stopLosses,
                    PortfolioEntryEntity::takeProfits
                )
                entryEntity.toDomainModel()
            }
            SonarResult.Success(result)
        } catch (e: Exception) {
            logger.error("Error inserting portfolio entry", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun insertPortfolioEntries(entries: List<PortfolioEntry>): SonarResult<Unit, UserDataError> {
        return try {
            databaseConnector.suspendTransaction {
                for (entry in entries) {
                    val inserted = PortfolioEntryEntity.new {
                        putInside(entry)
                    }

                    for (stopLoss in entry.stopLosses) {
                        StopLossEntity.new {
                            putInside(stopLoss.copy(entryId = inserted.id.value))
                        }
                    }

                    for (takeProfit in entry.takeProfits) {
                        TakeProfitEntity.new {
                            putInside(takeProfit.copy(entryId = inserted.id.value))
                        }
                    }
                }
            }
            SonarResult.Success(Unit)
        } catch (e: Exception) {
            logger.error("Error inserting portfolio entries", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun updatePortfolioEntry(portfolioEntry: PortfolioEntry): SonarResult<PortfolioEntry, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                PortfolioEntryEntity.findByIdAndUpdate(portfolioEntry.id) {
                    it.putInside(portfolioEntry)
                }?.load(
                    PortfolioEntryEntity::stopLosses,
                    PortfolioEntryEntity::takeProfits
                )?.toDomainModel()
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

    override suspend fun updatePortfolioEntryTransaction(
        id: Long,
        name: String,
        targetDeviation: BigDecimal,
        newStopLosses: List<StopLoss>,
        newTakeProfits: List<TakeProfit>,
        stopLossesToDelete: List<Long>,
        takeProfitsToDelete: List<Long>,
        takeProfitsToUpdate: List<TakeProfit>,
        stopLossesToUpdate: List<StopLoss>
    ): SonarResult<PortfolioEntry, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                newStopLosses.forEach {
                    StopLossEntity.new {
                        putInside(it)
                    }
                }

                newTakeProfits.forEach {
                    TakeProfitEntity.new {
                        putInside(it)
                    }
                }

                stopLossesToDelete.forEach { id ->
                    StopLossEntity.findById(id)?.delete()
                }

                takeProfitsToDelete.forEach { id ->
                    TakeProfitEntity.findById(id)?.delete()
                }

                stopLossesToUpdate.forEach {
                    StopLossEntity.findByIdAndUpdate(it.id) { entity ->
                        entity.putInside(it)
                    }
                }

                takeProfitsToUpdate.forEach {
                    TakeProfitEntity.findByIdAndUpdate(it.id) { entity ->
                        entity.putInside(it)
                    }
                }

                PortfolioEntryEntity.findByIdAndUpdate(id) {
                    it.name = name
                    it.targetDeviation = targetDeviation
                }?.load(
                    PortfolioEntryEntity::stopLosses,
                    PortfolioEntryEntity::takeProfits
                )?.toDomainModel()
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
                    deletePortfolioEntryEntity(entity)
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
                PortfolioEntryEntity.findById(id)
                    ?.load(
                        PortfolioEntryEntity::stopLosses,
                        PortfolioEntryEntity::takeProfits
                    )
                    ?.toDomainModel()
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

    override suspend fun createStopLoss(entryId: Long): SonarResult<Long, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                StopLossEntity.new { putInside(StopLoss.default(entryId = entryId)) }
            }
            SonarResult.Success(result.id.value)
        } catch (e: Exception) {
            logger.error("Error creating stop loss", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun createTakeProfit(entryId: Long): SonarResult<Long, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                TakeProfitEntity.new { putInside(TakeProfit.default(entryId = entryId)) }
            }
            SonarResult.Success(result.id.value)
        } catch (e: Exception) {
            logger.error("Error creating take profit", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getStopLossById(id: Long): SonarResult<StopLoss, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                StopLossEntity.findById(id)?.toDomainModel()
            }
            if (result != null) {
                SonarResult.Success(result)
            } else {
                SonarResult.Error(UserDataError.NotFound)
            }
        } catch (e: Exception) {
            logger.error("Error getting stop loss by id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getTakeProfitById(id: Long): SonarResult<TakeProfit, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                TakeProfitEntity.findById(id)?.toDomainModel()
            }
            if (result != null) {
                SonarResult.Success(result)
            } else {
                SonarResult.Error(UserDataError.NotFound)
            }
        } catch (e: Exception) {
            logger.error("Error getting take profit by id", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun deleteStopLoss(id: Long): SonarResult<Unit, UserDataError> {
        return try {
            var found = false
            databaseConnector.suspendTransaction {
                val entity = StopLossEntity.findById(id)
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
            logger.error("Error deleting stop loss", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun deleteTakeProfit(id: Long): SonarResult<Unit, UserDataError> {
        return try {
            var found = false
            databaseConnector.suspendTransaction {
                val entity = TakeProfitEntity.findById(id)
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
            logger.error("Error deleting take profit", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun getPortfolioWithEntriesById(id: Long): SonarResult<PortfolioWithEntries, UserDataError> {
        return try {
            val result = databaseConnector.suspendTransaction {
                PortfolioEntity.findById(id)
                    ?.load(
                        PortfolioEntity::entries,
                        PortfolioEntryEntity::stopLosses,
                        PortfolioEntryEntity::takeProfits
                    )
                    ?.let { portfolioEntity ->
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
                val portfolios = PortfolioEntity.all().with(
                    PortfolioEntity::entries,
                    PortfolioEntryEntity::stopLosses,
                    PortfolioEntryEntity::takeProfits
                )
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

    override suspend fun getPortfolioRule(portfolioId: Long): SonarResult<LightPortfolioWithRule, UserDataError> {
        return try {
            val res = databaseConnector.suspendTransaction {
                val portfolio = PortfolioEntity.findById(portfolioId)
                    ?: return@suspendTransaction null

                var rule = portfolio.rules.firstOrNull()?.toDomainModel()
                if (rule == null) {
                    rule = RulesEntity.new {
                        pitInside(PortfolioRule.default(portfolioId = portfolioId))
                    }.toDomainModel()
                }

                return@suspendTransaction portfolio.toLightPortfolioWithRule(rule)
            }
            if (res != null) {
                SonarResult.Success(res)
            } else {
                SonarResult.Error(UserDataError.NotFound)
            }
        } catch (e: Exception) {
            logger.error("Error getting portfolio rule", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }

    override suspend fun updatePortfolioRule(rule: PortfolioRule): SonarResult<LightPortfolioWithRule, UserDataError> {
        return try {
            databaseConnector.suspendTransaction {
                val old = PortfolioEntity.findById(rule.portfolioId)
                    ?: return@suspendTransaction SonarResult.Error(UserDataError.NotFound)

                val newRule = RulesEntity.findByIdAndUpdate(rule.id) { entity ->
                    entity.pitInside(rule)
                }?.toDomainModel()
                if (newRule == null) {
                    SonarResult.Error(UserDataError.NotFound)
                } else {
                    SonarResult.Success(old.toLightPortfolioWithRule(newRule))
                }
            }
        } catch (e: Exception) {
            logger.error("Error updating portfolio rule", e)
            SonarResult.Error(UserDataError.UnknownError(e))
        }
    }
}