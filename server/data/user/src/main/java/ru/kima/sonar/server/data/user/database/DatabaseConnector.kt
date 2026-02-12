package ru.kima.sonar.server.data.user.database

import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transactionManager
import java.sql.Connection

class DatabaseConnector(dbName: String) {
    private val connection: Database = Database.connect("jdbc:sqlite:$dbName.db", "org.sqlite.JDBC")

    init {
        connection.transactionManager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    suspend fun <T> transaction(block: suspend Transaction.() -> T): T =
        suspendTransaction(connection) { block() }

    suspend fun <T> transactionCatching(block: suspend Transaction.() -> T): Result<T> =
        runCatching { transaction { block() } }
}