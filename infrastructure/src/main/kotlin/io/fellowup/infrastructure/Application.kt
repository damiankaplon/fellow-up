package io.fellowup.infrastructure

import io.fellowup.domain.events.Topic
import io.fellowup.domain.matchmaking.MatchmakingEvent
import io.fellowup.infrastructure.db.createTransactionalRunner
import io.fellowup.infrastructure.db.installDatabase
import io.fellowup.infrastructure.events.outbox.infra.OutboxPublisher
import io.fellowup.infrastructure.kafka.infra.KafkaOutboxService
import io.fellowup.infrastructure.kafka.infra.installOutbox
import io.fellowup.infrastructure.kafka.infra.ktor.KafkaProducer
import io.fellowup.infrastructure.kafka.infra.ktor.consume
import io.fellowup.infrastructure.matchmaking.infra.createMatchmakingModule
import io.fellowup.infrastructure.mediation.readmodel.keycloak.KeycloakDatabaseTransactionalRunner
import io.fellowup.infrastructure.security.NoAuthenticatedSubjectExceptionHandler
import io.fellowup.infrastructure.security.NoJwtExceptionHandler
import io.fellowup.infrastructure.security.installOAuthAuth
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    installSerialization()
    install(StatusPages) {
        exception(NoJwtExceptionHandler)
        exception(NoAuthenticatedSubjectExceptionHandler)
    }
    val transactionalRunner = createTransactionalRunner(database = installDatabase())
    val matchmakingEventsPublisher = OutboxPublisher<MatchmakingEvent>(
        defaultTopic = Topic("io.fellowup.matchmaking.domain.matchmakingCreated"),
        transactionalRunner = transactionalRunner,
    )
    val matchmakingModule = createMatchmakingModule(
        transactionalRunner,
        KeycloakDatabaseTransactionalRunner(environment),
        matchmakingEventsPublisher
    )
    consume(
        transactionalRunner,
        matchmakingModule.matchmakingCreatedEventConsumer,
        Topic("io.fellowup.matchmaking.domain.matchmakingCreated")
    )
    val kafkaProducer = environment.config.KafkaProducer()
    monitor.installOutbox(KafkaOutboxService(transactionalRunner, kafkaProducer))
    routing {
        val oAuthModule = installOAuthAuth()
        installAppRouting(oAuthModule.securedRouting, matchmakingModule.matchmakingsController)
    }
}
