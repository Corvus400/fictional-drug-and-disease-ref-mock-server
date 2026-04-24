package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.measureTime

/**
 * `/drugs/{id}` 詳細エンドポイントの Phase 9-5a 契約検証。
 *
 * Phase 9-5a (Issue #56) では `scenarioRoute` と同等の `resolveScenarioWithOverride` +
 * `respondWithScenario` 経路に詳細 endpoint を揃え、Admin API `configs/drugDetail`
 * 経由で delay / statusCode を独立にオーバーライドできることを保証する。
 */
class DrugModuleDetailTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs drug_0001 default scenario returns 200 with all 37 Drug fields`() = testApplication {
        application { module() }

        val response = client.get("/drugs/drug_0001")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = "drug_0001",
            actual = body["id"]?.jsonPrimitive?.content,
            message = "default scenario must return the drug fixture matching the path id",
        )
        assertEquals(
            expected = 37,
            actual = body.keys.size,
            message = "default scenario must expose all 37 Drug fields (encodeDefaults=true)",
        )
    }

    @Test
    fun `POST admin configs drugDetail delayMs 500 defers GET drugs drug_0001 by at least 500ms`() =
        testApplication {
            application { module() }

            val configureResponse = client.post("/__admin/configs/drugDetail") {
                contentType(ContentType.Application.Json)
                setBody("""{"state":"default","delay_ms":500}""")
            }
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = configureResponse.status,
                message = "Admin API must accept drugDetail delayMs override",
            )

            val elapsed = measureTime {
                val response = client.get("/drugs/drug_0001")
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status,
                    message = "delay override must keep 200 status on default scenario",
                )
                val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
                assertEquals(
                    expected = "drug_0001",
                    actual = body["id"]?.jsonPrimitive?.content,
                    message = "delay override must still return the requested drug body",
                )
            }
            assertTrue(
                actual = elapsed.inWholeMilliseconds >= 500,
                message = "/drugs/{id} must honor Admin API delayMs=500 (observed=${elapsed.inWholeMilliseconds}ms)",
            )
        }

    @Test
    fun `POST admin configs drugDetail statusCode 500 flips GET drugs drug_0001 to 500 Internal Server Error`() =
        testApplication {
            application { module() }

            val configureResponse = client.post("/__admin/configs/drugDetail") {
                contentType(ContentType.Application.Json)
                setBody("""{"state":"default","status_code":500}""")
            }
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = configureResponse.status,
                message = "Admin API must accept drugDetail statusCode override",
            )

            val response = client.get("/drugs/drug_0001")
            assertEquals(
                expected = HttpStatusCode.InternalServerError,
                actual = response.status,
                message = "statusCode=500 override must flip GET /drugs/{id} to 500 Internal Server Error",
            )
        }

    @Test
    fun `GET drugs drug_9999 unknown id returns 404 with ErrorResponse code NOT_FOUND`() = testApplication {
        application { module() }

        val response = client.get("/drugs/drug_9999")

        assertEquals(
            expected = HttpStatusCode.NotFound,
            actual = response.status,
            message = "GET /drugs/drug_9999 must return 404 when the id has no matching fixture",
        )
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = "NOT_FOUND",
            actual = body["code"]?.jsonPrimitive?.content,
            message = "404 body must be ErrorResponse with code=NOT_FOUND, got keys=${body.keys}",
        )
        assertEquals(
            expected = "Drug not found: drug_9999",
            actual = body["message"]?.jsonPrimitive?.content,
            message = "404 body.message must embed the requested id for debuggability",
        )
    }

    @Test
    fun `POST admin configs drugDetail status_code 404 swaps GET drugs drug_0001 body to ErrorResponse NOT_FOUND`() =
        testApplication {
            application { module() }

            val configureResponse = client.post("/__admin/configs/drugDetail") {
                contentType(ContentType.Application.Json)
                setBody("""{"state":"default","status_code":404}""")
            }
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = configureResponse.status,
                message = "Admin API must accept drugDetail status_code=404 override",
            )

            val response = client.get("/drugs/drug_0001")

            assertEquals(
                expected = HttpStatusCode.NotFound,
                actual = response.status,
                message = "status_code=404 override must flip GET /drugs/drug_0001 to 404 even for an existing id",
            )
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            assertEquals(
                expected = "NOT_FOUND",
                actual = body["code"]?.jsonPrimitive?.content,
                message =
                "404 body must swap to ErrorResponse(code=NOT_FOUND); Drug body leaking through violates the " +
                    "error contract (#52). got keys=${body.keys}",
            )
            assertEquals(
                expected = "Drug not found: drug_0001",
                actual = body["message"]?.jsonPrimitive?.content,
                message =
                "404 body.message must embed the requested id so clients can distinguish override-driven 404 " +
                    "from hard id-miss 404 at the message level",
            )
        }
}
