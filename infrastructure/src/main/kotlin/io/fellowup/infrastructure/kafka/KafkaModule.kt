package io.fellowup.infrastructure.kafka

import dagger.Module
import dagger.Provides
import io.fellowup.domain.db.TransactionalRunner
import io.fellowup.infrastructure.kafka.infra.KafkaOutboxService
import io.fellowup.infrastructure.kafka.serialization.infra.JacksonKObjectSerializer
import io.ktor.server.config.*
import jakarta.inject.Singleton
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig

@Module
class KafkaModule {

    @Provides
    @Singleton
    fun provideKafkaProducer(ktorAppConfig: ApplicationConfig): KafkaProducer<String, Any> {
        val bootstrapServers = ktorAppConfig.property("kafka.bootstrapServers").getString()
        val clientName = ktorAppConfig.property("kafka.clientName").getString()
        val keySerializer = ktorAppConfig.propertyOrNull("kafka.keySerializer")?.getString()
            ?: "org.apache.kafka.common.serialization.StringSerializer"
        val valueSerializer =
            ktorAppConfig.propertyOrNull("kafka.valueSerializer")?.getString()
                ?: JacksonKObjectSerializer::class.qualifiedName
        val properties = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to keySerializer,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to valueSerializer,
            ProducerConfig.CLIENT_ID_CONFIG to clientName
        )
        return KafkaProducer<String, Any>(properties)
    }

    @Provides
    @Singleton
    fun provideKafkaOutboxService(
        transactionalRunner: TransactionalRunner,
        kafkaProducer: KafkaProducer<String, Any>
    ): KafkaOutboxService = KafkaOutboxService(transactionalRunner, kafkaProducer)
}
