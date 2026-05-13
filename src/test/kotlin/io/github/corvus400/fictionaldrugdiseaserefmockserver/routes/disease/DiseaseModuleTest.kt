package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DiseaseModuleTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET v1 diseases returns 200 OK with disease array`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases")

        assertEquals(
            HttpStatusCode.OK,
            response.status,
            "contract assertion failed"
        )
        assertTrue(
            response.bodyAsText().contains("disease_0001"),
            "contract assertion failed"
        )
    }

    @Test
    fun `GET diseases disease_0001 returns 200 OK with populated fixmerge name fields`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases/disease_0001")

        assertEquals(
            HttpStatusCode.OK,
            response.status,
            "contract assertion failed"
        )
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val name = body["name"]?.jsonPrimitive?.content
        val nameKana = body["name_kana"]?.jsonPrimitive?.content
        val nameEnglish = body["name_english"]?.jsonPrimitive?.content
        assertNotNull(
            name,
            "contract assertion failed"
        )
        assertTrue(
            name.isNotBlank(),
            "name must be non-blank"
        )
        assertNotNull(
            nameKana,
            "contract assertion failed"
        )
        assertTrue(
            nameKana.isNotBlank(),
            "nameKana must be non-blank"
        )
        assertNotNull(
            nameEnglish,
            "contract assertion failed"
        )
        assertTrue(
            nameEnglish.isNotBlank(),
            "nameEnglish must be non-blank"
        )
    }

    @Test
    fun `GET diseases returns 200 OK with disease array`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases")

        assertEquals(
            HttpStatusCode.OK,
            response.status,
            "contract assertion failed"
        )
        assertTrue(
            response.bodyAsText().contains("disease_0001"),
            "contract assertion failed"
        )
    }

    @Test
    fun `GET diseases default scenario first page exposes 8 DiseaseSummary fields and total_count 80`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/diseases")

            assertEquals(
                HttpStatusCode.OK,
                response.status,
                "contract assertion failed"
            )
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(
                items,
                "response body must have an items array"
            )
            assertTrue(
                items.isNotEmpty(),
                "default scenario first page must expose non-empty items array",
            )
            assertEquals(
                expected = 80,
                actual = body["total_count"]?.jsonPrimitive?.content?.toInt(),
                message = "default scenario envelope must surface total_count == 80 regardless of page size",
            )
            val firstItemKeys = items.first().jsonObject.keys
            assertEquals(
                expected = setOf(
                    "id",
                    "name",
                    "icd10_chapter",
                    "medical_department",
                    "chronicity",
                    "infectious",
                    "name_kana",
                    "revised_at",
                ),
                actual = firstItemKeys,
                message = "items[0] must expose DiseaseSummary 8 snake_case fields, got $firstItemKeys",
            )
        }

    @Test
    fun `GET diseases with X-Mock-Scenario empty returns envelope with zero items`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases") {
            headers {
                append(name = "X-Mock-Scenario", value = "empty")
            }
        }

        assertEquals(
            HttpStatusCode.OK,
            response.status,
            "contract assertion failed"
        )
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(
            items,
            "empty scenario must still expose an items array"
        )
        assertEquals(
            expected = 0,
            actual = items.size,
            message = "empty scenario must expose envelope with items.size == 0",
        )
    }
}
