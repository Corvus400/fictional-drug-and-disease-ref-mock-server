package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseModuleDetailTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET diseases disease_0001 returns 200 with exactly 24 snake_case fields`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/diseases/disease_0001")

        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status,
            message = "GET /diseases/disease_0001 must return 200 OK",
        )
        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = "disease_0001",
            actual = body["id"]?.jsonPrimitive?.content,
            message = "GET /diseases/disease_0001 body[id] must equal disease_0001",
        )
        assertEquals(
            expected = 24,
            actual = body.size,
            message = "Disease detail envelope must expose exactly 24 snake_case fields, got keys=${body.keys}",
        )
    }
}
