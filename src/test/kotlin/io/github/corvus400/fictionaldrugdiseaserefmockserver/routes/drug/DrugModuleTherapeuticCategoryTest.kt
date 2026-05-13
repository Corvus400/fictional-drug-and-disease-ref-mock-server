package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugModuleTherapeuticCategoryTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs therapeutic_category=ALIMENTARY_METABOLISM returns only alimentary metabolism drugs`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs?therapeutic_category=ALIMENTARY_METABOLISM&page_size=100")

            assertEquals(
                expected = FilterSnapshot(
                    status = HttpStatusCode.OK,
                    totalCountInRange = true,
                    itemsSizeMatchesTotal = true,
                    itemViolations = emptyList(),
                ),
                actual = filterSnapshot(
                    response = response,
                    expectedCategoryName = "消化器系および代謝",
                ),
                message = "therapeutic_category=ALIMENTARY_METABOLISM response must be filtered consistently",
            )
        }

    @Test
    fun `GET drugs therapeutic_category=NERVOUS_SYSTEM returns only nervous system drugs`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?therapeutic_category=NERVOUS_SYSTEM&page_size=100")

        assertEquals(
            expected = FilterSnapshot(
                status = HttpStatusCode.OK,
                totalCountInRange = true,
                itemsSizeMatchesTotal = true,
                itemViolations = emptyList(),
            ),
            actual = filterSnapshot(
                response = response,
                expectedCategoryName = "神経系",
            ),
            message = "therapeutic_category=NERVOUS_SYSTEM response must be filtered consistently",
        )
    }

    @Test
    fun `GET drugs therapeutic_category=UNKNOWN_CATEGORY returns HTTP 400 and INVALID_THERAPEUTIC_CATEGORY`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs?therapeutic_category=UNKNOWN_CATEGORY")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            assertEquals(
                expected = ErrorSnapshot(
                    status = HttpStatusCode.BadRequest,
                    code = "INVALID_THERAPEUTIC_CATEGORY",
                    messageMentionsUnknownCategory = true,
                ),
                actual = ErrorSnapshot(
                    status = response.status,
                    code = body["code"]?.jsonPrimitive?.content,
                    messageMentionsUnknownCategory =
                    body["message"]?.jsonPrimitive?.content?.contains(other = "UNKNOWN_CATEGORY") == true,
                ),
                message = "invalid therapeutic_category response must describe the rejected value",
            )
        }

    @Test
    fun `GET drugs therapeutic_category and category_atc returns intersection when both match`() = testApplication {
        application { module() }

        val response = client.get(
            "/v1/drugs?therapeutic_category=ALIMENTARY_METABOLISM&category_atc=A&page_size=100",
        )

        assertEquals(
            expected = FilterSnapshot(
                status = HttpStatusCode.OK,
                totalCountInRange = true,
                itemsSizeMatchesTotal = true,
                itemViolations = emptyList(),
            ),
            actual = filterSnapshot(
                response = response,
                expectedCategoryName = "消化器系および代謝",
            ),
            message = "matching therapeutic_category and category_atc filters must return an intersection",
        )
    }

    @Test
    fun `GET drugs therapeutic_category=A and category_atc=N returns empty intersection`() = testApplication {
        application { module() }

        val response = client.get(
            "/v1/drugs?therapeutic_category=ALIMENTARY_METABOLISM&category_atc=N&page_size=100",
        )

        assertEquals(
            expected = EmptyEnvelopeSnapshot(status = HttpStatusCode.OK, totalCount = 0, itemsSize = 0),
            actual = emptyEnvelopeSnapshot(response = response),
            message = "conflicting therapeutic_category and category_atc filters must return an empty envelope",
        )
    }

    @Test
    fun `GET drugs category_name is silently ignored and returns unfiltered total_count`() = testApplication {
        application { module() }

        val deprecatedResponse = client.get("/v1/drugs?category_name=消化器系および代謝&page=1")
        val unfilteredResponse = client.get("/v1/drugs?page=1")

        val deprecatedBody = json.parseToJsonElement(string = deprecatedResponse.bodyAsText()).jsonObject
        val unfilteredBody = json.parseToJsonElement(string = unfilteredResponse.bodyAsText()).jsonObject
        assertEquals(
            expected = IgnoredCategoryNameSnapshot(
                deprecatedStatus = HttpStatusCode.OK,
                unfilteredStatus = HttpStatusCode.OK,
                totalCountsMatch = true,
            ),
            actual = IgnoredCategoryNameSnapshot(
                deprecatedStatus = deprecatedResponse.status,
                unfilteredStatus = unfilteredResponse.status,
                totalCountsMatch =
                deprecatedBody["total_count"]?.jsonPrimitive?.content?.toInt() ==
                    unfilteredBody["total_count"]?.jsonPrimitive?.content?.toInt(),
            ),
            message = "category_name must no longer filter /v1/drugs; Ktor should silently ignore it",
        )
    }

    private suspend fun filterSnapshot(
        response: io.ktor.client.statement.HttpResponse,
        expectedCategoryName: String,
    ): FilterSnapshot {
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        val items = body["items"]?.jsonArray
        val itemViolations = items.orEmpty().mapIndexedNotNull { index, item ->
            val obj = item.jsonObject
            val id = obj["id"]?.jsonPrimitive?.content
            val actualCategoryName = obj["therapeutic_category_name"]?.jsonPrimitive?.content
            when {
                id == null -> "items[$index] must expose id"
                actualCategoryName != expectedCategoryName ->
                    "item id=$id must have therapeutic_category_name=$expectedCategoryName, got $actualCategoryName"
                else -> null
            }
        }
        return FilterSnapshot(
            status = response.status,
            totalCountInRange = totalCount != null && totalCount in 1 until DEFAULT_DRUG_COUNT,
            itemsSizeMatchesTotal = items != null && items.size == totalCount,
            itemViolations = itemViolations,
        )
    }

    private suspend fun emptyEnvelopeSnapshot(
        response: io.ktor.client.statement.HttpResponse,
    ): EmptyEnvelopeSnapshot {
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        return EmptyEnvelopeSnapshot(
            status = response.status,
            totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull(),
            itemsSize = body["items"]?.jsonArray?.size,
        )
    }

    private data class FilterSnapshot(
        val status: HttpStatusCode,
        val totalCountInRange: Boolean,
        val itemsSizeMatchesTotal: Boolean,
        val itemViolations: List<String>,
    )

    private data class ErrorSnapshot(
        val status: HttpStatusCode,
        val code: String?,
        val messageMentionsUnknownCategory: Boolean,
    )

    private data class EmptyEnvelopeSnapshot(
        val status: HttpStatusCode,
        val totalCount: Int?,
        val itemsSize: Int?,
    )

    private data class IgnoredCategoryNameSnapshot(
        val deprecatedStatus: HttpStatusCode,
        val unfilteredStatus: HttpStatusCode,
        val totalCountsMatch: Boolean,
    )

    private companion object {
        const val DEFAULT_DRUG_COUNT = 120
    }
}
