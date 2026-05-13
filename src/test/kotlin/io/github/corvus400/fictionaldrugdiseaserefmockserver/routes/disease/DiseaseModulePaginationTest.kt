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

class DiseaseModulePaginationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with page 1 page_size 20 returns 20 items total_pages 4 total_count 80`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/diseases?page=1&page_size=20")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            assertEquals(
                expected = PaginationSnapshot(
                    status = HttpStatusCode.OK,
                    itemsSize = 20,
                    page = 1,
                    pageSize = 20,
                    totalPages = 4,
                    totalCount = 80,
                ),
                actual = body.paginationSnapshot(status = response.status),
            )
        }

    @Test
    fun `GET diseases with page 4 page_size 20 returns last page with 20 items and page 4`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/diseases?page=4&page_size=20")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            assertEquals(
                expected = PaginationSnapshot(
                    status = HttpStatusCode.OK,
                    itemsSize = 20,
                    page = 4,
                    pageSize = 20,
                    totalPages = 4,
                    totalCount = 80,
                ),
                actual = body.paginationSnapshot(status = response.status),
            )
        }

    @Test
    fun `GET diseases without any query returns first page with 20 items and total_count 80`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/diseases")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            assertEquals(
                expected = PaginationSnapshot(
                    status = HttpStatusCode.OK,
                    itemsSize = 20,
                    page = 1,
                    pageSize = 20,
                    totalPages = 4,
                    totalCount = 80,
                ),
                actual = body.paginationSnapshot(status = response.status),
            )
        }

    @Test
    fun `GET diseases page_size 1000 is clamped to MAX_PAGE_SIZE 100 and total_pages becomes 1`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/diseases?page_size=1000")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            assertEquals(
                expected = PaginationSnapshot(
                    status = HttpStatusCode.OK,
                    itemsSize = 80,
                    page = 1,
                    pageSize = 100,
                    totalPages = 1,
                    totalCount = 80,
                ),
                actual = body.paginationSnapshot(status = response.status),
            )
        }

    private data class PaginationSnapshot(
        val status: HttpStatusCode,
        val itemsSize: Int?,
        val page: Int?,
        val pageSize: Int?,
        val totalPages: Int?,
        val totalCount: Int?,
    )

    private fun kotlinx.serialization.json.JsonObject.paginationSnapshot(status: HttpStatusCode): PaginationSnapshot =
        PaginationSnapshot(
            status = status,
            itemsSize = this["items"]?.jsonArray?.size,
            page = this["page"]?.jsonPrimitive?.content?.toIntOrNull(),
            pageSize = this["page_size"]?.jsonPrimitive?.content?.toIntOrNull(),
            totalPages = this["total_pages"]?.jsonPrimitive?.content?.toIntOrNull(),
            totalCount = this["total_count"]?.jsonPrimitive?.content?.toIntOrNull(),
        )
}
