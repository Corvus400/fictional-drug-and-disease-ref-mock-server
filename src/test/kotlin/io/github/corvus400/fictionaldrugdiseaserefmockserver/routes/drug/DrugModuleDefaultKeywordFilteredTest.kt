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
import io.ktor.http.encodeURLParameter
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Phase 11-11a (Issue #100): `default` シナリオ × `keyword` で filtered count を検証する。
 *
 * Phase 11-10a で実装済みの `DrugListFixtures.resolve` + `DrugSearchService.applyKeyword`
 * 統合が、`default` シナリオ (120 件) に対して Fixture 由来のキーワードで絞り込みを掛け、
 * 0 件超かつ全件未満を返すことを保証する (トートロジー回避)。
 *
 * Issue #100 本文の Red テンプレートは
 * - endpoint 名が `drugs` (実装は `drugList`)
 * - body が plain-text `default` (Admin API は JSON `{"state":"default"}` を要求)
 * - `keyword=` が空 (`DrugSearchService.applyKeyword` は空白だと素通しのため filtered<total が成立しない)
 *
 * という typo を含むため、Phase 11-10b の `DiseaseModuleKeywordTest` (commit 80f183d) と同じ
 * 判断で実体に整合させて書き換える。
 *
 * keyword 候補は `drug_0001` の `brand_name` 冒頭 `KEYWORD_PREFIX_LENGTH` 文字を実行時に取り出し
 * URL-encode する: 既存 fixture 由来文字列を SSOT で参照することで、name generator
 * (`DrugGenerator`) の出力変更にも耐える。既存の `DrugModuleKeywordTest` が hard-code する
 * 「スーパー」は実際には default シナリオの brand に含まれず filtered=0 で通っていた
 * (`filtered<total` が満たされる) ため採用しない。
 *
 * 下限を `filtered >= 2` に強化することで「drug_0001 自身が自分の brand_name にマッチする」
 * という定義上のトートロジー (Issue #100 コメントが回避を求める性質) を回避し、
 * `default` シナリオに drug_0001 以外にも該当 drug が少なくとも 1 件存在することを要求する。
 * 全文ではなく接頭辞を使うのは、brand_name 全体だと drug_0001 のみマッチして filtered=1 に
 * 落ち込みやすいため。katakana 接頭辞 2 文字は、120 件全てを被覆するほど一般的でも、
 * drug_0001 だけが孤立するほど特殊でもない狙い。
 */
class DrugModuleDefaultKeywordFilteredTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs under default scenario with keyword returns expected filtered count`() = testApplication {
        application { module() }

        val configResponse = client.post(urlString = "/__admin/configs/drugList") {
            contentType(type = ContentType.Application.Json)
            setBody(body = """{"state":"default"}""")
        }

        val sampleBrandName = client.get(urlString = "/v1/drugs/drug_0001").extractBrandName()
        val keywordPrefix = sampleBrandName.take(n = KEYWORD_PREFIX_LENGTH)
        val encodedKeyword = keywordPrefix.encodeURLParameter()

        val total = client.get(urlString = "/v1/drugs?page_size=100").totalCount()
        val filtered = client.get(
            urlString = "/v1/drugs?keyword=$encodedKeyword" +
                "&keyword_target=both&keyword_match=partial&page_size=100",
        ).totalCount()

        assertEquals(
            expected = KeywordFilterSnapshot(
                configStatus = HttpStatusCode.OK,
                brandNameLongEnough = true,
                total = 120,
                filteredIsNonTrivialSubset = true,
            ),
            actual = KeywordFilterSnapshot(
                configStatus = configResponse.status,
                brandNameLongEnough = sampleBrandName.length >= KEYWORD_PREFIX_LENGTH,
                total = total,
                filteredIsNonTrivialSubset = filtered in MIN_FILTERED_COUNT until total,
            ),
            message = "fixture-derived keyword prefix must filter default 120 to a non-trivial " +
                "subset that contains drug_0001 plus at least one other drug " +
                "(filtered=$filtered total=$total keywordPrefix=\"$keywordPrefix\")",
        )

        client.post(urlString = "/__admin/reset")
    }

    private companion object {
        /**
         * `brand_name` から取り出すキーワード接頭辞長。katakana 2 文字は default 120 件全てを
         * 被覆するほど一般的でも、drug_0001 だけが孤立するほど特殊でもない経験則。
         */
        const val KEYWORD_PREFIX_LENGTH: Int = 2

        /**
         * filtered の下限。`drug_0001` 自身がマッチする 1 件 (定義上トートロジー) に加え、
         * 少なくとももう 1 件マッチすることを要求し、フィルタが実質機能していることを検証する。
         */
        const val MIN_FILTERED_COUNT: Int = 2
    }

    private data class KeywordFilterSnapshot(
        val configStatus: HttpStatusCode,
        val brandNameLongEnough: Boolean,
        val total: Int,
        val filteredIsNonTrivialSubset: Boolean,
    )

    private suspend fun HttpResponse.totalCount(): Int {
        assertEquals(expected = HttpStatusCode.OK, actual = status)
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
        assertNotNull(actual = totalCount, message = "response body must have a numeric total_count")
        return totalCount
    }

    private suspend fun HttpResponse.extractBrandName(): String {
        assertEquals(expected = HttpStatusCode.OK, actual = status)
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        val brandName = body["brand_name"]?.jsonPrimitive?.content
        assertNotNull(actual = brandName, message = "drug detail must expose brand_name to derive keyword")
        assertTrue(actual = brandName.isNotBlank(), message = "brand_name must be non-blank to be a usable keyword")
        return brandName
    }
}
