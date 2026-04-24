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

class DrugModulePaginationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs page=1 page_size=20 returns items_size=20 total_pages=6 total_count=120`() = testApplication {
        application { module() }

        val response = client.get("/drugs?page=1&page_size=20")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response must include items array")
        assertEquals(expected = 20, actual = items.size, message = "items.size must be 20 for page_size=20")
        assertEquals(
            expected = 6,
            actual = body["total_pages"]?.jsonPrimitive?.content?.toInt(),
            message = "total_pages must be 6 (ceil(120/20))",
        )
        assertEquals(
            expected = 120,
            actual = body["total_count"]?.jsonPrimitive?.content?.toInt(),
            message = "total_count must be 120 (全 fixture 件数)",
        )
    }
}
