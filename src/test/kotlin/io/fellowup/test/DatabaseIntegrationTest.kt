package io.fellowup.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal abstract class DatabaseIntegrationTest {
    protected val db = TestDatabaseConnectionProvider.provide()

    protected fun test(test: suspend Transaction.() -> Unit) = runBlocking {
        newSuspendedTransaction(Dispatchers.Default, db = db) { test(); rollback() }
    }
}
