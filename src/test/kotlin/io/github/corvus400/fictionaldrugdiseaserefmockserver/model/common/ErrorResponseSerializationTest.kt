package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorResponseSerializationTest {
    @Test
    fun `ErrorResponse serializes code message details to snake_case JSON`() {
        val error = ErrorResponse(code = "NOT_FOUND", message = "Resource not found", details = null)
        val json = AppJson.encodeToString(error)
        assertEquals("""{"code":"NOT_FOUND","message":"Resource not found","details":null}""", json)
    }

    @Test
    fun `ErrorResponse with non-null details serializes details field value`() {
        val error = ErrorResponse(code = "VALIDATION", message = "Invalid", details = "id must not be blank")
        val json = AppJson.encodeToString(error)
        assertEquals(
            """{"code":"VALIDATION","message":"Invalid","details":"id must not be blank"}""",
            json,
        )
    }
}
