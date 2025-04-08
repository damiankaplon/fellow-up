package io.fellowup.domain.db

interface TransactionalRunner {

    suspend fun <T> transaction(
        isolation: Int = java.sql.Connection.TRANSACTION_SERIALIZABLE,
        readOnly: Boolean = false,
        block: suspend () -> T
    ): T
}
