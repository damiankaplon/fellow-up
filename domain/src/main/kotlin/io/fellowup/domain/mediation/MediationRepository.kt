package io.fellowup.domain.mediation

interface MediationRepository {

    suspend fun save(mediation: Mediation): Mediation
    suspend fun findById(id: Mediation.Id): Mediation?
}
