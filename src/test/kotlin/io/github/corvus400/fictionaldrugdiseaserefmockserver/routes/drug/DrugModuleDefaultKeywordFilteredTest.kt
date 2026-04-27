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
 * 0 件超かつ全件未満を返すことを保証する (タウトロジー回避)。
 *
 * Issue #100 本文の Red テンプレートは
 * - endpoint 名が `drugs` (実装は `drugList`)
 * - body が plain-text `default` (Admin API は JSON `{"state":"default"}` を要求)
 * - `keyword=` が空 (`DrugSearchService.applyKeyword` は空白だと素通しのため filtered<total が成立しない)
 *
 * という typo を含むため、Phase 11-10b の `DiseaseModuleKeywordTest` (commit 80f183d) と同じ
 * 判断で実体に整合させて書き換える。
 *
 * keyword 候補は `drug_0001` の `brand_name` を実行時に取り出して URL-encode する: 既存 fixture 由来文字列を
 * SSOT で参照することで、name generator (`DrugGenerator`) の出力変更にも耐える。既存の
 * `DrugModuleKeywordTest` が hard-code する「スーパー」は実際には default シナリオの brand に
 * 含まれず filtered=0 で通っていた (filtered<total が満たされる) ため、本テストでは採用しない。
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
        assertEquals(
            expected = HttpStatusCode.OK,
            actual = configResponse.status,
            message = "Admin API must accept drugList default scenario override",
        )

        val knownKeyword = client.get(urlString = "/drugs/drug_0001").extractBrandName()
        val encodedKeyword = knownKeyword.encodeURLParameter()

        val total = client.get(urlString = "/drugs?page_size=100").totalCount()
        val filtered = client.get(
            urlString = "/drugs?keyword=$encodedKeyword" +
                "&keyword_target=both&keyword_match=partial&page_size=100",
        ).totalCount()

        assertEquals(
            expected = 120,
            actual = total,
            message = "default scenario must populate 120 drugs (total=$total)",
        )
        assertTrue(
            actual = filtered in 1 until total,
            message = "fixture-derived keyword must filter to a strict subset of default 120 " +
                "(filtered=$filtered total=$total knownKeyword=$knownKeyword)",
        )

        client.post(urlString = "/__admin/reset")
    }

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
