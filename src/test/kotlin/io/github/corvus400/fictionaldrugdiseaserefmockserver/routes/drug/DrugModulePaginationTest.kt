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

class DrugModulePaginationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs page=1 page_size=20 returns items_size=20 total_pages=6 total_count=120`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?page=1&page_size=20")

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = PaginationSnapshot(
                status = HttpStatusCode.OK,
                itemsSize = 20,
                page = 1,
                pageSize = 20,
                totalPages = 6,
                totalCount = 120,
            ),
            actual = body.paginationSnapshot(status = response.status),
        )
    }

    @Test
    fun `GET drugs page=2 page_size=50 returns items_size=50 page=2 total_pages=3`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?page=2&page_size=50")

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = PaginationSnapshot(
                status = HttpStatusCode.OK,
                itemsSize = 50,
                page = 2,
                pageSize = 50,
                totalPages = 3,
                totalCount = 120,
            ),
            actual = body.paginationSnapshot(status = response.status),
        )
    }

    @Test
    fun `GET drugs page_size=1000 is clamped to 100 items and total_pages=2`() = testApplication {
        application { module() }

        val response = client.get("/v1/drugs?page_size=1000")

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = PaginationSnapshot(
                status = HttpStatusCode.OK,
                itemsSize = 100,
                page = 1,
                pageSize = 100,
                totalPages = 2,
                totalCount = 120,
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
