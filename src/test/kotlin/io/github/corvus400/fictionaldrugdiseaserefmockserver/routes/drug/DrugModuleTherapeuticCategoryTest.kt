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

class DrugModuleTherapeuticCategoryTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs therapeutic_category=ALIMENTARY_METABOLISM returns only alimentary metabolism drugs`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs?therapeutic_category=ALIMENTARY_METABOLISM&page_size=100")

            assertEquals(
                HttpStatusCode.OK,
                response.status,
                "contract assertion failed"
            )
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
            assertNotNull(totalCount, "response must include total_count")
            assertTrue(
                actual = totalCount in 1 until 120,
                message = "total_count=$totalCount must be 1..<120 for therapeutic_category=ALIMENTARY_METABOLISM",
            )
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response must include items array")
            assertEquals(
                expected = totalCount,
                actual = items.size,
                message = "page_size=100 must contain all filtered items",
            )
            items.forEach { item ->
                val id = item.jsonObject["id"]?.jsonPrimitive?.content
                assertNotNull(id, "item must expose id")
                val categoryNameValue = item.jsonObject["therapeutic_category_name"]?.jsonPrimitive?.content
                assertEquals(
                    expected = "消化器系および代謝",
                    actual = categoryNameValue,
                    message = "item id=$id must match therapeutic_category=ALIMENTARY_METABOLISM",
                )
            }
        }

    @Test
    fun `GET drugs therapeutic_category=NERVOUS_SYSTEM returns only nervous system drugs`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?therapeutic_category=NERVOUS_SYSTEM&page_size=100")

        assertEquals(
            HttpStatusCode.OK,
            response.status,
            "contract assertion failed"
        )
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
        assertNotNull(totalCount, "response must include total_count")
        assertTrue(
            actual = totalCount in 1 until 120,
            message = "total_count=$totalCount must be 1..<120 for therapeutic_category=NERVOUS_SYSTEM",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertEquals(
            expected = totalCount,
            actual = items.size,
            message = "page_size=100 must contain all filtered items",
        )
        items.forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            assertNotNull(id, "item must expose id")
            val categoryNameValue = item.jsonObject["therapeutic_category_name"]?.jsonPrimitive?.content
            assertEquals(
                expected = "神経系",
                actual = categoryNameValue,
                message = "item id=$id must match therapeutic_category=NERVOUS_SYSTEM",
            )
        }
    }

    @Test
    fun `GET drugs therapeutic_category=UNKNOWN_CATEGORY returns HTTP 400 and INVALID_THERAPEUTIC_CATEGORY`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs?therapeutic_category=UNKNOWN_CATEGORY")

            assertEquals(
                expected = HttpStatusCode.BadRequest,
                actual = response.status,
                message = "invalid therapeutic_category must return 400",
            )
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            assertEquals(
                expected = "INVALID_THERAPEUTIC_CATEGORY",
                actual = body["code"]?.jsonPrimitive?.content,
                message = "invalid therapeutic_category response must expose INVALID_THERAPEUTIC_CATEGORY",
            )
            val message = body["message"]?.jsonPrimitive?.content
            assertNotNull(message, "ErrorResponse must include message")
            assertTrue(
                actual = message.contains(other = "UNKNOWN_CATEGORY"),
                message = "ErrorResponse message=$message must mention rejected value UNKNOWN_CATEGORY",
            )
        }

    @Test
    fun `GET drugs therapeutic_category and category_atc returns intersection when both match`() = testApplication {
        application { module() }

        val response = client.get(
            "/v1/drugs?therapeutic_category=ALIMENTARY_METABOLISM&category_atc=A&page_size=100",
        )

        assertEquals(
            HttpStatusCode.OK,
            response.status,
            "contract assertion failed"
        )
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
        assertNotNull(totalCount, "response must include total_count")
        assertTrue(
            actual = totalCount in 1 until 120,
            message = "total_count=$totalCount must be 1..<120 for matching therapeutic_category and category_atc",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertEquals(
            expected = totalCount,
            actual = items.size,
            message = "page_size=100 must contain all filtered items",
        )
        items.forEach { item ->
            val id = item.jsonObject["id"]?.jsonPrimitive?.content
            assertNotNull(id, "item must expose id")
            val categoryNameValue = item.jsonObject["therapeutic_category_name"]?.jsonPrimitive?.content
            assertEquals(
                expected = "消化器系および代謝",
                actual = categoryNameValue,
                message = "item id=$id must satisfy therapeutic_category=ALIMENTARY_METABOLISM",
            )
        }
    }

    @Test
    fun `GET drugs therapeutic_category=A and category_atc=N returns empty intersection`() = testApplication {
        application { module() }

        val response = client.get(
            "/v1/drugs?therapeutic_category=ALIMENTARY_METABOLISM&category_atc=N&page_size=100",
        )

        assertEquals(
            HttpStatusCode.OK,
            response.status,
            "contract assertion failed"
        )
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = 0,
            actual = body["total_count"]?.jsonPrimitive?.content?.toInt(),
            message = "conflicting therapeutic_category=ALIMENTARY_METABOLISM and category_atc=N must be empty",
        )
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertEquals(
            expected = 0,
            actual = items.size,
            message = "items must be empty when therapeutic_category and category_atc conflict",
        )
    }

    @Test
    fun `GET drugs category_name is silently ignored and returns unfiltered total_count`() = testApplication {
        application { module() }

        val deprecatedResponse = client.get("/v1/drugs?category_name=消化器系および代謝&page=1")
        val unfilteredResponse = client.get("/v1/drugs?page=1")

        assertEquals(
            HttpStatusCode.OK,
            deprecatedResponse.status,
            "contract assertion failed"
        )
        assertEquals(
            HttpStatusCode.OK,
            unfilteredResponse.status,
            "contract assertion failed"
        )
        val deprecatedBody = json.parseToJsonElement(string = deprecatedResponse.bodyAsText()).jsonObject
        val unfilteredBody = json.parseToJsonElement(string = unfilteredResponse.bodyAsText()).jsonObject
        assertEquals(
            expected = unfilteredBody["total_count"]?.jsonPrimitive?.content?.toInt(),
            actual = deprecatedBody["total_count"]?.jsonPrimitive?.content?.toInt(),
            message = "category_name must no longer filter /v1/drugs; Ktor should silently ignore it",
        )
    }
}
