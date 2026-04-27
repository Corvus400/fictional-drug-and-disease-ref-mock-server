package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Phase 11-10b の Red 駆動テスト: `/diseases` ハンドラに `keyword` / `keyword_match` /
 * `keyword_target` クエリ受付と `DiseaseSearchService.applyKeyword` 統合を要求する。
 *
 * 親 Issue 本文の Red サンプルは `keyword=` (空文字) で `filtered < total` を要求するが、
 * `DiseaseSearchService` は `keyword.isNullOrBlank()` で全件素通しのため成立しない
 * (Issue 本文のプレースホルダ `<existing>` が脱落した typo と判断、`DiseaseSearchService`
 * の仕様変更は #110 でクローズ済みのため本フェーズではしない)。
 *
 * 本テストは確定的に絞り込まれる "存在しない" キーワードを使い、未統合状態 (現状) では
 * 受付されず全件返るため `total_count == 0` が外れて Red、ハンドラ統合後は 0 件で Green。
 */
class DiseaseModuleKeywordTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with non-matching keyword returns total_count 0 instead of full set`() = testApplication {
        application { module() }

        val total = client.get(urlString = "/diseases?page_size=100").totalCount()
        val filtered =
            client.get(
                urlString = "/diseases?keyword=zzznotexistzzz&keyword_target=name&keyword_match=partial&page_size=100",
            ).totalCount()

        assertTrue(
            actual = total > 0,
            message = "precondition: default scenario must populate diseases (total=$total)",
        )
        assertEquals(
            expected = 0,
            actual = filtered,
            message = "non-matching keyword must filter total to 0 (filtered=$filtered total=$total)",
        )
    }

    private suspend fun HttpResponse.totalCount(): Int {
        assertEquals(expected = HttpStatusCode.OK, actual = status)
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertNotNull(actual = totalCount, message = "response body must have a numeric total_count")
        return totalCount
    }
}
