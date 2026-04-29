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
import kotlin.test.assertNotNull
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
            val response = client.get("/drugs?adverse_reaction_keyword=$keyword&page_size=100")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
            assertNotNull(totalCount, "response must include total_count")
            assertTrue(
                actual = totalCount in 1 until 120,
                message = "total_count=$totalCount must be 1..<120 for adverse_reaction_keyword=重篤な副作用 2 " +
                    "(filter must be applied; baseline without filter would be 120)",
            )
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response must include items array")
            assertTrue(
                actual = items.isNotEmpty(),
                message = "filtered items must be non-empty for adverse_reaction_keyword=重篤な副作用 2",
            )
        }

    @Test
    fun `GET drugs with precaution_category=PREGNANT returns single-value filtered items`() =
        testApplication {
            application { module() }

            val response = client.get("/drugs?precaution_category=PREGNANT&page_size=100")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
            assertNotNull(totalCount, "response must include total_count")
            assertTrue(
                actual = totalCount in 1 until 120,
                message = "total_count=$totalCount must be 1..<120 for precaution_category=PREGNANT " +
                    "(filter must be applied; baseline without filter would be 120)",
            )
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response must include items array")
            assertTrue(
                actual = items.isNotEmpty(),
                message = "filtered items must be non-empty for precaution_category=PREGNANT",
            )
        }

    @Test
    fun `GET drugs with adverse_reaction_keyword and precaution_category returns AND intersection`() =
        testApplication {
            application { module() }

            // 「重篤な副作用 2」は serious[].name に count=2 の医薬品のみが保持する文字列。
            val keyword = "%E9%87%8D%E7%AF%A4%E3%81%AA%E5%89%AF%E4%BD%9C%E7%94%A8%202"

            val keywordOnlyTotal = client.get("/drugs?adverse_reaction_keyword=$keyword&page_size=100")
                .let { json.parseToJsonElement(string = it.bodyAsText()).jsonObject }
                .let { it["total_count"]?.jsonPrimitive?.content?.toInt() }
            assertNotNull(keywordOnlyTotal, "keyword-only response must include total_count")

            val precautionOnlyTotal = client.get("/drugs?precaution_category=PREGNANT&page_size=100")
                .let { json.parseToJsonElement(string = it.bodyAsText()).jsonObject }
                .let { it["total_count"]?.jsonPrimitive?.content?.toInt() }
            assertNotNull(precautionOnlyTotal, "precaution-only response must include total_count")

            val andResponse = client.get(
                "/drugs?adverse_reaction_keyword=$keyword&precaution_category=PREGNANT&page_size=100",
            )

            assertEquals(HttpStatusCode.OK, andResponse.status)
            val andBody = json.parseToJsonElement(string = andResponse.bodyAsText()).jsonObject
            val andTotal = andBody["total_count"]?.jsonPrimitive?.content?.toInt()
            assertNotNull(andTotal, "AND response must include total_count")
            val singleMin = minOf(a = keywordOnlyTotal, b = precautionOnlyTotal)
            assertTrue(
                actual = andTotal <= singleMin,
                message = "AND total=$andTotal must be <= min(keyword-only=$keywordOnlyTotal, " +
                    "precaution-only=$precautionOnlyTotal) (intersection cannot exceed either single filter)",
            )
            assertTrue(
                actual = andTotal < keywordOnlyTotal || andTotal < precautionOnlyTotal,
                message = "AND total=$andTotal must be strictly smaller than at least one single filter " +
                    "(keyword-only=$keywordOnlyTotal, precaution-only=$precautionOnlyTotal); " +
                    "otherwise the test would also pass under OR semantics and cannot discriminate AND",
            )
        }

    @Test
    fun `GET drugs with precaution_category PREGNANT and GERIATRIC returns OR-filtered items`() =
        testApplication {
            application { module() }

            val singlePregnantResponse =
                client.get("/drugs?precaution_category=PREGNANT&page_size=100")
            assertEquals(HttpStatusCode.OK, singlePregnantResponse.status)
            val singlePregnantTotal = json.parseToJsonElement(string = singlePregnantResponse.bodyAsText())
                .jsonObject["total_count"]?.jsonPrimitive?.content?.toInt()
            assertNotNull(singlePregnantTotal, "single-value PREGNANT response must include total_count")

            val orResponse =
                client.get("/drugs?precaution_category=PREGNANT&precaution_category=GERIATRIC&page_size=100")

            assertEquals(HttpStatusCode.OK, orResponse.status)
            val orBody = json.parseToJsonElement(string = orResponse.bodyAsText()).jsonObject
            val orTotal = orBody["total_count"]?.jsonPrimitive?.content?.toInt()
            assertNotNull(orTotal, "OR response must include total_count")
            assertTrue(
                actual = orTotal in 1 until 120,
                message = "OR total_count=$orTotal must be 1..<120 (filter must remain applied)",
            )
            assertTrue(
                actual = orTotal >= singlePregnantTotal,
                message = "OR total=$orTotal must be >= single-value PREGNANT total=$singlePregnantTotal " +
                    "(adding GERIATRIC must not reduce the result set under OR semantics)",
            )
        }

    @Test
    fun `GET drugs with precaution_category=INVALID returns HTTP 400 and INVALID_PRECAUTION_CATEGORY error`() =
        testApplication {
            application { module() }

            val response = client.get("/drugs?precaution_category=INVALID")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val code = body["code"]?.jsonPrimitive?.content
            assertEquals("INVALID_PRECAUTION_CATEGORY", code)
            val message = body["message"]?.jsonPrimitive?.content
            assertNotNull(message, "ErrorResponse must include a non-null message describing the rejected value")
            assertTrue(
                actual = message.contains("INVALID"),
                message = "ErrorResponse message=$message must mention the rejected raw value 'INVALID'",
            )
        }
}
