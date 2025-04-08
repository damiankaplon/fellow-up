package io.fellowup.infrastructure.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

internal abstract class DatabaseIntegrationTest {
    protected val db = TestDatabaseConnectionProvider.provide()

    protected fun test(test: suspend Transaction.() -> Unit): Unit = runBlocking {
        newSuspendedTransaction(Dispatchers.Default, db = db) {
            try {
                test()
            } catch (e: Throwable) {
                throw e
            } finally {
                rollback()
            }
        }
    }
}
