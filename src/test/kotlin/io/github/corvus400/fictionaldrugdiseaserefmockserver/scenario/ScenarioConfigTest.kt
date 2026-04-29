package io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScenarioConfigTest {

    @Test
    fun `admin configs returns empty map initially`() = testApplication {
        application { module() }
        val response = client.get("/__admin/configs")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{}", response.bodyAsText())
    }

    @Test
    fun `admin set config with delay and status`() = testApplication {
        application { module() }
        val setResponse = client.post("/__admin/configs/sample-detail") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"ServerError","delay_ms":2000,"status_code":500}""")
        }
        assertEquals(HttpStatusCode.OK, setResponse.status)

        val getResponse = client.get("/__admin/configs")
        val body = getResponse.bodyAsText()
        assertTrue(body.contains("ServerError"))
        assertTrue(body.contains("2000"))
        assertTrue(body.contains("500"))
    }

    @Test
    fun `admin set config with redirect headers`() = testApplication {
        application { module() }
        val setResponse = client.post("/__admin/configs/sample") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"Redirect","status_code":302,"headers":{"Location":"/dashboard"}}""")
        }
        assertEquals(HttpStatusCode.OK, setResponse.status)

        val getResponse = client.get("/__admin/configs")
        val body = getResponse.bodyAsText()
        assertTrue(body.contains("302"))
        assertTrue(body.contains("Location"))
        assertTrue(body.contains("/dashboard"))
    }

    @Test
    fun `admin reset clears all configs`() = testApplication {
        application { module() }
        // 設定を追加
        client.post("/__admin/configs/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"Test"}""")
        }

        // リセット
        val resetResponse = client.post("/__admin/reset")
        assertEquals(HttpStatusCode.OK, resetResponse.status)

        // 空になっていることを確認
        val getResponse = client.get("/__admin/configs")
        assertEquals("{}", getResponse.bodyAsText())
    }

    @Test
    fun `admin set drugList empty config is reflected on configs and reset clears it`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val setResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"empty"}""")
        }
        assertEquals(HttpStatusCode.OK, setResponse.status)

        val afterSet = client.get("/__admin/configs").bodyAsText()
        assertTrue(
            actual = afterSet.contains("drugList") && afterSet.contains("empty"),
            message = "POST 後の /__admin/configs に drugList=empty が含まれている必要がある: $afterSet",
        )

        val resetResponse = client.post("/__admin/reset")
        assertEquals(HttpStatusCode.OK, resetResponse.status)

        val afterReset = client.get("/__admin/configs").bodyAsText()
        assertEquals(
            expected = "{}",
            actual = afterReset,
            message = "reset 後の /__admin/configs は空オブジェクトに戻る必要がある",
        )
    }

    @Test
    fun `admin set diseaseList empty config is reflected on configs body`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        client.post("/__admin/configs/diseaseList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"empty"}""")
        }

        val body = client.get("/__admin/configs").bodyAsText()
        assertTrue(
            actual = body.contains("diseaseList") && body.contains("empty"),
            message = "/__admin/configs body に diseaseList=empty が反映されている必要がある: $body",
        )

        client.post("/__admin/reset")
    }
}
