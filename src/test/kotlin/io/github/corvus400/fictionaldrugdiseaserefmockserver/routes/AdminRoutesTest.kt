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
import kotlin.test.assertNotNull

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
    fun `GET admin configs returns current overrides`() = testApplication {
        application { module() }

        val response = client.get("/__admin/configs")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        // 初期状態では空のオブジェクトまたは設定されたオーバーライドが返る
        assertNotNull(body)
    }

    @Test
    fun `POST admin configs drugList empty override switches drug list to zero items`() = testApplication {
        application { module() }

        client.post("/__admin/reset")

        val overrideResponse = client.post("/__admin/configs/drugList") {
            contentType(ContentType.Application.Json)
            setBody("""{"state": "empty"}""")
        }
        assertEquals(HttpStatusCode.OK, overrideResponse.status)

        val listResponse = client.get("/v1/drugs")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val body = json.decodeFromString<JsonObject>(listResponse.bodyAsText())
        val items = body["items"]?.jsonArray
        assertNotNull(items)
        assertEquals(
            expected = 0,
            actual = items.size,
            message = "drugList を empty オーバーライド後の /drugs items は 0 件である必要がある",
        )
        assertEquals(
            expected = 0,
            actual = body["total_count"]?.jsonPrimitive?.int,
            message = "drugList を empty オーバーライド後の /drugs total_count は 0 である必要がある",
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
        assertEquals(HttpStatusCode.OK, overrideResponse.status)

        val listResponse = client.get("/v1/diseases")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val body = json.decodeFromString<JsonObject>(listResponse.bodyAsText())
        val items = body["items"]?.jsonArray
        assertNotNull(items)
        assertEquals(
            expected = 0,
            actual = items.size,
            message = "diseaseList を empty オーバーライド後の /diseases items は 0 件である必要がある",
        )
        assertEquals(
            expected = 0,
            actual = body["total_count"]?.jsonPrimitive?.int,
            message = "diseaseList を empty オーバーライド後の /diseases total_count は 0 である必要がある",
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
        assertEquals(0, emptyBody["total_count"]?.jsonPrimitive?.int)

        val resetResponse = client.post("/__admin/reset")
        assertEquals(HttpStatusCode.OK, resetResponse.status)

        val restoredResponse = client.get("/v1/drugs")
        val restoredBody = json.decodeFromString<JsonObject>(restoredResponse.bodyAsText())
        assertEquals(
            expected = 120,
            actual = restoredBody["total_count"]?.jsonPrimitive?.int,
            message = "Admin reset 後の /drugs default シナリオ total_count は 120 に戻る必要がある",
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
        assertNotNull(drugListEntry)
        assertEquals(
            expected = "empty",
            actual = drugListEntry["state"]?.jsonPrimitive?.content,
            message = "GET /__admin/configs に drugList=empty が反映されている必要がある",
        )

        client.post("/__admin/reset")
    }
}
