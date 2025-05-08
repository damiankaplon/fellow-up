package io.fellowup.infrastructure.mediation.readmodel

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.Mediation
import io.fellowup.domain.mediation.readmodel.Mediations
import io.fellowup.domain.mediation.readmodel.Proposal
import io.fellowup.infrastructure.mediation.JSONB_PARTICIPANTS_DESERIALIZER
import io.fellowup.infrastructure.mediation.JSONB_PARTICIPANTS_SERIALIZER
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.contains
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.not
import java.time.Instant
import java.util.*

class MediationsExposed : Mediations {

    override suspend fun findNotFinishedByParticipant(participantId: ParticipantId): Set<Mediation> {
        data class Row(
            val mediationId: UUID,
            val category: String,
            val participants: Set<ParticipantId>,
            val proposalId: UUID?,
            val orderNumber: Int?,
            val time: Instant?,
            val longitude: Double?,
            val latitude: Double?,
            val acceptedBy: Int?
        )
        return (MediationTable leftJoin ActivityProposalTable).select(
            MediationTable.id,
            MediationTable.category,
            MediationTable.participants,
            ActivityProposalTable.mediationId,
            ActivityProposalTable.id,
            ActivityProposalTable.orderNumber,
            ActivityProposalTable.time,
            ActivityProposalTable.longitude,
            ActivityProposalTable.latitude,
            ActivityProposalTable.acceptedByParticipantIds,
        ).where {
            MediationTable.participants.contains(setOf(participantId)) and
                    not(MediationTable.isFinished)
        }.mapTo(linkedSetOf()) {
            Row(
                mediationId = it[MediationTable.id].value,
                category = it[MediationTable.category],
                participants = it[MediationTable.participants],
                proposalId = it.getOrNull(ActivityProposalTable.id)?.value,
                orderNumber = it.getOrNull(ActivityProposalTable.orderNumber),
                time = it.getOrNull(ActivityProposalTable.time),
                longitude = it.getOrNull(ActivityProposalTable.longitude),
                latitude = it.getOrNull(ActivityProposalTable.latitude),
                acceptedBy = it.getOrNull(ActivityProposalTable.acceptedByParticipantIds)?.size
            )
        }.groupBy(Row::mediationId)
            .mapTo(linkedSetOf()) { (mediationId: UUID, rows: List<Row>) ->
                object : Mediation {
                    override val id: UUID = mediationId
                    override val category: String = rows.map(Row::category).toSet().single()
                    override val participantIds: Set<ParticipantId> = rows.flatMap(Row::participants).toSet()
                    override val proposals: Set<Proposal> = rows.mapNotNullTo(linkedSetOf()) { row ->
                        if (row.proposalId == null) return@mapNotNullTo null
                        object : Proposal {
                            override val acceptedBy: Int = row.acceptedBy!!
                            override val location: Location = Location(
                                longitude = row.longitude!!,
                                latitude = row.latitude!!
                            )
                        }
                    }
                }
            }
    }
}

private object MediationTable : UUIDTable("mediation") {
    val category = text("category")
    val participants = jsonb("participants", JSONB_PARTICIPANTS_SERIALIZER, JSONB_PARTICIPANTS_DESERIALIZER)
    val isFinished = bool("is_finished")
}

private object ActivityProposalTable : UUIDTable("activity_proposal") {
    val mediationId = reference("mediation_id", MediationTable)
    val orderNumber = integer("order_number")
    val time = timestamp("time")
    val longitude = double("longitude")
    val latitude = double("latitude")
    val acceptedByParticipantIds =
        jsonb("accepted_by_participant_ids", JSONB_PARTICIPANTS_SERIALIZER, JSONB_PARTICIPANTS_DESERIALIZER)
}
