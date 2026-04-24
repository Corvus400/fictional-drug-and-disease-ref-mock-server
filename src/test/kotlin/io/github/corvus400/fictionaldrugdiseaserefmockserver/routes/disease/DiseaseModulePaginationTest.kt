package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

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

class DiseaseModulePaginationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with page 1 page_size 20 returns 20 items total_pages 4 total_count 80`() =
        testApplication {
            application { module() }

            val response = client.get("/diseases?page=1&page_size=20")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response body must have an items array")
            assertEquals(
                expected = 20,
                actual = items.size,
                message = "page=1&page_size=20 must expose items.size == 20",
            )
            val totalPages = body["total_pages"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 4,
                actual = totalPages,
                message = "total_pages must be 4 when total_count=80 and page_size=20",
            )
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 80,
                actual = totalCount,
                message = "total_count must be 80 for default disease scenario",
            )
        }

    @Test
    fun `GET diseases with page 4 page_size 20 returns last page with 20 items and page 4`() =
        testApplication {
            application { module() }

            val response = client.get("/diseases?page=4&page_size=20")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response body must have an items array")
            assertEquals(
                expected = 20,
                actual = items.size,
                message = "page=4&page_size=20 must still expose 20 items (last page exactly fills page_size)",
            )
            val page = body["page"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 4,
                actual = page,
                message = "page echo must reflect the requested page number",
            )
        }

    @Test
    fun `GET diseases without any query returns first page with 20 items and total_count 80`() =
        testApplication {
            application { module() }

            val response = client.get("/diseases")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response body must have an items array")
            assertEquals(
                expected = 20,
                actual = items.size,
                message = "no-query default must expose items.size == 20 (DEFAULT_PAGE_SIZE)",
            )
            val page = body["page"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 1,
                actual = page,
                message = "page must default to 1 when query is omitted",
            )
            val pageSize = body["page_size"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 20,
                actual = pageSize,
                message = "page_size must default to DEFAULT_PAGE_SIZE (20) when query is omitted",
            )
            val totalCount = body["total_count"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 80,
                actual = totalCount,
                message = "total_count must be 80 regardless of page_size",
            )
        }

    @Test
    fun `GET diseases page_size 1000 is clamped to MAX_PAGE_SIZE 100 and total_pages becomes 1`() =
        testApplication {
            application { module() }

            val response = client.get("/diseases?page_size=1000")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            val items = body["items"]?.jsonArray
            assertNotNull(items, "response body must have an items array")
            assertEquals(
                expected = 80,
                actual = items.size,
                message = "page_size=1000 is clamped to 100 so items must contain all 80 diseases in one page",
            )
            val pageSize = body["page_size"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 100,
                actual = pageSize,
                message = "page_size in envelope must reflect the clamped value 100 (MAX_PAGE_SIZE)",
            )
            val totalPages = body["total_pages"]?.jsonPrimitive?.content?.toIntOrNull()
            assertEquals(
                expected = 1,
                actual = totalPages,
                message = "total_pages must be 1 after clamping (ceil(80/100))",
            )
        }
}
