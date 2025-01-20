package io.fellowup.test

import io.fellowup.db.TransactionalRunner
import org.jetbrains.exposed.sql.Database

internal class RollbackTransactionalRunner(
    private val database: Database
) : TransactionalRunner {
    override fun <T> transaction(isolation: Int, readOnly: Boolean, block: () -> T): T =
        org.jetbrains.exposed.sql.transactions.transaction(isolation, readOnly, database) {
            val result = block()
            rollback()
            result
        }
}