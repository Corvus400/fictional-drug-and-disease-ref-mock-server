package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.ErrorResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
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

class DrugModuleTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs drug_0001 returns 200 OK with populated fixmerge name fields`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs/drug_0001")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val brandName = body["brand_name"]?.jsonPrimitive?.content
        val genericName = body["generic_name"]?.jsonPrimitive?.content
        val manufacturer = body["manufacturer"]?.jsonPrimitive?.content
        assertNotNull(brandName)
        assertTrue(brandName.isNotBlank(), "brandName must be non-blank")
        assertNotNull(genericName)
        assertTrue(genericName.isNotBlank(), "genericName must be non-blank")
        assertNotNull(manufacturer)
        assertTrue(manufacturer.isNotBlank(), "manufacturer must be non-blank")
    }

    @Test
    fun `GET drugs returns 200 OK with drug array`() = testApplication {
        application { module() }

        val response = client.get("/drugs")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("drug_0001"))
    }

    @Test
    fun `GET drugs default scenario first page exposes 10 DrugSummary fields and total_count 120`() =
        testApplication {
            application { module() }

            val response = client.get("/drugs")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response body must have an items array")
            assertTrue(
                items.isNotEmpty(),
                "default scenario first page must expose non-empty items array",
            )
            assertEquals(
                expected = 120,
                actual = body["total_count"]?.jsonPrimitive?.content?.toInt(),
                message = "default scenario envelope must surface total_count == 120 regardless of page size",
            )
            val firstItemKeys = items.first().jsonObject.keys
            assertEquals(
                expected = setOf(
                    "id",
                    "brand_name",
                    "generic_name",
                    "therapeutic_category_name",
                    "regulatory_class",
                    "dosage_form",
                    "brand_name_kana",
                    "atc_code",
                    "revised_at",
                    "image_url",
                ),
                actual = firstItemKeys,
                message = "HTTP response items[0] must expose exactly 10 DrugSummary snake_case fields",
            )
        }

    @Test
    fun `GET drugs with X-Mock-Scenario empty returns envelope with zero items`() = testApplication {
        application { module() }

        val response = client.get("/drugs") {
            headers {
                append(name = "X-Mock-Scenario", value = "empty")
            }
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(items, "empty scenario must still expose an items array")
        assertEquals(
            expected = 0,
            actual = items.size,
            message = "empty scenario must expose envelope with items.size == 0",
        )
    }

    @Test
    fun `GET drugs under empty scenario with sort parameter returns empty items and 200`() = testApplication {
        application { module() }

        val response = client.get("/drugs?sort=brand_name_kana") {
            headers {
                append(name = "X-Mock-Scenario", value = "empty")
            }
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = AppJson.decodeFromString<DrugListResponse>(response.bodyAsText())
        assertEquals(0, body.totalCount)
        assertTrue(body.items.isEmpty())
    }

    @Test
    fun `GET drugs with sort brand_name_kana returns items ordered by brandNameKana ascending`() =
        testApplication {
            application { module() }

            val response = client.get("/drugs?sort=brand_name_kana&page_size=100")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = AppJson.decodeFromString<DrugListResponse>(response.bodyAsText())
            val kanas = body.items.map { summary -> summary.brandNameKana }
            assertEquals(
                expected = kanas.sorted(),
                actual = kanas,
                message = "sort=brand_name_kana must order items by brandNameKana ascending",
            )
        }

    @Test
    fun `GET drugs with invalid sort returns 400 with INVALID_SORT_KEY error`() = testApplication {
        application { module() }

        val response = client.get("/drugs?sort=invalid_key")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val error = AppJson.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals("INVALID_SORT_KEY", error.code)
    }
}
