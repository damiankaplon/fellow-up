package io.fellowup.test.kafka

import io.fellowup.kafka.KObjectMapperObjectSerializer
import org.assertj.core.api.Assertions.assertThat
import java.util.*
import kotlin.test.Test

internal class KObjectMapperSerializerTest {

    @Test
    fun `should serialize to json string`() {
        // given
        data class TestEvent(val property: String)
        val event = TestEvent("test")

        // when
        val result: ByteArray? = KObjectMapperObjectSerializer.serialize("topic", event)

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
        val result: ByteArray? = KObjectMapperObjectSerializer.serialize("topic", event)

        // then
        val resultString = String(result!!)
        assertThat(resultString).isEqualTo("{\"uuid\":\"${event.uuid}\"}")
    }
}