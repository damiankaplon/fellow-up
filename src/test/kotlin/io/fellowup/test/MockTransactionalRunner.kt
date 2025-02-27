package io.fellowup.test

import io.fellowup.db.TransactionalRunner

internal class MockTransactionalRunner : TransactionalRunner {

    override suspend fun <T> transaction(isolation: Int, readOnly: Boolean, block: suspend () -> T): T {
        return block()
    }
}
