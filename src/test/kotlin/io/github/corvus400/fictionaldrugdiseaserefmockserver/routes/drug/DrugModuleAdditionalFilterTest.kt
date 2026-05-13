package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugModuleAdditionalFilterTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs with adverse_reaction_keyword returns 200 OK`() =
        testApplication {
            application { module() }

            val response = client.get(adverseReactionKeywordUrl())

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        }

    @Test
    fun `GET drugs with adverse_reaction_keyword filters total_count to strict subset`() =
        testApplication {
            application { module() }

            val totalCount = client.get(adverseReactionKeywordUrl()).totalCount()

            assertTrue(
                actual = totalCount in 1 until DEFAULT_DRUG_COUNT,
                message = "total_count=$totalCount must be 1..<120 for adverse_reaction_keyword=" +
                    adverseReactionKeyword,
            )
        }

    @Test
    fun `GET drugs with adverse_reaction_keyword returns non-empty items`() =
        testApplication {
            application { module() }

            val itemsSize = client.get(adverseReactionKeywordUrl()).itemsSize()

            assertTrue(
                actual = itemsSize > 0,
                message = "filtered items must be non-empty for adverse_reaction_keyword=$adverseReactionKeyword",
            )
        }

    @Test
    fun `GET drugs with precaution_category=PREGNANT returns 200 OK`() =
        testApplication {
            application { module() }

            val response = client.get(PREGNANT_PRECAUTION_URL)

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        }

    @Test
    fun `GET drugs with precaution_category=PREGNANT filters total_count to strict subset`() =
        testApplication {
            application { module() }

            val totalCount = client.get(PREGNANT_PRECAUTION_URL).totalCount()

            assertTrue(
                actual = totalCount in 1 until DEFAULT_DRUG_COUNT,
                message = "total_count=$totalCount must be 1..<120 for precaution_category=PREGNANT",
            )
        }

    @Test
    fun `GET drugs with precaution_category=PREGNANT returns non-empty items`() =
        testApplication {
            application { module() }

            val itemsSize = client.get(PREGNANT_PRECAUTION_URL).itemsSize()

            assertTrue(
                actual = itemsSize > 0,
                message = "filtered items must be non-empty for precaution_category=PREGNANT",
            )
        }

    @Test
    fun `GET drugs with adverse_reaction_keyword and precaution_category returns 200 OK`() =
        testApplication {
            application { module() }

            val response = client.get(andFilterUrl())

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        }

    @Test
    fun `GET drugs with adverse_reaction_keyword and precaution_category total_count is no larger than singles`() =
        testApplication {
            application { module() }

            val keywordOnlyTotal = client.get(adverseReactionKeywordUrl()).totalCount()
            val precautionOnlyTotal = client.get(PREGNANT_PRECAUTION_URL).totalCount()
            val andTotal = client.get(andFilterUrl()).totalCount()

            assertTrue(
                actual = andTotal <= minOf(a = keywordOnlyTotal, b = precautionOnlyTotal),
                message = "AND total=$andTotal must be <= min(keyword-only=$keywordOnlyTotal, " +
                    "precaution-only=$precautionOnlyTotal)",
            )
        }

    @Test
    fun `GET drugs with adverse_reaction_keyword and precaution_category total_count is smaller than singles`() =
        testApplication {
            application { module() }

            val keywordOnlyTotal = client.get(adverseReactionKeywordUrl()).totalCount()
            val precautionOnlyTotal = client.get(PREGNANT_PRECAUTION_URL).totalCount()
            val andTotal = client.get(andFilterUrl()).totalCount()

            assertTrue(
                actual = andTotal < keywordOnlyTotal || andTotal < precautionOnlyTotal,
                message = "AND total=$andTotal must be strictly smaller than at least one single filter " +
                    "(keyword=$keywordOnlyTotal, precaution=$precautionOnlyTotal)",
            )
        }

    @Test
    fun `GET drugs with precaution_category PREGNANT and GERIATRIC returns 200 OK`() =
        testApplication {
            application { module() }

            val response = client.get(PREGNANT_OR_GERIATRIC_URL)

            assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        }

    @Test
    fun `GET drugs with precaution_category PREGNANT and GERIATRIC filters total_count to strict subset`() =
        testApplication {
            application { module() }

            val orTotal = client.get(PREGNANT_OR_GERIATRIC_URL).totalCount()

            assertTrue(
                actual = orTotal in 1 until DEFAULT_DRUG_COUNT,
                message = "OR total_count=$orTotal must be 1..<120",
            )
        }

    @Test
    fun `GET drugs with precaution_category PREGNANT and GERIATRIC total_count is at least PREGNANT only`() =
        testApplication {
            application { module() }

            val singlePregnantTotal = client.get(PREGNANT_PRECAUTION_URL).totalCount()
            val orTotal = client.get(PREGNANT_OR_GERIATRIC_URL).totalCount()

            assertTrue(
                actual = orTotal >= singlePregnantTotal,
                message = "OR total=$orTotal must be >= single-value PREGNANT total=$singlePregnantTotal",
            )
        }

    @Test
    fun `GET drugs with precaution_category=INVALID returns HTTP 400 and INVALID_PRECAUTION_CATEGORY error`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs?precaution_category=INVALID")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val code = body["code"]?.jsonPrimitive?.content
            val message = body["message"]?.jsonPrimitive?.content

            assertEquals(
                expected = ErrorSnapshot(
                    status = HttpStatusCode.BadRequest,
                    code = "INVALID_PRECAUTION_CATEGORY",
                    messageMentionsInvalid = true,
                ),
                actual = ErrorSnapshot(
                    status = response.status,
                    code = code,
                    messageMentionsInvalid = message?.contains("INVALID") == true,
                ),
                message = "ErrorResponse must describe the rejected raw value: $message",
            )
        }

    /**
     * 基本方針 9 シナリオ非依存原則の検証 (Phase 13-10 / Issue #147)。
     *
     * `empty` シナリオ (0 件入力) × `adverse_reaction_keyword` で 200 OK + 空 envelope を返し、
     * 例外送出や 5xx に退行しないことを pin する。Issue #147 本文の Red ケース 1。
     */
    @Test
    fun `GET drugs under empty scenario with adverse_reaction_keyword=X returns HTTP 200 + empty envelope`() =
        testApplication {
            application { module() }

            val configResponse = client.post(urlString = "/__admin/configs/drugList") {
                contentType(type = ContentType.Application.Json)
                setBody(body = """{"state":"empty"}""")
            }

            val response = client.get(urlString = "/v1/drugs?adverse_reaction_keyword=X")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
            val items = body["items"]?.jsonArray
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
                    totalCount = totalCount,
                    itemsSize = items?.size,
                ),
            )

            client.post(urlString = "/__admin/reset")
        }

    /**
     * 基本方針 9 シナリオ非依存原則の検証 (Phase 13-10 / Issue #147)。
     *
     * `empty` シナリオ (0 件入力) × `precaution_category` で 200 OK + 空 envelope を返すこと。
     * Issue #147 本文 Red ケース 2 — 本文記載の `PREGNANCY` は `PrecautionPopulationCategory` enum 名 として
     * 存在しないため、有効な enum 名 `PREGNANT` でリクエストする (`PREGNANCY` を送ると別仕様に従い
     * HTTP 400 + `INVALID_PRECAUTION_CATEGORY` が返り、Issue 期待の HTTP 200 と矛盾する)。
     */
    @Test
    fun `GET drugs under empty scenario with precaution_category=PREGNANT returns HTTP 200 + items size=0`() =
        testApplication {
            application { module() }

            val configResponse = client.post(urlString = "/__admin/configs/drugList") {
                contentType(type = ContentType.Application.Json)
                setBody(body = """{"state":"empty"}""")
            }

            val response = client.get(urlString = "/v1/drugs?precaution_category=PREGNANT")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
            val items = body["items"]?.jsonArray
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
                    totalCount = totalCount,
                    itemsSize = items?.size,
                ),
            )

            client.post(urlString = "/__admin/reset")
        }

    private data class EmptyEnvelopeSnapshot(
        val configStatus: HttpStatusCode,
        val status: HttpStatusCode,
        val totalCount: Int?,
        val itemsSize: Int?,
    )

    private data class ErrorSnapshot(
        val status: HttpStatusCode,
        val code: String?,
        val messageMentionsInvalid: Boolean,
    )

    private suspend fun io.ktor.client.statement.HttpResponse.totalCount(): Int =
        json.parseToJsonElement(string = bodyAsText()).jsonObject["total_count"]
            ?.jsonPrimitive
            ?.content
            ?.toIntOrNull()
            ?: error("response must include numeric total_count")

    private suspend fun io.ktor.client.statement.HttpResponse.itemsSize(): Int =
        json.parseToJsonElement(string = bodyAsText()).jsonObject["items"]
            ?.jsonArray
            ?.size
            ?: error("response must include items array")

    private fun adverseReactionKeywordUrl(): String =
        "/v1/drugs?adverse_reaction_keyword=$encodedAdverseReactionKeyword&page_size=100"

    private fun andFilterUrl(): String =
        "/v1/drugs?adverse_reaction_keyword=$encodedAdverseReactionKeyword&precaution_category=PREGNANT&page_size=100"

    private val adverseReactionKeyword: String by lazy {
        val adapter = FixmergeNameAdapter()
        val diseases =
            DiseaseGenerator(
                adapter = adapter,
                placeholderDictionary = DiseasePlaceholderDictionary(),
            ).generate(blueprints = DiseaseBlueprintFactory.build())
        DrugGenerator(
            adapter = adapter,
            placeholderDictionary = DrugPlaceholderDictionary(nameAdapter = adapter, diseases = diseases),
            diseases = diseases,
        ).generate(blueprints = DrugBlueprintFactory.build())
            .first { drug -> drug.adverseReactions.serious.size >= 2 }
            .adverseReactions
            .serious[1]
            .name
    }

    private val encodedAdverseReactionKeyword: String by lazy {
        URLEncoder.encode(adverseReactionKeyword, StandardCharsets.UTF_8)
    }

    private companion object {
        private const val DEFAULT_DRUG_COUNT = 120
        private const val PREGNANT_PRECAUTION_URL = "/v1/drugs?precaution_category=PREGNANT&page_size=100"
        private const val PREGNANT_OR_GERIATRIC_URL =
            "/v1/drugs?precaution_category=PREGNANT&precaution_category=GERIATRIC&page_size=100"
    }
}
