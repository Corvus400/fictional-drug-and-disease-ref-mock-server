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

class DiseaseModuleSortTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with sort name_kana returns items ordered by nameKana ascending`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases?sort=name_kana&page_size=100")

        assertEquals(
            expected = SortSnapshot(status = HttpStatusCode.OK, hasItems = true, ordered = true),
            actual = sortSnapshot(response = response, valueSelector = "name_kana"),
            message = "items must be ordered by name_kana ascending when sort=name_kana",
        )
    }

    @Test
    fun `GET diseases with sort icd10_chapter returns items ordered by icd10Chapter declaration order`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/diseases?sort=icd10_chapter&page_size=100")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val chapterDescriptor = Icd10Chapter.serializer().descriptor
            val chapterOrder = (0 until chapterDescriptor.elementsCount).associate { index ->
                chapterDescriptor.getElementName(index = index) to index
            }
            val ordinals = body["items"]?.jsonArray.orEmpty().map { item ->
                val serialName = item.jsonObject["icd10_chapter"]?.jsonPrimitive?.content
                checkNotNull(chapterOrder[serialName]) {
                    "items[${item.jsonObject}] must expose a known icd10_chapter serialName, got $serialName"
                }
            }
            assertEquals(
                expected = SortSnapshot(status = HttpStatusCode.OK, hasItems = true, ordered = true),
                actual = SortSnapshot(
                    status = response.status,
                    hasItems = body["items"]?.jsonArray?.isNotEmpty() == true,
                    ordered = ordinals == ordinals.sorted(),
                ),
                message = "items must be ordered by icd10_chapter ascending when sort=icd10_chapter",
            )
        }

    @Test
    fun `GET diseases with invalid sort returns 400 with INVALID_SORT_KEY error`() = testApplication {
        application { module() }

        val response = client.get("/v1/diseases?sort=invalid_key")

        val error = json.decodeFromString<ErrorResponse>(string = response.bodyAsText())
        assertEquals(
            expected = ErrorSnapshot(status = HttpStatusCode.BadRequest, code = "INVALID_SORT_KEY"),
            actual = ErrorSnapshot(status = response.status, code = error.code),
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
            expected = EmptyEnvelopeSnapshot(status = HttpStatusCode.OK, totalCount = 0, itemsSize = 0),
            actual = emptyEnvelopeSnapshot(response = response),
            message = "empty scenario with sort=name_kana must return an empty envelope",
        )
    }

    private suspend fun sortSnapshot(
        response: io.ktor.client.statement.HttpResponse,
        valueSelector: String,
    ): SortSnapshot {
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val values = body["items"]?.jsonArray.orEmpty()
            .map { it.jsonObject[valueSelector]?.jsonPrimitive?.content.orEmpty() }
        return SortSnapshot(
            status = response.status,
            hasItems = values.isNotEmpty(),
            ordered = values == values.sorted(),
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

    private data class SortSnapshot(
        val status: HttpStatusCode,
        val hasItems: Boolean,
        val ordered: Boolean,
    )

    private data class ErrorSnapshot(
        val status: HttpStatusCode,
        val code: String,
    )

    private data class EmptyEnvelopeSnapshot(
        val status: HttpStatusCode,
        val totalCount: Int?,
        val itemsSize: Int?,
    )
}
