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
        assertEquals(
            expected = ResponseSnapshot(status = HttpStatusCode.OK, body = "{}"),
            actual = ResponseSnapshot(status = response.status, body = response.bodyAsText()),
        )
    }

    @Test
    fun `admin set config with delay and status`() = testApplication {
        application { module() }
        val setResponse = client.post("/__admin/configs/drugDetail") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"ServerError","delay_ms":2000,"status_code":500}""")
        }

        val getResponse = client.get("/__admin/configs")
        val body = getResponse.bodyAsText()
        assertEquals(
            expected = ConfigBodySnapshot(
                setStatus = HttpStatusCode.OK,
                containsState = true,
                containsDelayMs = true,
                containsStatusCode = true,
            ),
            actual = ConfigBodySnapshot(
                setStatus = setResponse.status,
                containsState = body.contains("ServerError"),
                containsDelayMs = body.contains("2000"),
                containsStatusCode = body.contains("500"),
            ),
        )
    }

    @Test
    fun `admin set config with redirect headers`() = testApplication {
        application { module() }
        val setResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"Redirect","status_code":302,"headers":{"Location":"/dashboard"}}""")
        }

        val getResponse = client.get("/__admin/configs")
        val body = getResponse.bodyAsText()
        assertEquals(
            expected = RedirectConfigSnapshot(
                setStatus = HttpStatusCode.OK,
                containsStatusCode = true,
                containsLocationHeader = true,
                containsLocationValue = true,
            ),
            actual = RedirectConfigSnapshot(
                setStatus = setResponse.status,
                containsStatusCode = body.contains("302"),
                containsLocationHeader = body.contains("Location"),
                containsLocationValue = body.contains("/dashboard"),
            ),
        )
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

        // 空になっていることを確認
        val getResponse = client.get("/__admin/configs")
        assertEquals(
            expected = ResponseSnapshot(status = HttpStatusCode.OK, body = "{}"),
            actual = ResponseSnapshot(status = resetResponse.status, body = getResponse.bodyAsText()),
        )
    }

    @Test
    fun `admin set drugList empty config is reflected on configs and reset clears it`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val setResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state":"empty"}""")
        }

        val afterSet = client.get("/__admin/configs").bodyAsText()
        val resetResponse = client.post("/__admin/reset")
        val afterReset = client.get("/__admin/configs").bodyAsText()
        assertEquals(
            expected = ConfigSetAndResetSnapshot(
                setStatus = HttpStatusCode.OK,
                afterSetContainsKey = true,
                afterSetContainsState = true,
                resetStatus = HttpStatusCode.OK,
                afterResetBody = "{}",
            ),
            actual = ConfigSetAndResetSnapshot(
                setStatus = setResponse.status,
                afterSetContainsKey = afterSet.contains("drugList"),
                afterSetContainsState = afterSet.contains("empty"),
                resetStatus = resetResponse.status,
                afterResetBody = afterReset,
            ),
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

    private data class ResponseSnapshot(
        val status: HttpStatusCode,
        val body: String,
    )

    private data class ConfigBodySnapshot(
        val setStatus: HttpStatusCode,
        val containsState: Boolean,
        val containsDelayMs: Boolean,
        val containsStatusCode: Boolean,
    )

    private data class RedirectConfigSnapshot(
        val setStatus: HttpStatusCode,
        val containsStatusCode: Boolean,
        val containsLocationHeader: Boolean,
        val containsLocationValue: Boolean,
    )

    private data class ConfigSetAndResetSnapshot(
        val setStatus: HttpStatusCode,
        val afterSetContainsKey: Boolean,
        val afterSetContainsState: Boolean,
        val resetStatus: HttpStatusCode,
        val afterResetBody: String,
    )
}
