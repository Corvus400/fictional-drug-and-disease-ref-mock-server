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

class DiseaseModuleTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET v1 diseases returns 200 OK with disease array`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases")

        assertEquals(
            expected = ListContainsSnapshot(status = HttpStatusCode.OK, containsId = true),
            actual = ListContainsSnapshot(
                status = response.status,
                containsId = response.bodyAsText().contains("disease_0001"),
            ),
        )
    }

    @Test
    fun `GET diseases disease_0001 returns 200 OK with populated fixmerge name fields`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases/disease_0001")

        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val name = body["name"]?.jsonPrimitive?.content
        val nameKana = body["name_kana"]?.jsonPrimitive?.content
        val nameEnglish = body["name_english"]?.jsonPrimitive?.content
        assertEquals(
            expected = DetailNameSnapshot(
                status = HttpStatusCode.OK,
                nameNonBlank = true,
                nameKanaNonBlank = true,
                nameEnglishNonBlank = true,
            ),
            actual = DetailNameSnapshot(
                status = response.status,
                nameNonBlank = name?.isNotBlank() == true,
                nameKanaNonBlank = nameKana?.isNotBlank() == true,
                nameEnglishNonBlank = nameEnglish?.isNotBlank() == true,
            ),
        )
    }

    @Test
    fun `GET diseases returns 200 OK with disease array`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases")

        assertEquals(
            expected = ListContainsSnapshot(status = HttpStatusCode.OK, containsId = true),
            actual = ListContainsSnapshot(
                status = response.status,
                containsId = response.bodyAsText().contains("disease_0001"),
            ),
        )
    }

    @Test
    fun `GET diseases default scenario first page exposes 8 DiseaseSummary fields and total_count 80`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/diseases")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            val firstItemKeys = items?.firstOrNull()?.jsonObject?.keys.orEmpty()
            assertEquals(
                expected = DiseaseSummaryPageSnapshot(
                    status = HttpStatusCode.OK,
                    itemsPresent = true,
                    itemsNonEmpty = true,
                    totalCount = 80,
                    firstItemKeys = DISEASE_SUMMARY_KEYS,
                ),
                actual = DiseaseSummaryPageSnapshot(
                    status = response.status,
                    itemsPresent = items != null,
                    itemsNonEmpty = items?.isNotEmpty() == true,
                    totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt(),
                    firstItemKeys = firstItemKeys,
                ),
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

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertEquals(
            expected = EmptyListSnapshot(status = HttpStatusCode.OK, itemsSize = 0),
            actual = EmptyListSnapshot(status = response.status, itemsSize = items?.size),
        )
    }

    private data class ListContainsSnapshot(
        val status: HttpStatusCode,
        val containsId: Boolean,
    )

    private data class DetailNameSnapshot(
        val status: HttpStatusCode,
        val nameNonBlank: Boolean,
        val nameKanaNonBlank: Boolean,
        val nameEnglishNonBlank: Boolean,
    )

    private data class DiseaseSummaryPageSnapshot(
        val status: HttpStatusCode,
        val itemsPresent: Boolean,
        val itemsNonEmpty: Boolean,
        val totalCount: Int?,
        val firstItemKeys: Set<String>,
    )

    private data class EmptyListSnapshot(
        val status: HttpStatusCode,
        val itemsSize: Int?,
    )

    private companion object {
        val DISEASE_SUMMARY_KEYS: Set<String> = setOf(
            "id",
            "name",
            "icd10_chapter",
            "medical_department",
            "chronicity",
            "infectious",
            "name_kana",
            "revised_at",
        )
    }
}
