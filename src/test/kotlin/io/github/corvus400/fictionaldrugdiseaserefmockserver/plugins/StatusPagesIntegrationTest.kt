package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.Disclaimer
import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusPagesIntegrationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `unknown route error response includes disclaimer`() = testApplication {
        application { module() }

        val response = client.get("/unknown-route")
        val body = json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(
            expected = ErrorResponseSnapshot(
                status = HttpStatusCode.NotFound,
                disclaimer = Disclaimer.SHORT,
            ),
            actual = ErrorResponseSnapshot(
                status = response.status,
                disclaimer = body["disclaimer"]?.jsonPrimitive?.content,
            ),
            message = "unknown route must return 404 with the short disclaimer",
        )
    }

    private data class ErrorResponseSnapshot(
        val status: HttpStatusCode,
        val disclaimer: String?,
    )
}
