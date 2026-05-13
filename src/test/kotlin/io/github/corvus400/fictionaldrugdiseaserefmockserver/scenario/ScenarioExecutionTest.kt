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

        val startTime = System.currentTimeMillis()
        val response = client.get("/v1/drugs")
        val elapsed = System.currentTimeMillis() - startTime

        assertEquals(
            expected = DelaySnapshot(
                configStatus = HttpStatusCode.OK,
                responseStatus = HttpStatusCode.OK,
                delayed = true
            ),
            actual = DelaySnapshot(
                configStatus = configResponse.status,
                responseStatus = response.status,
                delayed = elapsed >= 500,
            ),
            message = "drugList delay override must keep 200 and delay at least 500ms, observed=${elapsed}ms",
        )
    }

    @Test
    fun `status code is applied when statusCode is set`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","status_code":500}""")
        }

        val response = client.get("/v1/drugs")

        assertEquals(
            expected = StatusOverrideSnapshot(
                configStatus = HttpStatusCode.OK,
                responseStatus = HttpStatusCode.InternalServerError,
            ),
            actual = StatusOverrideSnapshot(configStatus = configResponse.status, responseStatus = response.status),
        )
    }

    @Test
    fun `custom headers are applied when headers is set`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","headers":{"X-Custom-Header":"custom-value"}}""")
        }

        val response = client.get("/v1/drugs")

        assertEquals(
            expected = HeaderOverrideSnapshot(
                configStatus = HttpStatusCode.OK,
                responseStatus = HttpStatusCode.OK,
                headerValue = "custom-value",
            ),
            actual = HeaderOverrideSnapshot(
                configStatus = configResponse.status,
                responseStatus = response.status,
                headerValue = response.headers["X-Custom-Header"],
            ),
        )
    }

    @Test
    fun `reset clears override and restores default behavior`() = testApplication {
        application { module() }

        client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","status_code":500}""")
        }

        val errorResponse = client.get("/v1/drugs")

        val resetResponse = client.post("/__admin/reset")
        val body = json.decodeFromString<JsonObject>(resetResponse.bodyAsText())

        val normalResponse = client.get("/v1/drugs")
        assertEquals(
            expected = ResetBehaviorSnapshot(
                errorStatus = HttpStatusCode.InternalServerError,
                resetStatus = HttpStatusCode.OK,
                resetSuccess = true,
                restoredStatus = HttpStatusCode.OK,
            ),
            actual = ResetBehaviorSnapshot(
                errorStatus = errorResponse.status,
                resetStatus = resetResponse.status,
                resetSuccess = body["success"]?.jsonPrimitive?.boolean,
                restoredStatus = normalResponse.status,
            ),
        )
    }

    @Test
    fun `drugList endpoint applies delay and status code`() = testApplication {
        application { module() }

        val configResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"default","delay_ms":300,"status_code":503}""")
        }

        val startTime = System.currentTimeMillis()
        val response = client.get("/v1/drugs")
        val elapsed = System.currentTimeMillis() - startTime

        assertEquals(
            expected = DelaySnapshot(
                configStatus = HttpStatusCode.OK,
                responseStatus = HttpStatusCode.ServiceUnavailable,
                delayed = true,
            ),
            actual = DelaySnapshot(
                configStatus = configResponse.status,
                responseStatus = response.status,
                delayed = elapsed >= 300,
            ),
            message = "drugList delay+status override must return 503 and delay at least 300ms, observed=${elapsed}ms",
        )
    }

    @Test
    fun `drug list switches between default 120 and empty 0 via Admin API and reset restores 120`() =
        testApplication {
            application { module() }

            client.post("/__admin/reset")

            val defaultResponse = client.get("/v1/drugs")
            val defaultBody = json.decodeFromString<JsonObject>(defaultResponse.bodyAsText())

            client.post("/__admin/configs/drugList") {
                contentType(ContentType.Application.Json)
                setBody("""{"state":"empty"}""")
            }

            val emptyResponse = client.get("/v1/drugs")
            val emptyBody = json.decodeFromString<JsonObject>(emptyResponse.bodyAsText())

            client.post("/__admin/reset")

            val restoredResponse = client.get("/v1/drugs")
            val restoredBody = json.decodeFromString<JsonObject>(restoredResponse.bodyAsText())

            assertEquals(
                expected = ScenarioSwitchSnapshot(defaultTotal = 120, emptyTotal = 0, restoredTotal = 120),
                actual = ScenarioSwitchSnapshot(
                    defaultTotal = defaultBody["total_count"]?.jsonPrimitive?.int,
                    emptyTotal = emptyBody["total_count"]?.jsonPrimitive?.int,
                    restoredTotal = restoredBody["total_count"]?.jsonPrimitive?.int,
                ),
                message = "drug list scenario switch must move default -> empty -> restored",
            )
        }

    @Test
    fun `disease list switches between default 80 and empty 0 via Admin API and reset restores 80`() =
        testApplication {
            application { module() }

            client.post("/__admin/reset")

            val defaultResponse = client.get("/v1/diseases")
            val defaultBody = json.decodeFromString<JsonObject>(defaultResponse.bodyAsText())

            client.post("/__admin/configs/diseaseList") {
                contentType(ContentType.Application.Json)
                setBody("""{"state":"empty"}""")
            }

            val emptyResponse = client.get("/v1/diseases")
            val emptyBody = json.decodeFromString<JsonObject>(emptyResponse.bodyAsText())

            client.post("/__admin/reset")

            val restoredResponse = client.get("/v1/diseases")
            val restoredBody = json.decodeFromString<JsonObject>(restoredResponse.bodyAsText())

            assertEquals(
                expected = ScenarioSwitchSnapshot(defaultTotal = 80, emptyTotal = 0, restoredTotal = 80),
                actual = ScenarioSwitchSnapshot(
                    defaultTotal = defaultBody["total_count"]?.jsonPrimitive?.int,
                    emptyTotal = emptyBody["total_count"]?.jsonPrimitive?.int,
                    restoredTotal = restoredBody["total_count"]?.jsonPrimitive?.int,
                ),
                message = "disease list scenario switch must move default -> empty -> restored",
            )
        }

    private data class DelaySnapshot(
        val configStatus: HttpStatusCode,
        val responseStatus: HttpStatusCode,
        val delayed: Boolean,
    )

    private data class StatusOverrideSnapshot(
        val configStatus: HttpStatusCode,
        val responseStatus: HttpStatusCode,
    )

    private data class HeaderOverrideSnapshot(
        val configStatus: HttpStatusCode,
        val responseStatus: HttpStatusCode,
        val headerValue: String?,
    )

    private data class ResetBehaviorSnapshot(
        val errorStatus: HttpStatusCode,
        val resetStatus: HttpStatusCode,
        val resetSuccess: Boolean?,
        val restoredStatus: HttpStatusCode,
    )

    private data class ScenarioSwitchSnapshot(
        val defaultTotal: Int?,
        val emptyTotal: Int?,
        val restoredTotal: Int?,
    )
}
