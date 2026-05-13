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

class DrugModuleTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET v1 drugs returns 200 OK with drug array`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs")

        assertEquals(
            expected = ListContainsSnapshot(status = HttpStatusCode.OK, containsId = true),
            actual = ListContainsSnapshot(
                status = response.status,
                containsId = response.bodyAsText().contains("drug_0001"),
            ),
        )
    }

    @Test
    fun `GET drugs drug_0001 returns 200 OK with populated fixmerge name fields`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs/drug_0001")

        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val brandName = body["brand_name"]?.jsonPrimitive?.content
        val genericName = body["generic_name"]?.jsonPrimitive?.content
        val manufacturer = body["manufacturer"]?.jsonPrimitive?.content
        assertEquals(
            expected = DetailNameSnapshot(
                status = HttpStatusCode.OK,
                brandNameNonBlank = true,
                genericNameNonBlank = true,
                manufacturerNonBlank = true,
            ),
            actual = DetailNameSnapshot(
                status = response.status,
                brandNameNonBlank = brandName?.isNotBlank() == true,
                genericNameNonBlank = genericName?.isNotBlank() == true,
                manufacturerNonBlank = manufacturer?.isNotBlank() == true,
            ),
        )
    }

    @Test
    fun `GET drugs returns 200 OK with drug array`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs")

        assertEquals(
            expected = ListContainsSnapshot(status = HttpStatusCode.OK, containsId = true),
            actual = ListContainsSnapshot(
                status = response.status,
                containsId = response.bodyAsText().contains("drug_0001"),
            ),
        )
    }

    @Test
    fun `GET drugs default scenario first page exposes 10 DrugSummary fields and total_count 120`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            val firstItemKeys = items?.firstOrNull()?.jsonObject?.keys.orEmpty()
            assertEquals(
                expected = DrugSummaryPageSnapshot(
                    status = HttpStatusCode.OK,
                    itemsPresent = true,
                    itemsNonEmpty = true,
                    totalCount = 120,
                    firstItemKeys = DRUG_SUMMARY_KEYS,
                ),
                actual = DrugSummaryPageSnapshot(
                    status = response.status,
                    itemsPresent = items != null,
                    itemsNonEmpty = items?.isNotEmpty() == true,
                    totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt(),
                    firstItemKeys = firstItemKeys,
                ),
            )
        }

    @Test
    fun `GET drugs with X-Mock-Scenario empty returns envelope with zero items`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs") {
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

    @Test
    fun `GET drugs under empty scenario with sort parameter returns empty items and 200`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?sort=brand_name_kana") {
            headers {
                append(name = "X-Mock-Scenario", value = "empty")
            }
        }

        val body = AppJson.decodeFromString<DrugListResponse>(response.bodyAsText())
        assertEquals(
            expected = EmptyListSnapshot(status = HttpStatusCode.OK, itemsSize = 0, totalCount = 0),
            actual = EmptyListSnapshot(
                status = response.status,
                itemsSize = body.items.size,
                totalCount = body.totalCount,
            ),
        )
    }

    @Test
    fun `GET drugs with sort brand_name_kana returns items ordered by brandNameKana ascending`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs?sort=brand_name_kana&page_size=100")

            val body = AppJson.decodeFromString<DrugListResponse>(response.bodyAsText())
            val kanas = body.items.map { summary -> summary.brandNameKana }
            assertEquals(
                expected = SortSnapshot(status = HttpStatusCode.OK, ordered = true),
                actual = SortSnapshot(status = response.status, ordered = kanas == kanas.sorted()),
                message = "sort=brand_name_kana must order items by brandNameKana ascending",
            )
        }

    @Test
    fun `GET drugs with invalid sort returns 400 with INVALID_SORT_KEY error`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?sort=invalid_key")

        val error = AppJson.decodeFromString<ErrorResponse>(response.bodyAsText())
        assertEquals(
            expected = ErrorStatusSnapshot(status = HttpStatusCode.BadRequest, code = "INVALID_SORT_KEY"),
            actual = ErrorStatusSnapshot(status = response.status, code = error.code),
        )
    }

    private data class ListContainsSnapshot(
        val status: HttpStatusCode,
        val containsId: Boolean,
    )

    private data class DetailNameSnapshot(
        val status: HttpStatusCode,
        val brandNameNonBlank: Boolean,
        val genericNameNonBlank: Boolean,
        val manufacturerNonBlank: Boolean,
    )

    private data class DrugSummaryPageSnapshot(
        val status: HttpStatusCode,
        val itemsPresent: Boolean,
        val itemsNonEmpty: Boolean,
        val totalCount: Int?,
        val firstItemKeys: Set<String>,
    )

    private data class EmptyListSnapshot(
        val status: HttpStatusCode,
        val itemsSize: Int?,
        val totalCount: Int? = null,
    )

    private data class ErrorStatusSnapshot(
        val status: HttpStatusCode,
        val code: String,
    )

    private data class SortSnapshot(
        val status: HttpStatusCode,
        val ordered: Boolean,
    )

    private companion object {
        val DRUG_SUMMARY_KEYS: Set<String> = setOf(
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
        )
    }
}
