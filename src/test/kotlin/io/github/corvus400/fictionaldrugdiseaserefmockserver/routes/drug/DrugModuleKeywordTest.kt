package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DrugModuleKeywordTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs with keyword target brand and match partial returns filtered count less than total`() =
        testApplication {
            application { module() }

            val totalResponse = client.get("/drugs?page_size=100")
            val filteredResponse = client.get(
                "/drugs?keyword=%E3%82%B9%E3%83%BC%E3%83%91%E3%83%BC" +
                    "&keyword_target=brand&keyword_match=partial&page_size=100",
            )

            assertEquals(HttpStatusCode.OK, totalResponse.status)
            assertEquals(HttpStatusCode.OK, filteredResponse.status)
            val total = totalResponse.totalCount()
            val filtered = filteredResponse.totalCount()
            assertTrue(filtered < total, "filtered=$filtered total=$total")
            assertTrue(filtered >= 0, "filtered=$filtered must be >= 0")
        }

    private suspend fun HttpResponse.totalCount(): Int {
        val body = json.parseToJsonElement(string = bodyAsText()).jsonObject
        val totalCount = body["total_count"]?.jsonPrimitive?.content?.toInt()
        assertNotNull(totalCount, "response must include total_count")
        return totalCount
    }
}
