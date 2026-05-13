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

class ResponseDisclaimerIntegrationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `drug detail response carries fictional data headers`() = testApplication {
        application { module() }

        val detailResponse = client.get("/v1/drugs/drug_0001")

        assertEquals(
            expected = DetailHeaderSnapshot(
                status = HttpStatusCode.OK,
                fictionalData = "true",
                disclaimerMentionsFictionalData = true,
            ),
            actual = DetailHeaderSnapshot(
                status = detailResponse.status,
                fictionalData = detailResponse.headers["X-Fictional-Data"],
                disclaimerMentionsFictionalData = detailResponse.headers["X-Disclaimer"]
                    .orEmpty()
                    .contains("FICTIONAL DATA"),
            ),
            "contract assertion failed"
        )
    }

    @Test
    fun `drug detail body carries short disclaimer`() = testApplication {
        application { module() }

        val detailResponse = client.get("/v1/drugs/drug_0001")
        val detailBody = json.parseToJsonElement(detailResponse.bodyAsText()).jsonObject

        assertEquals(
            Disclaimer.SHORT,
            detailBody["disclaimer"]?.jsonPrimitive?.content,
            "contract assertion failed"
        )
    }

    @Test
    fun `drug list body carries short disclaimer`() = testApplication {
        application { module() }

        val listResponse = client.get("/v1/drugs")
        val listBody = json.parseToJsonElement(listResponse.bodyAsText()).jsonObject

        assertEquals(
            Disclaimer.SHORT,
            listBody["disclaimer"]?.jsonPrimitive?.content,
            "contract assertion failed"
        )
    }

    @Test
    fun `drug list summary items do not carry disclaimer`() = testApplication {
        application { module() }

        val listResponse = client.get("/v1/drugs")
        val listBody = json.parseToJsonElement(listResponse.bodyAsText()).jsonObject

        assertNull(
            listBody["items"]?.jsonArray?.first()?.jsonObject?.get("disclaimer"),
            "contract assertion failed"
        )
    }

    private data class DetailHeaderSnapshot(
        val status: HttpStatusCode,
        val fictionalData: String?,
        val disclaimerMentionsFictionalData: Boolean,
    )
}
