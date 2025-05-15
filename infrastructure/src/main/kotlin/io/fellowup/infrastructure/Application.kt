package io.fellowup.infrastructure

import io.fellowup.domain.events.Topic
import io.fellowup.infrastructure.kafka.installOutbox
import io.fellowup.infrastructure.kafka.ktor.consume
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
    val appComponent = DaggerFellowUpAppComponent.builder()
        .applicationConfig(environment.config)
        .build()

    consume(
        appComponent.transactionalRunner(),
        appComponent.matchmakingCreatedEventConsumer(),
        Topic("io.fellowup.matchmaking.domain.matchmakingCreated")
    )
    monitor.installOutbox(appComponent.kafkaOutboxService())
    routing {
        val oAuthModule = installOAuthAuth()
        installAppRouting(
            oAuthModule.securedRouting,
            appComponent.matchmakingController(),
            appComponent.mediationController()
        )
    }
}
