package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLParameter
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DrugModuleKeywordTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs with keyword target brand and match partial returns filtered count less than total`() =
        testApplication {
            application { module() }

            val sampleBrandName = client.get(urlString = "/v1/drugs/drug_0001").extractBrandName()
            assertTrue(
                actual = sampleBrandName.length >= KEYWORD_PREFIX_LENGTH,
                message = "drug_0001 brand_name must be at least $KEYWORD_PREFIX_LENGTH chars to take prefix " +
                    "(got=\"$sampleBrandName\")",
            )
            val keywordPrefix = sampleBrandName.take(n = KEYWORD_PREFIX_LENGTH)
            val encodedKeyword = keywordPrefix.encodeURLParameter()

            val totalResponse = client.get(urlString = "/v1/drugs?page_size=100")
            val filteredResponse = client.get(
                urlString = "/v1/drugs?keyword=$encodedKeyword" +
                    "&keyword_target=brand&keyword_match=partial&page_size=100",
            )

            val total = totalResponse.totalCount()
            val filtered = filteredResponse.totalCount()
            assertEquals(
                expected = KeywordFilterSnapshot(
                    totalStatus = HttpStatusCode.OK,
                    filteredStatus = HttpStatusCode.OK,
                    total = DEFAULT_TOTAL_COUNT,
                    filteredIsNonTrivialSubset = true,
                ),
                actual = KeywordFilterSnapshot(
                    totalStatus = totalResponse.status,
                    filteredStatus = filteredResponse.status,
                    total = total,
                    filteredIsNonTrivialSubset = filtered in MIN_FILTERED_COUNT until total,
                ),
                message = "fixture-derived keyword prefix must filter default $DEFAULT_TOTAL_COUNT to a non-trivial " +
                    "subset that contains drug_0001 plus at least one other drug " +
                    "(filtered=$filtered total=$total keywordPrefix=\"$keywordPrefix\")",
            )
        }

    private companion object {
        /**
         * `brand_name` から取り出すキーワード接頭辞長。`DrugModuleDefaultKeywordFilteredTest`
         * (PR #289) の SSOT パターンと揃える: katakana 2 文字は default 120 件全てを被覆するほど
         * 一般的でも、drug_0001 だけが孤立するほど特殊でもない経験則。
         */
        const val KEYWORD_PREFIX_LENGTH: Int = 2

        /**
         * filtered の下限。`drug_0001` 自身がマッチする 1 件 (定義上トートロジー) に加え、
         * 少なくとももう 1 件マッチすることを要求し、フィルタが実質機能していることを検証する。
         */
        const val MIN_FILTERED_COUNT: Int = 2

        /**
         * default シナリオが常に 120 件であることを Phase 11 全体で固定する不変量。
         */
        const val DEFAULT_TOTAL_COUNT: Int = 120
    }

    private data class KeywordFilterSnapshot(
        val totalStatus: HttpStatusCode,
        val filteredStatus: HttpStatusCode,
        val total: Int,
        val filteredIsNonTrivialSubset: Boolean,
    )

    private suspend fun HttpResponse.totalCount(): Int {
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
        assertNotNull(actual = totalCount, message = "response must include total_count")
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
