package io.fellowup.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface TransactionalRunner {

    suspend fun <T> transaction(
        isolation: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE,
        readOnly: Boolean = false,
        block: suspend () -> T
    ): T
}

class ExposedTransactionalRunner(
    private val database: Database
) : TransactionalRunner {
    override suspend fun <T> transaction(isolation: Int, readOnly: Boolean, block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, readOnly = readOnly, db = database) {
            block()
        }
}
