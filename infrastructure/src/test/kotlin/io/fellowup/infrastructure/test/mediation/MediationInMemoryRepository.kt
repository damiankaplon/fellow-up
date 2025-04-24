package io.fellowup.infrastructure.test.mediation

import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.MediationRepository

internal class MediationInMemoryRepository : MediationRepository {

    private val mediations = mutableSetOf<Mediation>()

    override suspend fun save(mediation: Mediation): Mediation {
        mediations.removeIf { it.id == mediation.id }
        mediations.add(mediation)
        return mediation
    }

    override suspend fun findById(id: Mediation.Id): Mediation? {
        return mediations.find { it.id == id }
    }
}