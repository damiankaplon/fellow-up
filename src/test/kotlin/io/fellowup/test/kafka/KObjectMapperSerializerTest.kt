package io.fellowup.test.kafka

import io.fellowup.kafka.serialization.infra.JacksonKObjectSerializer
import org.assertj.core.api.Assertions.assertThat
import java.util.*
import kotlin.test.Test

internal class KObjectMapperSerializerTest {

    private val testee = JacksonKObjectSerializer()

    @Test
    fun `should serialize to json string`() {
        // given
        data class TestEvent(val property: String)

        val event = TestEvent("test")

        // when
        val result: ByteArray? = testee.serialize("topic", event)

        // then
        val resultString = String(result!!)
        assertThat(resultString).isEqualTo("{\"property\":\"test\"}")
    }

    @Test
    fun `should serialize kotlin uuid to string`() {
        // given
        data class TestEvent(val uuid: UUID)

        val event = TestEvent(UUID.randomUUID())

        // when
        val result: ByteArray? = testee.serialize("topic", event)

        // then
        val resultString = String(result!!)
        assertThat(resultString).isEqualTo("{\"uuid\":\"${event.uuid}\"}")
    }

    interface TestEvent {
        val id: Long
    }

    @Test
    fun `should serialize origin class given class extending interface`() {
        // given
        data class TestEventImpl(override val id: Long, val property: String) : TestEvent

        val event = TestEventImpl(1, "test")

        // when
        val result: ByteArray? = testee.serialize("topic", event)

        // then
        val resultString = String(result!!)
        assertThat(resultString).isEqualTo("{\"id\":1,\"property\":\"test\"}")
    }

}