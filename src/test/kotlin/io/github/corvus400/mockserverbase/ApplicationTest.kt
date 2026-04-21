package io.github.corvus400.mockserverbase

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
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("""{"status":"ok"}""", response.bodyAsText())
    }

    @Test
    fun `admin configs returns empty map initially`() = testApplication {
        application { module() }
        val response = client.get("/__admin/configs")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{}", response.bodyAsText())
    }

    @Test
    fun `admin set config and get it`() = testApplication {
        application { module() }
        // Set config
        val setResponse = client.post("/__admin/configs/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"TestState"}""")
        }
        assertEquals(HttpStatusCode.OK, setResponse.status)

        // Get configs
        val getResponse = client.get("/__admin/configs")
        val body = json.decodeFromString<JsonObject>(getResponse.bodyAsText())
        assertEquals("TestState", body["test"]?.jsonObject?.get("state")?.jsonPrimitive?.content)
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
        assertEquals(HttpStatusCode.OK, resetResponse.status)

        // Verify empty
        val getResponse = client.get("/__admin/configs")
        assertEquals("{}", getResponse.bodyAsText())
    }
}
