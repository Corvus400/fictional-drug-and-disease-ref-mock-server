package io.github.corvus400.mockserverbase.plugins

import io.github.corvus400.mockserverbase.catalog.EndpointRegistry
import io.github.corvus400.mockserverbase.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OpenApiDocTest {
    @BeforeTest
    fun setUp() {
        EndpointRegistry.clear()
    }

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `openapi json is generated and parseable`() = testApplication {
        application { module() }
        val response = client.get("/openapi.json")
        assertEquals(HttpStatusCode.OK, response.status)
        val spec = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertTrue(spec.containsKey("openapi"), "OpenAPI仕様にopenapiフィールドが存在する")
        assertTrue(spec.containsKey("paths"), "OpenAPI仕様にpathsフィールドが存在する")
        assertTrue(spec.containsKey("info"), "OpenAPI仕様にinfoフィールドが存在する")
    }

    @Test
    fun `all ApiTag entries are defined in openapi tags`() = testApplication {
        application { module() }
        val response = client.get("/openapi.json")
        val spec = json.decodeFromString<JsonObject>(response.bodyAsText())
        val tags = spec["tags"]?.jsonArray ?: JsonArray(emptyList())
        val tagNames = tags.map { it.jsonObject["name"]?.jsonPrimitive?.content }

        ApiTag.entries.forEach { apiTag ->
            assertTrue(
                tagNames.contains(apiTag.tagName),
                "タグ '${apiTag.tagName}' がOpenAPI仕様に定義されていない",
            )
        }
    }

    @Test
    fun `all endpoints are documented in openapi paths`() = testApplication {
        application { module() }
        val response = client.get("/openapi.json")
        val spec = json.decodeFromString<JsonObject>(response.bodyAsText())
        val paths = spec["paths"]?.jsonObject ?: JsonObject(emptyMap())

        val registeredPaths = EndpointRegistry.getAll()
            .map { it.path.substringBefore("?") }
            .toSet()

        registeredPaths.forEach { expectedPath ->
            assertTrue(
                paths.containsKey(expectedPath),
                "EndpointRegistryに登録されているパス '$expectedPath' がOpenAPI pathsに存在しない。" +
                    "ルート定義にio.github.smiley4.ktoropenapi.get/postを使用しているか確認してください。",
            )
        }
    }

    @Test
    fun `swagger ui is accessible`() = testApplication {
        application { module() }
        val response = client.get("/swagger")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `redoc is accessible`() = testApplication {
        application { module() }
        val response = client.get("/redoc")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `info description does not contain hardcoded endpoint counts`() = testApplication {
        application { module() }
        val response = client.get("/openapi.json")
        val spec = json.decodeFromString<JsonObject>(response.bodyAsText())
        val description = spec["info"]?.jsonObject?.get("description")?.jsonPrimitive?.content ?: ""

        assertTrue(
            description.contains("APIカテゴリ").not() || description.contains("26").not(),
            "info.descriptionにハードコードのエンドポイント数が含まれている",
        )
    }

    @Test
    fun `info description contains all ApiTag categories`() = testApplication {
        application { module() }
        val response = client.get("/openapi.json")
        val spec = json.decodeFromString<JsonObject>(response.bodyAsText())
        val description = spec["info"]?.jsonObject?.get("description")?.jsonPrimitive?.content ?: ""

        ApiTag.entries.forEach { apiTag ->
            assertTrue(
                description.contains(apiTag.tagName),
                "info.descriptionにAPIカテゴリ '${apiTag.tagName}' が含まれていない",
            )
        }
    }
}
