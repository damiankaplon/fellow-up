package io.fellowup.infrastructure.mediation

import io.fellowup.domain.mediation.ActivityProposal
import io.fellowup.domain.mediation.Mediation
import io.fellowup.domain.mediation.MediationRepository
import io.fellowup.infrastructure.db.reflection.getPrivateProperty
import io.fellowup.infrastructure.db.reflection.setPrivateProperty
import org.jetbrains.exposed.sql.SizedIterable

class MediationDaoRepository : MediationRepository {
    override suspend fun save(mediation: Mediation): Mediation {
        val entity: MediationDao = MediationDao.findByIdAndUpdate(mediation.id.value) { persist(mediation, it) }
            ?: MediationDao.new(id = mediation.id.value) { persist(mediation, this) }
        return entity.toDomain()
    }

    override suspend fun findById(id: Mediation.Id): Mediation? {
        val entity = MediationDao.findById(id.value)
        val proposals = entity?.proposals?.map { it.toDomain() }
        return entity?.toDomain()?.apply {
            setPrivateProperty(this, this::proposals.name, proposals?.toSet() ?: emptySet<ActivityProposal>())
        }
    }

    private fun persist(mediation: Mediation, entity: MediationDao) {
        entity.from(mediation)
        mediation.persistProposals(entity)
    }

    private fun Mediation.persistProposals(parent: MediationDao) {
        val proposals: SizedIterable<ActivityProposalDao> = parent.proposals.forUpdate()
        this.proposals.associateWith { proposal -> proposals.find { it.order == proposal.order.value } }
            .forEach { (domain: ActivityProposal, entity: ActivityProposalDao?) ->
                entity?.from(parent, domain) ?: ActivityProposalDao.new { this.from(parent, domain) }
            }
    }

    private fun MediationDao.from(mediation: Mediation) {
        this.category = mediation.category
        this.participants = mediation.getPrivateProperty(MediationDao.MediationsTable.participants.name)
        this.isFinished = mediation.isFinished
    }

    private fun ActivityProposalDao.from(parent: MediationDao, activityProposal: ActivityProposal) {
        this.order = activityProposal.order.value
        this.time = activityProposal.time
        this.acceptedByParticipantIds = activityProposal.acceptedByParticipantIds
        this.latitude = activityProposal.location.latitude
        this.longitude = activityProposal.location.longitude
        this.mediation = parent
    }
}
