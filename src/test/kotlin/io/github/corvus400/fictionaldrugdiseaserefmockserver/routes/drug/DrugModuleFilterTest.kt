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

class DrugModuleFilterTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs category_atc=A returns total_count=20 with all items atc_code starting with A`() = testApplication {
        application { module() }

        val response = client.get("/drugs?category_atc=A&page_size=100")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = 20,
            actual = body["total_count"]?.jsonPrimitive?.content?.toInt(),
            message = "ATC A 群は 120 件中 20 件 (医薬品モデル仕様の組合せ数計算)",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertEquals(
            expected = 20,
            actual = items.size,
            message = "items.size must be 20 when page_size=100 covers all ATC-A drugs",
        )
        items.forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            assertNotNull(id, "item must expose id")
            val detailResponse = client.get("/drugs/$id")
            assertEquals(HttpStatusCode.OK, detailResponse.status, "detail GET must succeed for id=$id")
            val detail = json.parseToJsonElement(string = detailResponse.bodyAsText()).jsonObject
            val atcCode = detail["atc_code"]?.jsonPrimitive?.content
            assertNotNull(atcCode, "drug $id must expose atc_code")
            assertTrue(
                actual = atcCode.startsWith(prefix = "A"),
                message = "item id=$id has atc_code=$atcCode; must start with 'A' under category_atc=A filter",
            )
        }
    }
}
