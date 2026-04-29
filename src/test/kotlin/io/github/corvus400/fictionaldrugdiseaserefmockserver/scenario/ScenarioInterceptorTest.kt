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
import kotlin.test.assertNotNull

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
        assertEquals(HttpStatusCode.OK, adminResponse.status)

        val response = client.get("/drugs") {
            header("X-Mock-Scenario", "empty")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val items = body["items"]?.jsonArray
        assertNotNull(items)
        assertEquals(
            expected = 0,
            actual = items.size,
            message = "X-Mock-Scenario=empty が Admin override(default) より優先され items は 0 件",
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
        assertEquals(HttpStatusCode.OK, adminResponse.status)

        val response = client.get("/drugs")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals(
            expected = 0,
            actual = body["total_count"]?.jsonPrimitive?.int,
            message = "ヘッダなしリクエストは Admin override(empty) を採用し total_count=0",
        )

        client.post("/__admin/reset")
    }

    @Test
    fun `Drugs request with no scenario setting falls back to default 120`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val response = client.get("/drugs")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals(
            expected = 120,
            actual = body["total_count"]?.jsonPrimitive?.int,
            message = "ヘッダ・Admin override 無しの状態は default シナリオ(total_count=120) にフォールバック",
        )
    }
}
