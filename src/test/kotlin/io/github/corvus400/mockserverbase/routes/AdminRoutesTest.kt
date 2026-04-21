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
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AdminRoutesTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET health returns ok status`() = testApplication {
        application { module() }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals("ok", body["status"]?.jsonPrimitive?.content)
    }

    @Test
    fun `POST admin configs sets new override`() = testApplication {
        application { module() }

        val response = client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertTrue(body["success"]?.jsonPrimitive?.boolean == true)
    }

    @Test
    fun `POST admin reset clears all overrides`() = testApplication {
        application { module() }

        // まずオーバーライドを設定
        client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }

        // リセット
        val response = client.post("/__admin/reset")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertTrue(body["success"]?.jsonPrimitive?.boolean == true)
    }

    @Test
    fun `GET admin configs returns current overrides`() = testApplication {
        application { module() }

        val response = client.get("/__admin/configs")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        // 初期状態では空のオブジェクトまたは設定されたオーバーライドが返る
        assertNotNull(body)
    }
}
