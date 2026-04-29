package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes

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
import kotlinx.serialization.json.JsonObject
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
    fun `POST transitions with empty scenarios returns bad request`() = testApplication {
        application { module() }

        val response = client.post("/__admin/transitions/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"scenarios": []}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST batch transitions sets multiple chains for drugList and diseaseList`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val response = client.post("/__admin/transitions") {
            contentType(ContentType.Application.Json)
            setBody(
                """{"transitions": {"drugList": ["default", "empty"], "diseaseList": ["default", "empty"]}}""",
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val getResponse = client.get("/__admin/transitions")
        val body = json.decodeFromString<JsonObject>(getResponse.bodyAsText())
        assertTrue(body.containsKey("drugList"))
        assertTrue(body.containsKey("diseaseList"))

        client.post("/__admin/reset")
    }

    @Test
    fun `reset clears transition chains`() = testApplication {
        application { module() }

        client.post("/__admin/transitions/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"scenarios": ["default", "empty"]}""")
        }

        client.post("/__admin/reset")

        val response = client.get("/__admin/transitions")
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertTrue(body.isEmpty())
    }

    @Test
    fun `POST transitions drugList records chain and initial override on configs`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val transitionResponse = client.post("/__admin/transitions/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"scenarios": ["default", "empty"]}""")
        }
        assertEquals(HttpStatusCode.OK, transitionResponse.status)

        val transitionsResponse = client.get("/__admin/transitions")
        val transitionsBody = json.decodeFromString<JsonObject>(transitionsResponse.bodyAsText())
        val drugListChain = transitionsBody["drugList"]?.jsonObject
        assertEquals(
            expected = 0,
            actual = drugListChain?.get("current_index")?.jsonPrimitive?.int,
            message = "drugList の遷移チェーン初期 current_index は 0 である必要がある",
        )
        assertEquals(
            expected = "default",
            actual = drugListChain?.get("scenarios")?.jsonArray?.get(0)?.jsonPrimitive?.content,
            message = "drugList の遷移チェーン scenarios[0] は default",
        )
        assertEquals(
            expected = "empty",
            actual = drugListChain?.get("scenarios")?.jsonArray?.get(1)?.jsonPrimitive?.content,
            message = "drugList の遷移チェーン scenarios[1] は empty",
        )

        val configsResponse = client.get("/__admin/configs")
        val configsBody = json.decodeFromString<JsonObject>(configsResponse.bodyAsText())
        assertEquals(
            expected = "default",
            actual = configsBody["drugList"]?.jsonObject?.get("state")?.jsonPrimitive?.content,
            message = "drugList の transition 設定直後は configs に default が反映されている",
        )

        client.post("/__admin/reset")
    }

    @Test
    fun `POST transitions diseaseList records chain in transitions endpoint`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        client.post("/__admin/transitions/diseaseList") {
            contentType(ContentType.Application.Json)
            setBody("""{"scenarios": ["default", "empty"]}""")
        }

        val transitionsResponse = client.get("/__admin/transitions")
        val body = json.decodeFromString<JsonObject>(transitionsResponse.bodyAsText())
        assertTrue(
            actual = body.containsKey("diseaseList"),
            message = "/__admin/transitions に diseaseList のチェーンが含まれている必要がある",
        )

        client.post("/__admin/reset")
    }
}
