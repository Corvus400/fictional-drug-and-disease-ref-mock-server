package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DiseaseModuleTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases disease_0001 returns 200 OK with populated fixmerge name fields`() = testApplication {
        application { module() }

        val response = client.get("/diseases/disease_0001")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val name = body["name"]?.jsonPrimitive?.content
        val nameKana = body["name_kana"]?.jsonPrimitive?.content
        val nameEnglish = body["name_english"]?.jsonPrimitive?.content
        assertNotNull(name)
        assertTrue(name.isNotBlank(), "name must be non-blank")
        assertNotNull(nameKana)
        assertTrue(nameKana.isNotBlank(), "nameKana must be non-blank")
        assertNotNull(nameEnglish)
        assertTrue(nameEnglish.isNotBlank(), "nameEnglish must be non-blank")
    }

    @Test
    fun `GET diseases returns 200 OK with disease array`() = testApplication {
        application { module() }

        val response = client.get("/diseases")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("disease_0001"))
    }
}
