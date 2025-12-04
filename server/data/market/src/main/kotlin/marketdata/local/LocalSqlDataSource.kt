package ru.kima.sonar.server.data.market.marketdata.local

import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import ru.kima.sonar.common.serverapi.model.Candle
import ru.kima.sonar.common.serverapi.model.CandleInterval
import ru.kima.sonar.server.data.market.marketdata.local.model.entities.CandleEntity
import ru.kima.sonar.server.data.market.marketdata.local.model.mappers.toCandle
import ru.kima.sonar.server.data.market.marketdata.local.model.tables.CandleTable
import kotlin.time.ExperimentalTime

internal class LocalSqlDataSource(
    private val databaseConnector: DatabaseConnector
) : LocalDataSource {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        databaseConnector.transaction {
            val tables = arrayOf(CandleTable)
            SchemaUtils.create(*tables)
            val missingColumnsStatements =
                MigrationUtils.statementsRequiredForDatabaseMigration(*tables)
            missingColumnsStatements.forEach {
                logger.info("Executing statement: $it")
                try {
                    connection.prepareStatement(it, true).executeUpdate()
                } catch (e: Exception) {
                    logger.error(e.message)
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun getCandles(uuid: String, candleInterval: CandleInterval): List<Candle> =
        databaseConnector.suspendTransaction {
            CandleEntity
                .find { CandleTable.instrumentUid eq uuid and (CandleTable.interval eq candleInterval) }
                .orderBy(CandleTable.time to SortOrder.ASC)
                .map { it.toCandle() }
                .toList()
        }
}