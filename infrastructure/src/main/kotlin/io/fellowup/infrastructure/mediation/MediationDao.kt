package io.fellowup.infrastructure.mediation


import io.fellowup.domain.mediation.Mediation
import io.fellowup.infrastructure.db.reflection.setPrivateProperty
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.json.jsonb
import java.util.*

internal class MediationDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MediationDao>(MediationsTable)

    var category by MediationsTable.category
    var isFinished by MediationsTable.isFinished
    var participants by MediationsTable.participants

    val proposals by ActivityProposalDao referrersOn ActivityProposalDao.ActivityProposalsTable.mediation

    fun toDomain(): Mediation {
        val mediation = Mediation(
            id = Mediation.Id(id.value),
            category = category,
            participants = participants
        )

        setPrivateProperty(mediation, Mediation::proposals.name, proposals.map(ActivityProposalDao::toDomain).toSet())
        setPrivateProperty(mediation, Mediation::isFinished.name, isFinished)

        return mediation
    }

    object MediationsTable : UUIDTable("mediation") {
        val category = text("category")
        val isFinished = bool("is_finished")
        val participants = jsonb("participants", JSONB_PARTICIPANTS_SERIALIZER, JSONB_PARTICIPANTS_DESERIALIZER)
    }
}
