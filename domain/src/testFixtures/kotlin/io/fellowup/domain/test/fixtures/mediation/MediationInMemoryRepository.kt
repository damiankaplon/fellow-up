package io.fellowup.domain.test.fixtures.mediation

import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.MediationRepository

class MediationInMemoryRepository : MediationRepository {

    private val _mediations = mutableSetOf<Mediation>()
    val mediations: Set<Mediation>; get() = _mediations.toSet()

    override suspend fun save(mediation: Mediation): Mediation {
        _mediations.removeIf { it.id == mediation.id }
        _mediations.add(mediation)
        return mediation
    }

    override suspend fun findById(id: Mediation.Id): Mediation? {
        return _mediations.find { it.id == id }
    }
}