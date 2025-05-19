package io.fellowup.infrastructure.kafka

import dagger.Module
import dagger.Provides
import io.fellowup.infrastructure.db.ExposedTransactionalRunner
import io.fellowup.infrastructure.kafka.serialization.infra.JacksonKObjectSerializer
import io.ktor.server.config.*
import jakarta.inject.Singleton
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.slf4j.LoggerFactory
import javax.sql.DataSource

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
        dataSource: DataSource,
        kafkaProducer: KafkaProducer<String, Any>
    ): KafkaOutboxService {

        val transactionalRunner = ExposedTransactionalRunner(
            Database.connect(
                datasource = dataSource,
                databaseConfig = DatabaseConfig { sqlLogger = KafkaOutboxServiceSqlLogger }
            )
        )
        return KafkaOutboxService(transactionalRunner, kafkaProducer)
    }

    private object KafkaOutboxServiceSqlLogger : SqlLogger {
        private val logger = LoggerFactory.getLogger(KafkaOutboxServiceSqlLogger::class.simpleName)
        override fun log(context: StatementContext, transaction: Transaction) {
            if (logger.isDebugEnabled) {
                logger.debug(context.expandArgs(TransactionManager.current()))
            }
        }
    }
}
