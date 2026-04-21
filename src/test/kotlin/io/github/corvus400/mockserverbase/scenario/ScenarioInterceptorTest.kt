package io.github.corvus400.mockserverbase.scenario

import io.github.corvus400.mockserverbase.model.sample.SampleResponse
import io.github.corvus400.mockserverbase.module
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
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
