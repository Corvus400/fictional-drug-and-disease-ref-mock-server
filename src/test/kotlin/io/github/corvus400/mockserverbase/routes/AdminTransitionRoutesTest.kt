package io.github.corvus400.mockserverbase.routes

import io.github.corvus400.mockserverbase.module
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
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdminTransitionRoutesTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `POST transitions sets chain for endpoint`() = testApplication {
        application { module() }

        val response = client.post("/__admin/transitions/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"scenarios": ["default", "empty"]}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertTrue(body["success"]?.jsonPrimitive?.boolean == true)
    }

    @Test
    fun `GET transitions returns all chains`() = testApplication {
        application { module() }

        // チェーンを設定
        client.post("/__admin/transitions/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"scenarios": ["first", "second"]}""")
        }

        val response = client.get("/__admin/transitions")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val sampleChain = body["sample"]?.jsonObject
        assertEquals(0, sampleChain?.get("currentIndex")?.jsonPrimitive?.int)
        assertEquals("first", sampleChain?.get("scenarios")?.jsonArray?.get(0)?.jsonPrimitive?.content)
        assertEquals("second", sampleChain?.get("scenarios")?.jsonArray?.get(1)?.jsonPrimitive?.content)
    }

    @Test
    fun `POST transitions with empty scenarios returns bad request`() = testApplication {
        application { module() }

        val response = client.post("/__admin/transitions/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"scenarios": []}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST batch transitions sets multiple chains`() = testApplication {
        application { module() }

        val response = client.post("/__admin/transitions") {
            contentType(ContentType.Application.Json)
            setBody(
                """{"transitions": {"sample": ["default", "empty"], "other": ["a", "b"]}}""",
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        // 両方のチェーンが設定されていることを確認
        val getResponse = client.get("/__admin/transitions")
        val body = json.decodeFromString<JsonObject>(getResponse.bodyAsText())
        assertTrue(body.containsKey("sample"))
        assertTrue(body.containsKey("other"))
    }

    @Test
    fun `reset clears transition chains`() = testApplication {
        application { module() }

        // チェーンを設定
        client.post("/__admin/transitions/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"scenarios": ["first", "second"]}""")
        }

        // リセット
        client.post("/__admin/reset")

        // チェーンが空になっていることを確認
        val response = client.get("/__admin/transitions")
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertTrue(body.isEmpty())
    }

    @Test
    fun `transition chain sets initial override to first scenario`() = testApplication {
        application { module() }

        // チェーンを設定
        client.post("/__admin/transitions/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"scenarios": ["first_scenario", "second_scenario"]}""")
        }

        // configs API で確認すると、初期シナリオが設定されている
        val response = client.get("/__admin/configs")
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals("first_scenario", body["sample"]?.jsonObject?.get("state")?.jsonPrimitive?.content)
    }
}
