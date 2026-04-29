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

class DiseaseModuleSortTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases with sort name_kana returns items ordered by nameKana ascending`() = testApplication {
        application { module() }

        val response = client.get("/diseases?sort=name_kana&page_size=100")

        assertEquals(expected = HttpStatusCode.OK, actual = response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(actual = items, message = "response body must have an items array")
        val kanas = items.map { it.jsonObject["name_kana"]?.jsonPrimitive?.content.orEmpty() }
        assertEquals(
            expected = kanas.sorted(),
            actual = kanas,
            message = "items must be ordered by name_kana ascending when sort=name_kana",
        )
    }
}
