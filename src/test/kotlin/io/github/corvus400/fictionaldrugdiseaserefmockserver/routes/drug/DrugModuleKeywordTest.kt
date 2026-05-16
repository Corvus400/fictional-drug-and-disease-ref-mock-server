package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLParameter
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugModuleKeywordTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs with keyword target brand and match partial returns filtered count less than total`() =
        testApplication {
            application { module() }

            val brandNameSnapshot = client.get(urlString = "/v1/drugs/drug_0001").brandNameSnapshot()
            val keywordPrefix = brandNameSnapshot.brandName.orEmpty().take(n = KEYWORD_PREFIX_LENGTH)
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
                    brandNameLongEnough = true,
                    detailStatus = HttpStatusCode.OK,
                    totalStatus = HttpStatusCode.OK,
                    filteredStatus = HttpStatusCode.OK,
                    total = DEFAULT_TOTAL_COUNT,
                    filteredIsNonTrivialSubset = true,
                ),
                actual = KeywordFilterSnapshot(
                    brandNameLongEnough = brandNameSnapshot.brandName.orEmpty().length >= KEYWORD_PREFIX_LENGTH,
                    detailStatus = brandNameSnapshot.status,
                    totalStatus = totalResponse.status,
                    filteredStatus = filteredResponse.status,
                    total = total,
                    filteredIsNonTrivialSubset = total?.let { totalCount ->
                        filtered?.let { filteredCount -> filteredCount in MIN_FILTERED_COUNT until totalCount }
                    } == true,
                ),
                message = "fixture-derived keyword prefix must filter default $DEFAULT_TOTAL_COUNT to a non-trivial " +
                    "subset that contains drug_0001 plus at least one other drug " +
                    "(filtered=$filtered total=$total keywordPrefix=\"$keywordPrefix\")",
            )
        }

    @Test
    fun `GET drugs with keyword target all can find drug by ATC and YJ codes`() = testApplication {
        application { module() }

        val codeSnapshot = client.get(urlString = "/v1/drugs/drug_0001").drugCodeSnapshot()
        val atcResponse = client.get(
            urlString = "/v1/drugs?keyword=${codeSnapshot.atcCode.orEmpty().encodeURLParameter()}" +
                "&keyword_target=all&keyword_match=partial&page_size=100",
        )
        val yjResponse = client.get(
            urlString = "/v1/drugs?keyword=${codeSnapshot.yjCode.orEmpty().encodeURLParameter()}" +
                "&keyword_target=all&keyword_match=partial&page_size=100",
        )

        assertEquals(
            expected = CodeKeywordRouteSnapshot(
                detailStatus = HttpStatusCode.OK,
                codeFieldsPresent = true,
                atcStatus = HttpStatusCode.OK,
                yjStatus = HttpStatusCode.OK,
                atcContainsDrug0001 = true,
                yjContainsDrug0001 = true,
            ),
            actual = CodeKeywordRouteSnapshot(
                detailStatus = codeSnapshot.status,
                codeFieldsPresent = !codeSnapshot.atcCode.isNullOrBlank() && !codeSnapshot.yjCode.isNullOrBlank(),
                atcStatus = atcResponse.status,
                yjStatus = yjResponse.status,
                atcContainsDrug0001 = atcResponse.itemIds().contains(element = "drug_0001"),
                yjContainsDrug0001 = yjResponse.itemIds().contains(element = "drug_0001"),
            ),
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
        val brandNameLongEnough: Boolean,
        val detailStatus: HttpStatusCode,
        val totalStatus: HttpStatusCode,
        val filteredStatus: HttpStatusCode,
        val total: Int?,
        val filteredIsNonTrivialSubset: Boolean,
    )

    private data class BrandNameSnapshot(
        val status: HttpStatusCode,
        val brandName: String?,
    )

    private data class DrugCodeSnapshot(
        val status: HttpStatusCode,
        val atcCode: String?,
        val yjCode: String?,
    )

    private data class CodeKeywordRouteSnapshot(
        val detailStatus: HttpStatusCode,
        val codeFieldsPresent: Boolean,
        val atcStatus: HttpStatusCode,
        val yjStatus: HttpStatusCode,
        val atcContainsDrug0001: Boolean,
        val yjContainsDrug0001: Boolean,
    )

    private suspend fun HttpResponse.totalCount(): Int? {
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        return body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
    }

    private suspend fun HttpResponse.itemIds(): List<String> {
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray ?: JsonArray(content = emptyList())
        return items.mapNotNull { item -> item.jsonObject["id"]?.jsonPrimitive?.content }
    }

    private suspend fun HttpResponse.brandNameSnapshot(): BrandNameSnapshot {
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        return BrandNameSnapshot(
            status = status,
            brandName = body["brand_name"]?.jsonPrimitive?.content,
        )
    }

    private suspend fun HttpResponse.drugCodeSnapshot(): DrugCodeSnapshot {
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        return DrugCodeSnapshot(
            status = status,
            atcCode = body["atc_code"]?.jsonPrimitive?.content,
            yjCode = body["yj_code"]?.jsonPrimitive?.content,
        )
    }
}
