package io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class ScenarioInterceptorTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `X-Mock-Scenario header on drugs overrides Admin API drugList state`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val adminResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "default"}""")
        }

        val response = client.get("/v1/drugs") {
            header("X-Mock-Scenario", "empty")
        }

        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val items = body["items"]?.jsonArray
        assertEquals(
            expected = HeaderScenarioSnapshot(
                adminStatus = HttpStatusCode.OK,
                responseStatus = HttpStatusCode.OK,
                itemsSize = 0,
            ),
            actual = HeaderScenarioSnapshot(
                adminStatus = adminResponse.status,
                responseStatus = response.status,
                itemsSize = items?.size,
            ),
        )

        client.post("/__admin/reset")
    }

    @Test
    fun `Drugs request without header uses Admin API drugList state`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val adminResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }

        val response = client.get("/v1/drugs")

        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals(
            expected = AdminScenarioSnapshot(
                adminStatus = HttpStatusCode.OK,
                responseStatus = HttpStatusCode.OK,
                totalCount = 0,
            ),
            actual = AdminScenarioSnapshot(
                adminStatus = adminResponse.status,
                responseStatus = response.status,
                totalCount = body["total_count"]?.jsonPrimitive?.int,
            ),
        )

        client.post("/__admin/reset")
    }

    @Test
    fun `Drugs request with no scenario setting falls back to default 120`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val response = client.get("/v1/drugs")

        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals(
            expected = DefaultScenarioSnapshot(responseStatus = HttpStatusCode.OK, totalCount = 120),
            actual = DefaultScenarioSnapshot(
                responseStatus = response.status,
                totalCount = body["total_count"]?.jsonPrimitive?.int,
            ),
        )
    }

    private data class HeaderScenarioSnapshot(
        val adminStatus: HttpStatusCode,
        val responseStatus: HttpStatusCode,
        val itemsSize: Int?,
    )

    private data class AdminScenarioSnapshot(
        val adminStatus: HttpStatusCode,
        val responseStatus: HttpStatusCode,
        val totalCount: Int?,
    )

    private data class DefaultScenarioSnapshot(
        val responseStatus: HttpStatusCode,
        val totalCount: Int?,
    )
}
