package io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.sample.SampleResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ScenarioInterceptorTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `X-Mock-Scenario header overrides Admin API scenario state`() = testApplication {
        application { module() }

        // Admin APIでシナリオを default に設定
        val adminResponse = client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "default"}""")
        }
        assertEquals(HttpStatusCode.OK, adminResponse.status)

        // X-Mock-Scenarioヘッダーで empty を指定
        val response = client.get("/api/sample") {
            header("X-Mock-Scenario", "empty")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<SampleResponse>(response.bodyAsText())
        // ヘッダーの値（empty）が優先される
        assertEquals("", body.id)
        assertEquals("", body.message)
    }

    @Test
    fun `Request without X-Mock-Scenario uses Admin API scenario state`() = testApplication {
        application { module() }

        // Admin APIでシナリオを empty に設定
        val adminResponse = client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }
        assertEquals(HttpStatusCode.OK, adminResponse.status)

        // ヘッダーなしでリクエスト
        val response = client.get("/api/sample")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<SampleResponse>(response.bodyAsText())
        // Admin APIで設定した値（empty）が使用される
        assertEquals("", body.id)
    }

    @Test
    fun `Request without any scenario setting uses default`() = testApplication {
        application { module() }

        // 何も設定せずにリクエスト
        val response = client.get("/api/sample")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<SampleResponse>(response.bodyAsText())
        // デフォルト値が使用される
        assertEquals("sample-1", body.id)
    }

    @Test
    fun `Admin API reset clears scenario state`() = testApplication {
        application { module() }

        // Admin APIでシナリオを設定
        client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }

        // リセット
        val resetResponse = client.post("/__admin/reset")
        assertEquals(HttpStatusCode.OK, resetResponse.status)

        // リセット後はデフォルトに戻る
        val response = client.get("/api/sample")

        val body = json.decodeFromString<SampleResponse>(response.bodyAsText())
        assertEquals("sample-1", body.id)
    }

    @Test
    fun `X-Mock-Scenario header on drugs overrides Admin API drugList state`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val adminResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "default"}""")
        }
        assertEquals(HttpStatusCode.OK, adminResponse.status)

        val response = client.get("/drugs") {
            header("X-Mock-Scenario", "empty")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val items = body["items"]?.jsonArray
        assertNotNull(items)
        assertEquals(
            expected = 0,
            actual = items.size,
            message = "X-Mock-Scenario=empty が Admin override(default) より優先され items は 0 件",
        )

        client.post("/__admin/reset")
    }

    @Test
    fun `Drugs request without header uses Admin API drugList state`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val adminResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }
        assertEquals(HttpStatusCode.OK, adminResponse.status)

        val response = client.get("/drugs")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals(
            expected = 0,
            actual = body["total_count"]?.jsonPrimitive?.int,
            message = "ヘッダなしリクエストは Admin override(empty) を採用し total_count=0",
        )

        client.post("/__admin/reset")
    }

    @Test
    fun `Drugs request with no scenario setting falls back to default 120`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val response = client.get("/drugs")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals(
            expected = 120,
            actual = body["total_count"]?.jsonPrimitive?.int,
            message = "ヘッダ・Admin override 無しの状態は default シナリオ(total_count=120) にフォールバック",
        )
    }
}
