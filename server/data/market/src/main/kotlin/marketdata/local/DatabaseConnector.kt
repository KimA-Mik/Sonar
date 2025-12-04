package ru.kima.sonar.server.data.market.marketdata.local

import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction

internal interface DatabaseConnector {
    fun <T> transaction(block: JdbcTransaction.() -> T): T
    suspend fun <T> suspendTransaction(block: suspend JdbcTransaction.() -> T): T
    fun <T> transactionCatching(block: Transaction.() -> T): Result<T> =
        runCatching { transaction { block() } }

    suspend fun <T> suspendTransactionCatching(block: suspend Transaction.() -> T): Result<T> =
        runCatching { suspendTransaction { block() } }
}