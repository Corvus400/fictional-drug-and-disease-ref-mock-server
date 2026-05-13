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

class DiseaseModuleDetailTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET v1 diseases disease_0001 returns 200 with matching Disease id`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases/disease_0001")

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = DetailIdentitySnapshot(status = HttpStatusCode.OK, id = "disease_0001"),
            actual = DetailIdentitySnapshot(
                status = response.status,
                id = body["id"]?.jsonPrimitive?.content,
            ),
        )
    }

    @Test
    fun `GET diseases disease_0001 returns 200 with exactly 25 snake_case fields`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases/disease_0001")

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = DetailShapeSnapshot(status = HttpStatusCode.OK, id = "disease_0001", fieldCount = 25),
            actual = DetailShapeSnapshot(
                status = response.status,
                id = body["id"]?.jsonPrimitive?.content,
                fieldCount = body.size,
            ),
        )
    }

    @Test
    fun `POST admin configs diseaseDetail delay_ms 500 delays GET diseases response`() = testApplication {
        application { module() }

        val configResponse = client.post(urlString = "/__admin/configs/diseaseDetail") {
            contentType(type = ContentType.Application.Json)
            setBody(body = """{"state":"default","delay_ms":500}""")
        }

        val startTime = System.currentTimeMillis()
        val response = client.get(urlString = "/v1/diseases/disease_0001")
        val elapsedMs = System.currentTimeMillis() - startTime

        assertEquals(
            expected = DetailDelaySnapshot(
                configStatus = HttpStatusCode.OK,
                responseStatus = HttpStatusCode.OK,
                delayed = true,
            ),
            actual = DetailDelaySnapshot(
                configStatus = configResponse.status,
                responseStatus = response.status,
                delayed = elapsedMs >= 500,
            ),
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

        val response = client.get(urlString = "/v1/diseases/disease_0001")

        assertEquals(
            expected = StatusOverrideSnapshot(
                configStatus = HttpStatusCode.OK,
                responseStatus = HttpStatusCode.InternalServerError,
            ),
            actual = StatusOverrideSnapshot(
                configStatus = configResponse.status,
                responseStatus = response.status,
            ),
            message = "GET /v1/diseases/disease_0001 must honor diseaseDetail status_code=500 override",
        )
    }

    @Test
    fun `POST admin configs diseaseDetail status_code 404 override swaps body to ErrorResponse NOT_FOUND`() =
        testApplication {
            application { module() }

            val configResponse = client.post(urlString = "/__admin/configs/diseaseDetail") {
                contentType(type = ContentType.Application.Json)
                setBody(body = """{"state":"default","status_code":404}""")
            }

            val response = client.get(urlString = "/v1/diseases/disease_0001")

            val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
            assertEquals(
                expected = ErrorResponseSnapshot(
                    configStatus = HttpStatusCode.OK,
                    status = HttpStatusCode.NotFound,
                    code = "NOT_FOUND",
                    message = "Disease not found: disease_0001",
                ),
                actual = ErrorResponseSnapshot(
                    configStatus = configResponse.status,
                    status = response.status,
                    code = body["code"]?.jsonPrimitive?.content,
                    message = body["message"]?.jsonPrimitive?.content,
                ),
            )
        }

    @Test
    fun `GET diseases unknown id returns 404 with ErrorResponse NOT_FOUND body`() = testApplication {
        application { module() }

        val response = client.get(urlString = "/v1/diseases/disease_9999")

        val body = json.parseToJsonElement(string = response.bodyAsText()).jsonObject
        assertEquals(
            expected = ErrorResponseSnapshot(
                status = HttpStatusCode.NotFound,
                code = "NOT_FOUND",
                message = "Disease not found: disease_9999",
            ),
            actual = ErrorResponseSnapshot(
                status = response.status,
                code = body["code"]?.jsonPrimitive?.content,
                message = body["message"]?.jsonPrimitive?.content,
            ),
        )
    }

    private data class DetailIdentitySnapshot(
        val status: HttpStatusCode,
        val id: String?,
    )

    private data class DetailShapeSnapshot(
        val status: HttpStatusCode,
        val id: String?,
        val fieldCount: Int,
    )

    private data class ErrorResponseSnapshot(
        val configStatus: HttpStatusCode? = null,
        val status: HttpStatusCode,
        val code: String?,
        val message: String?,
    )

    private data class DetailDelaySnapshot(
        val configStatus: HttpStatusCode,
        val responseStatus: HttpStatusCode,
        val delayed: Boolean,
    )

    private data class StatusOverrideSnapshot(
        val configStatus: HttpStatusCode,
        val responseStatus: HttpStatusCode,
    )
}
