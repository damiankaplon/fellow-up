package io.fellowup.infrastructure.test.kafka

import io.fellowup.domain.test.fixtures.db.MockTransactionalRunner
import io.fellowup.infrastructure.kafka.KafkaConsumerThread
import io.fellowup.infrastructure.kafka.KafkaRecordConsumer
import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

internal class KafkaRecordsConsumingIntegrationTest {

    private val testTopic = "io.fellowup.integration-test.${this::class.simpleName}"

    private val admin = Admin.create(ApplicationIntegrationTestPropertiesLoader.PROPERTIES)
    private val kafkaProducer = KafkaProducer<String, Any>(ApplicationIntegrationTestPropertiesLoader.PROPERTIES)
    private lateinit var kafkaConsumerThread: KafkaConsumerThread

    @BeforeTest
    fun setUp() {
        admin.createTopics(listOf(NewTopic(testTopic, 1, 1))).all().get()
    }

    @AfterTest
    fun tearDown() {
        admin.deleteTopics(listOf(testTopic))
        kafkaConsumerThread.interrupt()
    }

    data class TestEvent(val property: String)

    @Test
    fun `should consume event`() {
        // given
        var consumedEvents: List<String> = emptyList()
        val kafkaRecordConsumer = object : KafkaRecordConsumer {
            override val topic: String = testTopic
            override suspend fun consume(record: ConsumerRecord<String, String>) {
                consumedEvents = consumedEvents + record.value()
            }
        }
        kafkaConsumerThread = KafkaConsumerThread(
            ApplicationIntegrationTestPropertiesLoader.PROPERTIES,
            kafkaRecordConsumer,
            MockTransactionalRunner()
        )
        kafkaConsumerThread.start()

        // when
        kafkaProducer.send(ProducerRecord(testTopic, TestEvent("test"))).get()

        // then
        await atMost 10.seconds untilAsserted {
            assertThat(consumedEvents).hasSize(1)
            assertThat(consumedEvents[0]).contains("property", "test")
        }
        kafkaConsumerThread.close()
        await atMost 10.seconds untilAsserted {
            assertThat(kafkaConsumerThread.isAlive).isFalse()
        }
    }
}
