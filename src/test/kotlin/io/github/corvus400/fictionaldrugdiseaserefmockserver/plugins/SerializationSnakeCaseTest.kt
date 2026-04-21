package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationSnakeCaseTest {
    @Serializable
    private data class DummyDto(
        val firstName: String,
        val lastName: String,
    )

    @Test
    fun `serializer converts camelCase kotlin fields to snake_case JSON keys`() {
        val jsonText = AppJson.encodeToString(
            DummyDto(firstName = "a", lastName = "b"),
        )
        assertEquals(
            """{"first_name":"a","last_name":"b"}""",
            jsonText,
        )
    }
}
