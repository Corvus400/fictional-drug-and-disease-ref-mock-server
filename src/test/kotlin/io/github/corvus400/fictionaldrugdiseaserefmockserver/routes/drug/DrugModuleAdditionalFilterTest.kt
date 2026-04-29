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
}
