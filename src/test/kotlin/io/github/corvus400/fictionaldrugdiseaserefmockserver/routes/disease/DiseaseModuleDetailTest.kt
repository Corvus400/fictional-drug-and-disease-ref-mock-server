package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `POST admin configs diseaseDetail delay_ms 500 delays GET diseases response`() = testApplication {
        application { module() }

        val configResponse = client.post(urlString = "/__admin/configs/diseaseDetail") {
            contentType(type = ContentType.Application.Json)
            setBody(body = """{"state":"default","delay_ms":500}""")
        }
        assertEquals(
            expected = HttpStatusCode.OK,
            actual = configResponse.status,
            message = "POST /__admin/configs/diseaseDetail must return 200 OK",
        )

        val startTime = System.currentTimeMillis()
        val response = client.get(urlString = "/diseases/disease_0001")
        val elapsedMs = System.currentTimeMillis() - startTime

        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status,
            message = "GET /diseases/disease_0001 must remain 200 OK when only delay_ms is overridden",
        )
        assertTrue(
            actual = elapsedMs >= 500,
            message = "Expected delay of at least 500ms (diseaseDetail delay_ms override), got ${elapsedMs}ms",
        )
    }

    @Test
    fun `POST admin configs diseaseDetail status_code 500 makes GET diseases return 500`() = testApplication {
        application { module() }

        val configResponse = client.post(urlString = "/__admin/configs/diseaseDetail") {
            contentType(type = ContentType.Application.Json)
            setBody(body = """{"state":"default","status_code":500}""")
        }
        assertEquals(
            expected = HttpStatusCode.OK,
            actual = configResponse.status,
            message = "POST /__admin/configs/diseaseDetail must return 200 OK",
        )

        val response = client.get(urlString = "/diseases/disease_0001")

        assertEquals(
            expected = HttpStatusCode.InternalServerError,
            actual = response.status,
            message = "GET /diseases/disease_0001 must honor diseaseDetail status_code=500 override",
        )
    }
}
