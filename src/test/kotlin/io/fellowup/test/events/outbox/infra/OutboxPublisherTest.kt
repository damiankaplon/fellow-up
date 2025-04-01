package io.fellowup.test.events.outbox.infra

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.fellowup.db.ExposedTransactionalRunner
import io.fellowup.events.Topic
import io.fellowup.events.outbox.infra.OutboxDao
import io.fellowup.events.outbox.infra.OutboxPublisher
import io.fellowup.test.DatabaseIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

internal class OutboxPublisherTest : DatabaseIntegrationTest() {

    data class TestEvent(val property: String)

    private val testee = OutboxPublisher<TestEvent>(
        ExposedTransactionalRunner(super.db),
        Topic("io.fellowup.test.topic")
    )

    @Test
    fun `should save event to outbox`() = test {
        // given
        val event = TestEvent("integration-test-event")

        // when
        testee.publish(event)

        // then
        val result = OutboxDao.all()
        assertThat(result).singleElement().satisfies(
            { assertThat(it.message).isNotNull },
            { assertThat(it.destination).isEqualTo("io.fellowup.test.topic") }
        )
    }

    @Test
    fun `should be able to serialize to string outbox entry message given jackson object mapper`() = test {
        // given
        val objectMapper = jacksonObjectMapper()
        val event = TestEvent("integration-test-event")

        // when
        testee.publish(event)
        val result = OutboxDao.all().single().message.let(objectMapper::writeValueAsString)

        // then
        assertThat(result).isEqualTo("{\"property\":\"integration-test-event\"}")
    }
}
