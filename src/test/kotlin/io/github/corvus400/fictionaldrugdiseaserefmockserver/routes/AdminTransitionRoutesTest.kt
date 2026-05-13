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

        assertEquals(
            expected = HttpStatusCode.BadRequest,
            actual = response.status,
            message = "empty transition chain must be rejected with 400",
        )
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

        val getResponse = client.get("/__admin/transitions")
        val body = json.decodeFromString<JsonObject>(getResponse.bodyAsText())
        assertEquals(
            expected = BatchTransitionSnapshot(
                postStatus = HttpStatusCode.OK,
                containsDrugList = true,
                containsDiseaseList = true,
            ),
            actual = BatchTransitionSnapshot(
                postStatus = response.status,
                containsDrugList = body.containsKey("drugList"),
                containsDiseaseList = body.containsKey("diseaseList"),
            ),
        )

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

        val transitionsResponse = client.get("/__admin/transitions")
        val transitionsBody = json.decodeFromString<JsonObject>(transitionsResponse.bodyAsText())
        val drugListChain = transitionsBody["drugList"]?.jsonObject
        assertEquals(
            expected = DrugListTransitionSnapshot(
                postStatus = HttpStatusCode.OK,
                currentIndex = 0,
                scenarios = listOf("default", "empty"),
                configState = "default",
            ),
            actual = DrugListTransitionSnapshot(
                postStatus = transitionResponse.status,
                currentIndex = drugListChain?.get("current_index")?.jsonPrimitive?.int,
                scenarios = drugListChain?.get("scenarios")?.jsonArray?.map { it.jsonPrimitive.content },
                configState = client.get("/__admin/configs")
                    .let { json.decodeFromString<JsonObject>(it.bodyAsText()) }
                    ["drugList"]?.jsonObject?.get("state")?.jsonPrimitive?.content,
            ),
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

    private data class BatchTransitionSnapshot(
        val postStatus: HttpStatusCode,
        val containsDrugList: Boolean,
        val containsDiseaseList: Boolean,
    )

    private data class DrugListTransitionSnapshot(
        val postStatus: HttpStatusCode,
        val currentIndex: Int?,
        val scenarios: List<String>?,
        val configState: String?,
    )
}
