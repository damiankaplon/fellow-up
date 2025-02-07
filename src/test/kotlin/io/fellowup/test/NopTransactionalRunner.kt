package io.fellowup.test

import io.fellowup.db.TransactionalRunner

internal class NopTransactionalRunner : TransactionalRunner {

    override fun <T> transaction(isolation: Int, readOnly: Boolean, block: () -> T): T  =
        block()
}