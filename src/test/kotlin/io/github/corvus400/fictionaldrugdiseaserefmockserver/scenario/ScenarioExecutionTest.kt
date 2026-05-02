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
import kotlinx.serialization.json.int
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
    fun `drugList endpoint applies delay`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","delay_ms":500}""")
        }
        assertEquals(HttpStatusCode.OK, configResponse.status)

        val startTime = System.currentTimeMillis()
        val response = client.get("/v1/drugs")
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

        val configResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","status_code":500}""")
        }
        assertEquals(HttpStatusCode.OK, configResponse.status)

        val response = client.get("/v1/drugs")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }

    @Test
    fun `custom headers are applied when headers is set`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","headers":{"X-Custom-Header":"custom-value"}}""")
        }
        assertEquals(HttpStatusCode.OK, configResponse.status)

        val response = client.get("/v1/drugs")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("custom-value", response.headers["X-Custom-Header"])
    }

    @Test
    fun `reset clears override and restores default behavior`() = testApplication {
        application { module() }

        client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","status_code":500}""")
        }

        val errorResponse = client.get("/v1/drugs")
        assertEquals(HttpStatusCode.InternalServerError, errorResponse.status)

        val resetResponse = client.post("/__admin/reset")
        assertEquals(HttpStatusCode.OK, resetResponse.status)
        val body = json.decodeFromString<JsonObject>(resetResponse.bodyAsText())
        assertTrue(body["success"]?.jsonPrimitive?.boolean == true)

        val normalResponse = client.get("/v1/drugs")
        assertEquals(HttpStatusCode.OK, normalResponse.status)
    }

    @Test
    fun `drugList endpoint applies delay and status code`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","delay_ms":300,"status_code":503}""")
        }
        assertEquals(HttpStatusCode.OK, configResponse.status)

        val startTime = System.currentTimeMillis()
        val response = client.get("/v1/drugs")
        val elapsed = System.currentTimeMillis() - startTime

        assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
        assertTrue(
            actual = elapsed >= 300,
            message = "Expected delay of at least 300ms, but got ${elapsed}ms",
        )
    }

    @Test
    fun `drug list switches between default 120 and empty 0 via Admin API and reset restores 120`() =
        testApplication {
            application { module() }

            client.post("/__admin/reset")

            val defaultResponse = client.get("/v1/drugs")
            val defaultBody = json.decodeFromString<JsonObject>(defaultResponse.bodyAsText())
            assertEquals(
                expected = 120,
                actual = defaultBody["total_count"]?.jsonPrimitive?.int,
                message = "default シナリオの /drugs total_count は 120",
            )

            client.post("/__admin/configs/drugList") {
                contentType(ContentType.Application.Json)
                setBody("""{"state":"empty"}""")
            }

            val emptyResponse = client.get("/v1/drugs")
            val emptyBody = json.decodeFromString<JsonObject>(emptyResponse.bodyAsText())
            assertEquals(
                expected = 0,
                actual = emptyBody["total_count"]?.jsonPrimitive?.int,
                message = "empty オーバーライド後の /drugs total_count は 0",
            )

            client.post("/__admin/reset")

            val restoredResponse = client.get("/v1/drugs")
            val restoredBody = json.decodeFromString<JsonObject>(restoredResponse.bodyAsText())
            assertEquals(
                expected = 120,
                actual = restoredBody["total_count"]?.jsonPrimitive?.int,
                message = "reset 後の /drugs total_count は default の 120 に戻る",
            )
        }

    @Test
    fun `disease list switches between default 80 and empty 0 via Admin API and reset restores 80`() =
        testApplication {
            application { module() }

            client.post("/__admin/reset")

            val defaultResponse = client.get("/v1/diseases")
            val defaultBody = json.decodeFromString<JsonObject>(defaultResponse.bodyAsText())
            assertEquals(
                expected = 80,
                actual = defaultBody["total_count"]?.jsonPrimitive?.int,
                message = "default シナリオの /diseases total_count は 80",
            )

            client.post("/__admin/configs/diseaseList") {
                contentType(ContentType.Application.Json)
                setBody("""{"state":"empty"}""")
            }

            val emptyResponse = client.get("/v1/diseases")
            val emptyBody = json.decodeFromString<JsonObject>(emptyResponse.bodyAsText())
            assertEquals(
                expected = 0,
                actual = emptyBody["total_count"]?.jsonPrimitive?.int,
                message = "empty オーバーライド後の /diseases total_count は 0",
            )

            client.post("/__admin/reset")

            val restoredResponse = client.get("/v1/diseases")
            val restoredBody = json.decodeFromString<JsonObject>(restoredResponse.bodyAsText())
            assertEquals(
                expected = 80,
                actual = restoredBody["total_count"]?.jsonPrimitive?.int,
                message = "reset 後の /diseases total_count は default の 80 に戻る",
            )
        }
}
