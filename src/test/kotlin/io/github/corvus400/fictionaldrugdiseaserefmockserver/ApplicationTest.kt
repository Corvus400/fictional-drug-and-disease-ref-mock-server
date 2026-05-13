package io.github.corvus400.fictionaldrugdiseaserefmockserver

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `health endpoint returns ok`() = testApplication {
        application { module() }
        val response = client.get("/health")
        assertEquals(
            expected = ResponseSnapshot(status = HttpStatusCode.OK, body = """{"status":"ok"}"""),
            actual = ResponseSnapshot(status = response.status, body = response.bodyAsText()),
        )
    }

    @Test
    fun `admin configs returns empty map initially`() = testApplication {
        application { module() }
        val response = client.get("/__admin/configs")
        assertEquals(
            expected = ResponseSnapshot(status = HttpStatusCode.OK, body = "{}"),
            actual = ResponseSnapshot(status = response.status, body = response.bodyAsText()),
        )
    }

    @Test
    fun `admin set config and get it`() = testApplication {
        application { module() }
        // Set config
        val setResponse = client.post("/__admin/configs/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"TestState"}""")
        }

        // Get configs
        val getResponse = client.get("/__admin/configs")
        val body = json.decodeFromString<JsonObject>(getResponse.bodyAsText())
        assertEquals(
            expected = ConfigSetSnapshot(setStatus = HttpStatusCode.OK, storedState = "TestState"),
            actual = ConfigSetSnapshot(
                setStatus = setResponse.status,
                storedState = body["test"]?.jsonObject?.get("state")?.jsonPrimitive?.content,
            ),
        )
    }

    @Test
    fun `module boots successfully with current fixtures passing CrossReferenceInitCheck`() =
        testApplication {
            application { module() }

            val response = client.get("/v1/drugs")
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = response.status,
                message = "module startup must keep /v1/drugs available with current fixtures",
            )
        }

    @Test
    fun `admin reset clears all configs`() = testApplication {
        application { module() }
        // Set config
        client.post("/__admin/configs/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"TestState"}""")
        }

        // Reset
        val resetResponse = client.post("/__admin/reset")

        // Verify empty
        val getResponse = client.get("/__admin/configs")
        assertEquals(
            expected = ResponseSnapshot(status = HttpStatusCode.OK, body = "{}"),
            actual = ResponseSnapshot(status = resetResponse.status, body = getResponse.bodyAsText()),
        )
    }

    private data class ResponseSnapshot(
        val status: HttpStatusCode,
        val body: String,
    )

    private data class ConfigSetSnapshot(
        val setStatus: HttpStatusCode,
        val storedState: String?,
    )
}
