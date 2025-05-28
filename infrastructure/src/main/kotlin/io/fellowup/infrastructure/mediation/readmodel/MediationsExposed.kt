package io.fellowup.infrastructure.mediation.readmodel

import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.mediation.ParticipantId
import io.fellowup.domain.mediation.readmodel.Mediation
import io.fellowup.domain.mediation.readmodel.Mediations
import io.fellowup.domain.mediation.readmodel.Proposal
import io.fellowup.infrastructure.mediation.JSONB_PARTICIPANTS_DESERIALIZER
import io.fellowup.infrastructure.mediation.JSONB_PARTICIPANTS_SERIALIZER
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.contains
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.not
import java.time.Instant
import java.util.*
import io.fellowup.domain.mediation.Mediation as MediationDomain

private val MEDIATION_READ_MODEL_DB_COLUMNS = listOf<Expression<*>>(
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
)

class MediationsExposed(
    private val transactionalRunner: TransactionalRunner
) : Mediations {


    override suspend fun findNotFinishedByParticipant(participantId: ParticipantId): Set<Mediation> =
        transactionalRunner.transaction(readOnly = true) {
            return@transaction (MediationTable leftJoin ActivityProposalTable).select(MEDIATION_READ_MODEL_DB_COLUMNS)
                .where {
                    MediationTable.participants.contains(setOf(participantId)) and
                            not(MediationTable.isFinished)
                }.mapTo(linkedSetOf(), ::DatabaseRow)
                .groupBy(DatabaseRow::mediationId)
                .mapTo(linkedSetOf()) { (mediationId: UUID, relatedDatabaseRows: List<DatabaseRow>) ->
                    Mediation(mediationId, relatedDatabaseRows)
                }
        }

    override suspend fun findById(id: MediationDomain.Id): Mediation? =
        transactionalRunner.transaction(readOnly = true) {
            return@transaction (MediationTable leftJoin ActivityProposalTable).select(MEDIATION_READ_MODEL_DB_COLUMNS)
                .where { MediationTable.id eq id.value }
                .mapTo(linkedSetOf(), ::DatabaseRow)
                .groupBy(DatabaseRow::mediationId)
                .entries.find { it.key == id.value }
                ?.let { (mediationId: UUID, relatedDatabaseRows: List<DatabaseRow>) ->
                    Mediation(mediationId, relatedDatabaseRows)
                }
        }

    private data class DatabaseRow(
        val mediationId: UUID,
        val category: String,
        val participants: Set<ParticipantId>,
        val proposalId: UUID?,
        val acceptedByParticipantIds: Set<ParticipantId>?,
        val orderNumber: Int?,
        val time: Instant?,
        val longitude: Double?,
        val latitude: Double?,
        val acceptedBy: Int?
    )

    private fun DatabaseRow(resultRow: ResultRow): DatabaseRow {
        return DatabaseRow(
            mediationId = resultRow[MediationTable.id].value,
            category = resultRow[MediationTable.category],
            participants = resultRow[MediationTable.participants],
            proposalId = resultRow.getOrNull(ActivityProposalTable.id)?.value,
            acceptedByParticipantIds = resultRow[ActivityProposalTable.acceptedByParticipantIds],
            orderNumber = resultRow.getOrNull(ActivityProposalTable.orderNumber),
            time = resultRow.getOrNull(ActivityProposalTable.time),
            longitude = resultRow.getOrNull(ActivityProposalTable.longitude),
            latitude = resultRow.getOrNull(ActivityProposalTable.latitude),
            acceptedBy = resultRow.getOrNull(ActivityProposalTable.acceptedByParticipantIds)?.size
        )
    }

    private fun Mediation(mediationId: UUID, relatedDatabaseRows: List<DatabaseRow>): Mediation {
        return object : Mediation {
            override val id: UUID = mediationId
            override val category: String =
                relatedDatabaseRows.map(DatabaseRow::category).toSet().single()
            override val participantIds: Set<ParticipantId> =
                relatedDatabaseRows.flatMap(DatabaseRow::participants).toSet()
            override val proposals: Set<Proposal> =
                relatedDatabaseRows.mapNotNullTo(linkedSetOf()) { row ->
                    if (row.proposalId == null) return@mapNotNullTo null
                    object : Proposal {
                        override val acceptedBy: Set<ParticipantId> = row.acceptedByParticipantIds ?: emptySet()
                        override val location: Location = Location(
                            longitude = row.longitude!!,
                            latitude = row.latitude!!
                        )
                        override val time: Instant = row.time!!
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
