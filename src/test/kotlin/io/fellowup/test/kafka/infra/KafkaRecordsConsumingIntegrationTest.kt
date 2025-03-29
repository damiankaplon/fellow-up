package io.fellowup.test.kafka.infra

import io.fellowup.kafka.infra.KafkaConsumerThread
import io.fellowup.kafka.infra.KafkaRecordConsumer
import io.fellowup.test.MockTransactionalRunner
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
            override fun consume(record: ConsumerRecord<String, String>) {
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
            assertThat(kafkaConsumerThread.isInterrupted).isTrue()
            assertThat(kafkaConsumerThread.isAlive).isFalse().withFailMessage {
                """
                    Kafka consumer thread was not closed for the test. Next test run may fail.
                    It is necessary for kafka cluster to record that consumer got closed.
                    Consumer is closed when consumer thread is interrupted.
                """.trimIndent()
            }
        }
    }
}
