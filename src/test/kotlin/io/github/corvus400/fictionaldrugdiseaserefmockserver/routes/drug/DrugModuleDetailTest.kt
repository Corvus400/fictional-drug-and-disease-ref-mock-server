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
}
