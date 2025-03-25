package io.fellowup.test.kafka.infra

import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import java.util.*

class KafkaTestUtils(
    val admin: Admin,
    val kafkaProducer: KafkaProducer<String, Any>,
    val kafkaConsumer: KafkaConsumer<String, String>
)

private object KafkaTestUtilsHolder {
    val properties = Properties()

    init {
        properties.load(object {}.javaClass.classLoader.getResourceAsStream("application-integration.properties"))
    }

    val ADMIN: Admin = Admin.create(properties)
    val KAFKA_PRODUCER = KafkaProducer<String, Any>(properties)
    val KAFKA_CONSUMER = KafkaConsumer<String, String>(properties)
}

fun withKafkaTestUtils(test: KafkaTestUtils.() -> Unit) {
    val properties = Properties()
    properties.load(object {}.javaClass.classLoader.getResourceAsStream("application-integration.properties"))
    KafkaTestUtils(
        KafkaTestUtilsHolder.ADMIN,
        KafkaTestUtilsHolder.KAFKA_PRODUCER,
        KafkaTestUtilsHolder.KAFKA_CONSUMER
    ).test()
}
