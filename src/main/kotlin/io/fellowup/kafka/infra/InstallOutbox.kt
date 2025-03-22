package io.fellowup.kafka.infra

import io.ktor.events.*
import io.ktor.server.application.*
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

fun Events.installOutbox(kafkaOutboxService: KafkaOutboxService) {
    val logger = KtorSimpleLogger("outbox-service-publisher")
    val exceptionHandler = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
        logger.error("Coroutine error", throwable)
    }
    val outboxCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + exceptionHandler)
    subscribe(ApplicationStarted) {
        outboxCoroutineScope.launch {
            while (true) {
                delay(100.milliseconds)
                kafkaOutboxService.publishOutbox()
            }
        }
    }
    subscribe(ApplicationStopped) {
        outboxCoroutineScope.cancel()
    }
}