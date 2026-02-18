package ru.kima.sonar.server.data.market.marketdata.local

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.transactions.transactionManager
import java.sql.Connection

internal class SqliteDatabaseConnector(
    dbName: String,
) : DatabaseConnector {
    private val connection: Database = Database.connect("jdbc:sqlite:$dbName", "org.sqlite.JDBC")

    init {
        connection.transactionManager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    override fun <T> transaction(block: JdbcTransaction.() -> T): T =
        transaction(connection) { block() }

    override suspend fun <T> suspendTransaction(block: suspend JdbcTransaction.() -> T): T =
        suspendTransaction(connection) { block() }
}