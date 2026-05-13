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

        assertEquals(
            HttpStatusCode.NotFound,
            response.status,
            "contract assertion failed"
        )
        val body = json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(
            Disclaimer.SHORT,
            body["disclaimer"]?.jsonPrimitive?.content,
            "contract assertion failed"
        )
    }
}
