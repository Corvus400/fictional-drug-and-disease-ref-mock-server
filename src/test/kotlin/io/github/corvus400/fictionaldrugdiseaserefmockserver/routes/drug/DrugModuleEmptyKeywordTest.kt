package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Phase 11-12a (Issue #101): `empty` シナリオ × `keyword` でシナリオ非依存性原則を検証する。
 *
 * §基本方針 9 シナリオ非依存原則: keyword 検索は items が 0 件の `empty` シナリオでも
 * 200 OK + `total_count=0` + 空配列を返さねばならない (例外送出や 5xx は禁止)。
 *
 * Phase 11-10a の `DrugListFixtures.resolve` + `DrugSearchService.applyKeyword` 統合で
 * `applyKeyword(emptyList(), ...)` が空リストを素通しする実装になっているため、本テストは
 * 既実装に対する pin (characterization) として位置付ける。Route ハンドラが empty シナリオ時に
 * fixture 取得を skip して例外を投げる等の退行が混入した場合に検出する。
 */
class DrugModuleEmptyKeywordTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs under empty scenario with keyword returns items size zero and 200`() = testApplication {
        application { module() }

        val configResponse = client.post(urlString = "/__admin/configs/drugList") {
            contentType(type = ContentType.Application.Json)
            setBody(body = """{"state":"empty"}""")
        }

        val response = client.get(
            urlString = "/v1/drugs?keyword=whatever&keyword_target=both&keyword_match=partial",
        )

        val body = response.parseBody()
        assertEquals(
            expected = EmptyEnvelopeSnapshot(
                configStatus = HttpStatusCode.OK,
                status = HttpStatusCode.OK,
                totalCount = 0,
                itemsSize = 0,
            ),
            actual = EmptyEnvelopeSnapshot(
                configStatus = configResponse.status,
                status = response.status,
                totalCount = body.totalCountOrNull(),
                itemsSize = body.itemsSizeOrNull(),
            ),
        )

        client.post(urlString = "/__admin/reset")
    }

    private suspend fun HttpResponse.parseBody(): JsonObject {
        return json.parseToJsonElement(string = bodyAsText()).jsonObject
    }

    private fun JsonObject.totalCountOrNull(): Int? =
        this["total_count"]?.jsonPrimitive?.content?.toIntOrNull()

    private fun JsonObject.itemsSizeOrNull(): Int? =
        this["items"]?.jsonArray?.size

    private data class EmptyEnvelopeSnapshot(
        val configStatus: HttpStatusCode,
        val status: HttpStatusCode,
        val totalCount: Int?,
        val itemsSize: Int?,
    )
}
