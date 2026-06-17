package ru.kima.sonar.server.data.market.marketdata.local

import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import ru.kima.sonar.common.serverapi.model.Candle
import ru.kima.sonar.common.serverapi.model.CandleInterval
import ru.kima.sonar.server.common.util.databaseutil.DatabaseConnector
import ru.kima.sonar.server.data.market.marketdata.local.model.entities.CandleEntity
import ru.kima.sonar.server.data.market.marketdata.local.model.mappers.putInside
import ru.kima.sonar.server.data.market.marketdata.local.model.mappers.toCandle
import ru.kima.sonar.server.data.market.marketdata.local.model.tables.CandleTable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
    override suspend fun getCandles(
        ticker: String,
        interval: CandleInterval
    ): List<Candle> = databaseConnector.suspendTransaction {
        CandleEntity
            .find { (CandleTable.ticker eq ticker) and (CandleTable.interval eq interval) }
            .orderBy(CandleTable.timestamp to SortOrder.ASC)
            .map { it.toCandle() }
            .toList()
    }

    override suspend fun getCandle(
        ticker: String,
        interval: CandleInterval,
        time: Instant
    ): Candle? {
        return databaseConnector.suspendTransaction {
            CandleEntity
                .find { (CandleTable.ticker eq ticker) and (CandleTable.interval eq interval) and (CandleTable.timestamp eq time.epochSeconds) }
                .firstOrNull()
                ?.toCandle()
        }
    }

    override suspend fun getUnfinishedCandle(
        ticker: String,
        interval: CandleInterval
    ): List<Candle> = databaseConnector.suspendTransaction {
        CandleEntity
            .find { (CandleTable.ticker eq ticker) and (CandleTable.interval eq interval) and (CandleTable.isComplete eq false) }
            .orderBy(CandleTable.timestamp to SortOrder.ASC)
            .map { it.toCandle() }
            .toList()
    }

    override suspend fun saveCandles(
        candles: List<Candle>
    ): List<Candle> = databaseConnector.suspendTransaction {
        buildList {
            candles.forEach { candle ->
                val candle = CandleEntity.new {
                    putInside(candle)
                }
                add(candle.toCandle())
            }
        }
    }

    override suspend fun saveCandle(
        candle: Candle
    ): Candle = databaseConnector.suspendTransaction {
        val candleEntity = CandleEntity.new {
            putInside(candle)
        }
        candleEntity.toCandle()
    }

    override suspend fun updateCandle(
        candle: Candle
    ): Candle? = databaseConnector.suspendTransaction {
        val candleEntity = CandleEntity.findById(candle.id)
        if (candleEntity == null) {
            logger.warn("Candle with id ${candle.id} not found for update")
            return@suspendTransaction null
        }
        candleEntity.putInside(candle)
        candleEntity.toCandle()
    }
}