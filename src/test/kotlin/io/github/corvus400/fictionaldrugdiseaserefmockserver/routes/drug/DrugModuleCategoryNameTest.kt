package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixtures
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

class DrugModuleCategoryNameTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs category_name= returns only drugs where therapeuticCategoryName equals the value`() =
        testApplication {
            application { module() }

            val targetCategoryName = "消化器系および代謝"
            val response = client.get("/drugs?category_name=$targetCategoryName&page_size=100")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
            assertNotNull(totalCount, "response must include total_count")
            assertTrue(
                actual = totalCount in 1 until 120,
                message = "total_count=$totalCount must be 1..<120 for category_name=$targetCategoryName",
            )
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response must include items array")
            assertTrue(
                actual = items.isNotEmpty(),
                message = "filtered items must be non-empty for category_name=$targetCategoryName",
            )
            items.forEach { item ->
                val id = item.jsonObject["id"]?.jsonPrimitive?.content
                assertNotNull(id, "item must expose id")
                val categoryNameValue = item.jsonObject["therapeutic_category_name"]?.jsonPrimitive?.content
                assertEquals(
                    expected = targetCategoryName,
                    actual = categoryNameValue,
                    message = "item id=$id has therapeutic_category_name=$categoryNameValue; " +
                        "must equal '$targetCategoryName' under category_name filter",
                )
            }
        }

    @Test
    fun `GET drugs with no category_name returns default pagination size items`() = testApplication {
        application { module() }

        val response = client.get("/drugs")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertEquals(
            expected = DrugListFixtures.DEFAULT_PAGE_SIZE,
            actual = items.size,
            message = "items.size must equal DEFAULT_PAGE_SIZE when no filter is applied",
        )
        assertEquals(
            expected = 120,
            actual = body["total_count"]?.jsonPrimitive?.content?.toInt(),
            message = "total_count must be 120 (全件) when no category_name filter is applied",
        )
    }

    @Test
    fun `GET drugs category_atc=A and category_name= returns intersection (items satisfy both)`() = testApplication {
        application { module() }

        val targetCategoryName = "消化器系および代謝"
        val response = client.get("/drugs?category_atc=A&category_name=$targetCategoryName&page_size=100")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
        assertNotNull(totalCount, "response must include total_count")
        assertTrue(
            actual = totalCount in 1 until 120,
            message = "total_count=$totalCount must be 1..<120 for category_atc=A & category_name=$targetCategoryName",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertEquals(
            expected = totalCount,
            actual = items.size,
            message = "page_size=100 must contain all filtered items (intersection)",
        )
        items.forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            assertNotNull(id, "item must expose id")
            val categoryNameValue = item.jsonObject["therapeutic_category_name"]?.jsonPrimitive?.content
            assertEquals(
                expected = targetCategoryName,
                actual = categoryNameValue,
                message = "item id=$id must satisfy category_name=$targetCategoryName under AND filter",
            )
        }
    }
}
