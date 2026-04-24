package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

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
}
