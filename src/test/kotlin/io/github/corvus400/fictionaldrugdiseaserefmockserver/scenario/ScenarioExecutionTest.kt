package io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * シナリオ実行基盤のテスト
 *
 * Admin APIで設定したdelayMs、statusCode、headersが
 * 実際のレスポンスに反映されることを検証
 */
class ScenarioExecutionTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `sample endpoint applies delay`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","delayMs":500}""")
        }
        assertEquals(HttpStatusCode.OK, configResponse.status)

        val startTime = System.currentTimeMillis()
        val response = client.get("/api/sample")
        val elapsed = System.currentTimeMillis() - startTime

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(
            actual = elapsed >= 500,
            message = "Expected delay of at least 500ms, but got ${elapsed}ms",
        )
    }

    @Test
    fun `status code is applied when statusCode is set`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","statusCode":500}""")
        }
        assertEquals(HttpStatusCode.OK, configResponse.status)

        val response = client.get("/api/sample")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }

    @Test
    fun `custom headers are applied when headers is set`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","headers":{"X-Custom-Header":"custom-value"}}""")
        }
        assertEquals(HttpStatusCode.OK, configResponse.status)

        val response = client.get("/api/sample")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("custom-value", response.headers["X-Custom-Header"])
    }

    @Test
    fun `reset clears override and restores default behavior`() = testApplication {
        application { module() }

        client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","statusCode":500}""")
        }

        val errorResponse = client.get("/api/sample")
        assertEquals(HttpStatusCode.InternalServerError, errorResponse.status)

        val resetResponse = client.post("/__admin/reset")
        assertEquals(HttpStatusCode.OK, resetResponse.status)
        val body = json.decodeFromString<JsonObject>(resetResponse.bodyAsText())
        assertTrue(body["success"]?.jsonPrimitive?.boolean == true)

        val normalResponse = client.get("/api/sample")
        assertEquals(HttpStatusCode.OK, normalResponse.status)
    }

    @Test
    fun `sample endpoint applies delay and status code`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","delayMs":300,"statusCode":503}""")
        }
        assertEquals(HttpStatusCode.OK, configResponse.status)

        val startTime = System.currentTimeMillis()
        val response = client.get("/api/sample")
        val elapsed = System.currentTimeMillis() - startTime

        assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
        assertTrue(
            actual = elapsed >= 300,
            message = "Expected delay of at least 300ms, but got ${elapsed}ms",
        )
    }
}
