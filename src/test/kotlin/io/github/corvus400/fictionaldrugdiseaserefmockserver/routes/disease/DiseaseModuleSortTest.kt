package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.ErrorResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DiseaseModuleSortTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with sort name_kana returns items ordered by nameKana ascending`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases?sort=name_kana&page_size=100")

        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status,
            message = "sort=name_kana must return HTTP 200",
        )
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have an items array")
        val kanas = items.map { it.jsonObject["name_kana"]?.jsonPrimitive?.content.orEmpty() }
        assertEquals(
            expected = kanas.sorted(),
            actual = kanas,
            message = "items must be ordered by name_kana ascending when sort=name_kana",
        )
    }

    @Test
    fun `GET diseases with sort icd10_chapter returns items ordered by icd10Chapter declaration order`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/diseases?sort=icd10_chapter&page_size=100")

            assertEquals(
                expected = HttpStatusCode.OK,
                actual = response.status,
                message = "sort=icd10_chapter must return HTTP 200",
            )
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(actual = items, message = "response body must have an items array")
            val chapterDescriptor = Icd10Chapter.serializer().descriptor
            val chapterOrder = (0 until chapterDescriptor.elementsCount).associate { index ->
                chapterDescriptor.getElementName(index = index) to index
            }
            val ordinals = items.map { item ->
                val serialName = item.jsonObject["icd10_chapter"]?.jsonPrimitive?.content
                checkNotNull(chapterOrder[serialName]) {
                    "items[${item.jsonObject}] must expose a known icd10_chapter serialName, got $serialName"
                }
            }
            assertEquals(
                expected = ordinals.sorted(),
                actual = ordinals,
                message = "items must be ordered by icd10_chapter ascending when sort=icd10_chapter",
            )
        }

    @Test
    fun `GET diseases with invalid sort returns 400 with INVALID_SORT_KEY error`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases?sort=invalid_key")

        assertEquals(
            expected = HttpStatusCode.BadRequest,
            actual = response.status,
            message = "unknown sort key must surface as 400 BadRequest, got ${response.status}",
        )
        val error = json.decodeFromString<ErrorResponse>(string = response.bodyAsText())
        assertEquals(
            expected = "INVALID_SORT_KEY",
            actual = error.code,
            message = "invalid disease sort key must expose INVALID_SORT_KEY",
        )
    }

    @Test
    fun `GET diseases under empty scenario with sort parameter returns empty items and 200`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases?sort=name_kana") {
            header(key = "X-Mock-Scenario", value = "empty")
        }

        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status,
            "empty scenario with sort=name_kana must return HTTP 200",
        )
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertEquals(
            expected = 0,
            actual = totalCount,
            "empty scenario with sort=name_kana must report total_count=0",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have an items array")
        assertTrue(
            actual = items.isEmpty(),
            "empty scenario with sort=name_kana must return an empty items array",
        )
    }
}
