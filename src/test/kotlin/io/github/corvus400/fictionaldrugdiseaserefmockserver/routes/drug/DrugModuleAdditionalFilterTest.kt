package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugModuleAdditionalFilterTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs with adverse_reaction_keyword filters items to total_count less than 120 under default`() =
        testApplication {
            application { module() }

            // 「重篤な副作用 2」は serious[].name に count=2 の医薬品のみが保持する文字列。
            // SHORT_LIST_COUNT_RANGE = 1..2 のため fixture 全 120 件のうち count=2 のサブセットのみ一致する。
            val keyword = "%E9%87%8D%E7%AF%A4%E3%81%AA%E5%89%AF%E4%BD%9C%E7%94%A8%202"
            val response = client.get("/v1/drugs?adverse_reaction_keyword=$keyword&page_size=100")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
            val items = body["items"]?.jsonArray
            val violations = listOfNotNull(
                "status must be 200 OK but was ${response.status}".takeUnless {
                    response.status == HttpStatusCode.OK
                },
                "response must include total_count".takeUnless { totalCount != null },
                "total_count=$totalCount must be 1..<120 for adverse_reaction_keyword=重篤な副作用 2"
                    .takeUnless { totalCount != null && totalCount in 1 until 120 },
                "response must include items array".takeUnless { items != null },
                "filtered items must be non-empty for adverse_reaction_keyword=重篤な副作用 2"
                    .takeUnless { items?.isNotEmpty() == true },
            )

            assertTrue(
                actual = violations.isEmpty(),
                message = "adverse_reaction_keyword filter violations: $violations",
            )
        }

    @Test
    fun `GET drugs with precaution_category=PREGNANT returns single-value filtered items`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs?precaution_category=PREGNANT&page_size=100")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
            val items = body["items"]?.jsonArray
            val violations = listOfNotNull(
                "status must be 200 OK but was ${response.status}".takeUnless {
                    response.status == HttpStatusCode.OK
                },
                "response must include total_count".takeUnless { totalCount != null },
                "total_count=$totalCount must be 1..<120 for precaution_category=PREGNANT"
                    .takeUnless { totalCount != null && totalCount in 1 until 120 },
                "response must include items array".takeUnless { items != null },
                "filtered items must be non-empty for precaution_category=PREGNANT"
                    .takeUnless { items?.isNotEmpty() == true },
            )

            assertTrue(
                actual = violations.isEmpty(),
                message = "precaution_category single filter violations: $violations",
            )
        }

    @Test
    fun `GET drugs with adverse_reaction_keyword and precaution_category returns AND intersection`() =
        testApplication {
            application { module() }

            // 「重篤な副作用 2」は serious[].name に count=2 の医薬品のみが保持する文字列。
            val keyword = "%E9%87%8D%E7%AF%A4%E3%81%AA%E5%89%AF%E4%BD%9C%E7%94%A8%202"

            val keywordOnlyTotal = client.get("/v1/drugs?adverse_reaction_keyword=$keyword&page_size=100")
                .let { json.parseToJsonElement(string = it.bodyAsText()).jsonObject }
                .let { it["total_count"]?.jsonPrimitive?.content?.toInt() }

            val precautionOnlyTotal = client.get("/v1/drugs?precaution_category=PREGNANT&page_size=100")
                .let { json.parseToJsonElement(string = it.bodyAsText()).jsonObject }
                .let { it["total_count"]?.jsonPrimitive?.content?.toInt() }

            val andResponse = client.get(
                "/v1/drugs?adverse_reaction_keyword=$keyword&precaution_category=PREGNANT&page_size=100",
            )

            val andBody = json.parseToJsonElement(string = andResponse.bodyAsText()).jsonObject
            val andTotal = andBody["total_count"]?.jsonPrimitive?.content?.toInt()
            val violations = listOfNotNull(
                "keyword-only response must include total_count".takeUnless { keywordOnlyTotal != null },
                "precaution-only response must include total_count".takeUnless { precautionOnlyTotal != null },
                "status must be 200 OK but was ${andResponse.status}".takeUnless {
                    andResponse.status == HttpStatusCode.OK
                },
                "AND response must include total_count".takeUnless { andTotal != null },
                "AND total=$andTotal must be <= min(keyword-only=$keywordOnlyTotal, precaution-only=$precautionOnlyTotal)"
                    .takeUnless {
                        andTotal != null &&
                            keywordOnlyTotal != null &&
                            precautionOnlyTotal != null &&
                            andTotal <= minOf(a = keywordOnlyTotal, b = precautionOnlyTotal)
                    },
                "AND total=$andTotal must be strictly smaller than at least one single filter"
                    .takeUnless {
                        andTotal != null &&
                            keywordOnlyTotal != null &&
                            precautionOnlyTotal != null &&
                            (andTotal < keywordOnlyTotal || andTotal < precautionOnlyTotal)
                    },
            )

            assertTrue(
                actual = violations.isEmpty(),
                message = "adverse_reaction_keyword and precaution_category AND violations: $violations",
            )
        }

    @Test
    fun `GET drugs with precaution_category PREGNANT and GERIATRIC returns OR-filtered items`() =
        testApplication {
            application { module() }

            val singlePregnantResponse =
                client.get("/v1/drugs?precaution_category=PREGNANT&page_size=100")
            val singlePregnantTotal = json.parseToJsonElement(string = singlePregnantResponse.bodyAsText())
                .jsonObject["total_count"]?.jsonPrimitive?.content?.toInt()

            val orResponse =
                client.get("/v1/drugs?precaution_category=PREGNANT&precaution_category=GERIATRIC&page_size=100")

            val orBody = json.parseToJsonElement(string = orResponse.bodyAsText()).jsonObject
            val orTotal = orBody["total_count"]?.jsonPrimitive?.content?.toInt()
            val violations = listOfNotNull(
                "single-value PREGNANT status must be 200 OK but was ${singlePregnantResponse.status}"
                    .takeUnless { singlePregnantResponse.status == HttpStatusCode.OK },
                "single-value PREGNANT response must include total_count".takeUnless {
                    singlePregnantTotal != null
                },
                "OR status must be 200 OK but was ${orResponse.status}".takeUnless {
                    orResponse.status == HttpStatusCode.OK
                },
                "OR response must include total_count".takeUnless { orTotal != null },
                "OR total_count=$orTotal must be 1..<120".takeUnless {
                    orTotal != null && orTotal in 1 until 120
                },
                "OR total=$orTotal must be >= single-value PREGNANT total=$singlePregnantTotal"
                    .takeUnless { orTotal != null && singlePregnantTotal != null && orTotal >= singlePregnantTotal },
            )

            assertTrue(
                actual = violations.isEmpty(),
                message = "precaution_category OR filter violations: $violations",
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
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = configResponse.status,
                message = "Admin API must accept drugList empty scenario override",
            )

            val response = client.get(urlString = "/v1/drugs?adverse_reaction_keyword=X")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
            val items = body["items"]?.jsonArray
            assertEquals(
                expected = EmptyEnvelopeSnapshot(status = HttpStatusCode.OK, totalCount = 0, itemsSize = 0),
                actual = EmptyEnvelopeSnapshot(
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
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = configResponse.status,
                message = "Admin API must accept drugList empty scenario override",
            )

            val response = client.get(urlString = "/v1/drugs?precaution_category=PREGNANT")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
            val items = body["items"]?.jsonArray
            assertEquals(
                expected = EmptyEnvelopeSnapshot(status = HttpStatusCode.OK, totalCount = 0, itemsSize = 0),
                actual = EmptyEnvelopeSnapshot(
                    status = response.status,
                    totalCount = totalCount,
                    itemsSize = items?.size,
                ),
            )

            client.post(urlString = "/__admin/reset")
        }

    private data class EmptyEnvelopeSnapshot(
        val status: HttpStatusCode,
        val totalCount: Int?,
        val itemsSize: Int?,
    )

    private data class ErrorSnapshot(
        val status: HttpStatusCode,
        val code: String?,
        val messageMentionsInvalid: Boolean,
    )
}
