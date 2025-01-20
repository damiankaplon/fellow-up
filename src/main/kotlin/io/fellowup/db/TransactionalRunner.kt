package io.fellowup.db

import org.jetbrains.exposed.sql.Database

interface TransactionalRunner {

    fun <T> transaction(
        isolation: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE,
        readOnly: Boolean = false,
        block: () -> T
    ): T
}

class ExposedTransactionalRunner(
    private val database: Database
) : TransactionalRunner {
    override fun <T> transaction(isolation: Int, readOnly: Boolean, block: () -> T): T =
        org.jetbrains.exposed.sql.transactions.transaction(isolation, readOnly, database) {
            block()
        }
}
