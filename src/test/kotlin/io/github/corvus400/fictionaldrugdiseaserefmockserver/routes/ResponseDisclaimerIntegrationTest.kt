package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.Disclaimer
import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResponseDisclaimerIntegrationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `drug responses carry disclaimer in headers and non-summary bodies`() = testApplication {
        application { module() }

        val detailResponse = client.get("/v1/drugs/drug_0001")

        assertEquals(
            HttpStatusCode.OK,
            detailResponse.status,
            "contract assertion failed"
        )
        assertEquals(
            "true",
            detailResponse.headers["X-Fictional-Data"],
            "contract assertion failed"
        )
        assertTrue(
            detailResponse.headers["X-Disclaimer"].orEmpty().contains("FICTIONAL DATA"),
            "contract assertion failed"
        )
        val detailBody = json.parseToJsonElement(detailResponse.bodyAsText()).jsonObject
        assertEquals(
            Disclaimer.SHORT,
            detailBody["disclaimer"]?.jsonPrimitive?.content,
            "contract assertion failed"
        )

        val listResponse = client.get("/v1/drugs")
        val listBody = json.parseToJsonElement(listResponse.bodyAsText()).jsonObject
        assertEquals(
            Disclaimer.SHORT,
            listBody["disclaimer"]?.jsonPrimitive?.content,
            "contract assertion failed"
        )
        assertNull(
            listBody["items"]?.jsonArray?.first()?.jsonObject?.get("disclaimer"),
            "contract assertion failed"
        )
    }
}
