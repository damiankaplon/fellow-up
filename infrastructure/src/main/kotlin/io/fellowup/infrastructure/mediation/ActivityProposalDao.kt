package io.fellowup.infrastructure.mediation

import io.fellowup.domain.matchmaking.Location
import io.fellowup.domain.mediation.ActivityProposal
import io.fellowup.infrastructure.db.reflection.setPrivateProperty
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb
import java.util.*

internal class ActivityProposalDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ActivityProposalDao>(ActivityProposalsTable)

    var order by ActivityProposalsTable.order
    var time by ActivityProposalsTable.time
    var longitude by ActivityProposalsTable.longitude
    var latitude by ActivityProposalsTable.latitude
    var acceptedByParticipantIds by ActivityProposalsTable.acceptedByParticipantIds
    var mediation by MediationDao referencedOn ActivityProposalsTable.mediation

    fun toDomain(): ActivityProposal {
        val proposal = ActivityProposal(
            order = ActivityProposal.Order(order),
            location = Location(longitude, latitude),
            time = time
        )
        setPrivateProperty(
            proposal,
            ActivityProposal::acceptedByParticipantIds.name,
            acceptedByParticipantIds
        )

        return proposal
    }

    object ActivityProposalsTable : UUIDTable("activity_proposal") {


        val order = integer("order_number")
        val mediation = reference("mediation_id", MediationDao.MediationsTable, onDelete = ReferenceOption.CASCADE)
        val time = timestamp("time")
        val longitude = double("longitude")
        val latitude = double("latitude")
        val acceptedByParticipantIds =
            jsonb("accepted_by_participant_ids", JSONB_PARTICIPANTS_SERIALIZER, JSONB_PARTICIPANTS_DESERIALIZER)
    }
}
