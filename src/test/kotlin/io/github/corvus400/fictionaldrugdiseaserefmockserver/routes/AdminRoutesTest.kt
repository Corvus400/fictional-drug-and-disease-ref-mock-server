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

class AdminRoutesTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET health returns ok status`() = testApplication {
        application { module() }

        val response = client.get("/health")
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals(
            expected = StatusSnapshot(status = HttpStatusCode.OK, bodyStatus = "ok"),
            actual = StatusSnapshot(
                status = response.status,
                bodyStatus = body["status"]?.jsonPrimitive?.content,
            ),
        )
    }

    @Test
    fun `GET admin configs returns current overrides`() = testApplication {
        application { module() }

        val response = client.get("/__admin/configs")

        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status,
            message = "GET /__admin/configs must return 200 and decode as a JSON object",
        )
        json.decodeFromString<JsonObject>(response.bodyAsText())
    }

    @Test
    fun `POST admin configs drugList empty override switches drug list to zero items`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val overrideResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }

        val listResponse = client.get("/v1/drugs")
        val body = json.decodeFromString<JsonObject>(listResponse.bodyAsText())
        val items = body["items"]?.jsonArray
        assertEquals(
            expected = EmptyOverrideSnapshot(
                overrideStatus = HttpStatusCode.OK,
                listStatus = HttpStatusCode.OK,
                itemsSize = 0,
                totalCount = 0,
            ),
            actual = EmptyOverrideSnapshot(
                overrideStatus = overrideResponse.status,
                listStatus = listResponse.status,
                itemsSize = items?.size,
                totalCount = body["total_count"]?.jsonPrimitive?.int,
            ),
        )

        client.post("/__admin/reset")
    }

    @Test
    fun `POST admin configs diseaseList empty override switches disease list to zero items`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val overrideResponse = client.post("/__admin/configs/diseaseList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }

        val listResponse = client.get("/v1/diseases")
        val body = json.decodeFromString<JsonObject>(listResponse.bodyAsText())
        val items = body["items"]?.jsonArray
        assertEquals(
            expected = EmptyOverrideSnapshot(
                overrideStatus = HttpStatusCode.OK,
                listStatus = HttpStatusCode.OK,
                itemsSize = 0,
                totalCount = 0,
            ),
            actual = EmptyOverrideSnapshot(
                overrideStatus = overrideResponse.status,
                listStatus = listResponse.status,
                itemsSize = items?.size,
                totalCount = body["total_count"]?.jsonPrimitive?.int,
            ),
        )

        client.post("/__admin/reset")
    }

    @Test
    fun `POST admin reset on drug override restores drug list to default 120 total_count`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }

        val emptyResponse = client.get("/v1/drugs")
        val emptyBody = json.decodeFromString<JsonObject>(emptyResponse.bodyAsText())

        val resetResponse = client.post("/__admin/reset")

        val restoredResponse = client.get("/v1/drugs")
        val restoredBody = json.decodeFromString<JsonObject>(restoredResponse.bodyAsText())
        assertEquals(
            expected = ResetSnapshot(
                emptyTotalCount = 0,
                resetStatus = HttpStatusCode.OK,
                restoredTotalCount = 120,
            ),
            actual = ResetSnapshot(
                emptyTotalCount = emptyBody["total_count"]?.jsonPrimitive?.int,
                resetStatus = resetResponse.status,
                restoredTotalCount = restoredBody["total_count"]?.jsonPrimitive?.int,
            ),
        )
    }

    @Test
    fun `GET admin configs body contains drugList override after POST`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }

        val response = client.get("/__admin/configs")
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val drugListEntry = body["drugList"]?.jsonObject
        assertEquals(
            expected = DrugListConfigSnapshot(entryExists = true, state = "empty"),
            actual = DrugListConfigSnapshot(
                entryExists = drugListEntry != null,
                state = drugListEntry?.get("state")?.jsonPrimitive?.content,
            ),
        )

        client.post("/__admin/reset")
    }

    private data class StatusSnapshot(
        val status: HttpStatusCode,
        val bodyStatus: String?,
    )

    private data class EmptyOverrideSnapshot(
        val overrideStatus: HttpStatusCode,
        val listStatus: HttpStatusCode,
        val itemsSize: Int?,
        val totalCount: Int?,
    )

    private data class ResetSnapshot(
        val emptyTotalCount: Int?,
        val resetStatus: HttpStatusCode,
        val restoredTotalCount: Int?,
    )

    private data class DrugListConfigSnapshot(
        val entryExists: Boolean,
        val state: String?,
    )
}
