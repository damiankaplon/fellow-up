package io.fellowup

import io.fellowup.db.createTransactionalRunner
import io.fellowup.db.installDatabase
import io.fellowup.events.Topic
import io.fellowup.events.outbox.infra.OutboxPublisher
import io.fellowup.kafka.infra.KafkaOutboxService
import io.fellowup.kafka.infra.installOutbox
import io.fellowup.kafka.infra.ktor.KafkaProducer
import io.fellowup.kafka.infra.ktor.consume
import io.fellowup.matchmaking.MatchmakingEvent
import io.fellowup.matchmaking.infra.createMatchmakingModule
import io.fellowup.security.NoAuthenticatedSubjectExceptionHandler
import io.fellowup.security.NoJwtExceptionHandler
import io.fellowup.security.installOAuthAuth
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
    val db = installDatabase()
    val transactionalRunner = createTransactionalRunner(db)
    val oAuthModule = installOAuthAuth()
    val matchmakingEventsPublisher = OutboxPublisher<MatchmakingEvent>(
        defaultTopic = Topic("io.fellowup.matchmaking.domain.matchmakingCreated"),
        transactionalRunner = transactionalRunner,
    )
    val matchmakingModule = createMatchmakingModule(transactionalRunner, matchmakingEventsPublisher)
    consume(
        transactionalRunner,
        matchmakingModule.matchmakingCreatedEventConsumer,
        Topic("io.fellowup.matchmaking.domain.matchmakingCreated")
    )
    val kafkaProducer = environment.config.KafkaProducer()
    monitor.installOutbox(KafkaOutboxService(transactionalRunner, kafkaProducer))
    routing {
        installAppRouting(oAuthModule.securedRouting, matchmakingModule.matchmakingsController)
    }
}
