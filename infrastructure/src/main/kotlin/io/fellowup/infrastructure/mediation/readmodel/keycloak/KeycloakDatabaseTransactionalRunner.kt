package io.fellowup.infrastructure.mediation.readmodel.keycloak

import io.fellowup.domain.db.TransactionalRunner

open class KeycloakDatabaseTransactionalRunner(
    private val exposedTransactionalRunner: TransactionalRunner
) {

    suspend fun <T> readOnlyTransaction(block: suspend () -> T): T {
        return exposedTransactionalRunner.transaction(readOnly = true, block = block)
    }
}
