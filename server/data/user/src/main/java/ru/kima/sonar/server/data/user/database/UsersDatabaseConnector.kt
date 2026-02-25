package ru.kima.sonar.server.data.user.database

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.transactions.transactionManager
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory
import ru.kima.sonar.server.common.util.databaseutil.DatabaseConnector
import ru.kima.sonar.server.data.user.scema.SessionTable
import ru.kima.sonar.server.data.user.scema.UserTable
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryTable
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioTable
import java.sql.Connection

internal class UsersDatabaseConnector(dbName: String) : DatabaseConnector {
    private val connection: Database = Database.connect("jdbc:sqlite:$dbName", "org.sqlite.JDBC")

    init {
        connection.transactionManager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        val logger = LoggerFactory.getLogger(this::class.java)
        this.transaction {
            val tables = arrayOf(UserTable, SessionTable, PortfolioTable, PortfolioEntryTable)
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

    override fun <T> transaction(block: JdbcTransaction.() -> T): T =
        transaction(connection) { block() }

    override suspend fun <T> suspendTransaction(block: suspend JdbcTransaction.() -> T): T =
        suspendTransaction(connection) { block() }
}