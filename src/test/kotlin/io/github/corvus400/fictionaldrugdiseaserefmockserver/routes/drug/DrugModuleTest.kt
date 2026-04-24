package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DrugModuleTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET drugs drug_0001 returns 200 OK with populated fixmerge name fields`() = testApplication {
        application { module() }

        val response = client.get("/drugs/drug_0001")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val brandName = body["brand_name"]?.jsonPrimitive?.content
        val genericName = body["generic_name"]?.jsonPrimitive?.content
        val manufacturer = body["manufacturer"]?.jsonPrimitive?.content
        assertNotNull(brandName)
        assertTrue(brandName.isNotBlank(), "brandName must be non-blank")
        assertNotNull(genericName)
        assertTrue(genericName.isNotBlank(), "genericName must be non-blank")
        assertNotNull(manufacturer)
        assertTrue(manufacturer.isNotBlank(), "manufacturer must be non-blank")
    }

    @Test
    fun `GET drugs returns 200 OK with drug array`() = testApplication {
        application { module() }

        val response = client.get("/drugs")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("drug_0001"))
    }

    @Test
    fun `GET drugs default scenario returns envelope with 120 items`() = testApplication {
        application { module() }

        val response = client.get("/drugs")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        val items = body["items"]?.jsonArray
        assertNotNull(items, "response body must have an items array")
        assertEquals(
            expected = 120,
            actual = items.size,
            message = "default scenario must expose envelope with items.size == 120",
        )
    }
}
