package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointRegistry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
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
import kotlin.test.assertNotNull
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
        val description = spec["info"]?.jsonObject?.get("description")?.jsonPrimitive?.content.orEmpty()

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
        val description = spec["info"]?.jsonObject?.get("description")?.jsonPrimitive?.content.orEmpty()

        ApiTag.entries.forEach { apiTag ->
            assertTrue(
                description.contains(apiTag.tagName),
                "info.descriptionにAPIカテゴリ '${apiTag.tagName}' が含まれていない",
            )
        }
    }

    @Test
    fun `drug regulatory_class description contains english SerialName via interpolation`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/drugs", parameterName = "regulatory_class")
        assertTrue(
            description.contains("prescription_required"),
            "regulatory_class description に英語 SerialName 'prescription_required' が含まれていない: $description",
        )
    }

    @Test
    fun `drug route description contains english SerialName via interpolation`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/drugs", parameterName = "route")
        assertTrue(
            description.contains("oral"),
            "route description に英語 SerialName 'oral' が含まれていない: $description",
        )
    }

    @Test
    fun `drug dosage_form description contains english SerialName via interpolation`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/drugs", parameterName = "dosage_form")
        assertTrue(
            description.contains("tablet"),
            "dosage_form description に英語 SerialName 'tablet' が含まれていない: $description",
        )
    }

    @Test
    fun `disease icd10_chapter parameter has description`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/diseases", parameterName = "icd10_chapter")
        assertTrue(
            description.isNotEmpty(),
            "icd10_chapter description が空文字列",
        )
        assertTrue(
            description.contains("chapter_i"),
            "icd10_chapter description に英語 SerialName 'chapter_i' が含まれていない: $description",
        )
    }

    @Test
    fun `disease department parameter has description`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/diseases", parameterName = "department")
        assertTrue(
            description.contains("internal_medicine"),
            "department description に英語 SerialName 'internal_medicine' が含まれていない: $description",
        )
    }

    @Test
    fun `disease chronicity parameter has description`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/diseases", parameterName = "chronicity")
        assertTrue(
            description.contains("chronic"),
            "chronicity description に英語 SerialName 'chronic' が含まれていない: $description",
        )
    }

    @Test
    fun `disease infectious parameter has description`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/diseases", parameterName = "infectious")
        assertTrue(
            description.isNotEmpty(),
            "infectious description が空文字列",
        )
        assertTrue(
            description.contains("true") && description.contains("false"),
            "infectious description に Boolean literal 'true' / 'false' が含まれていない: $description",
        )
    }

    private suspend fun ApplicationTestBuilder.fetchParameterDescription(
        path: String,
        parameterName: String,
    ): String {
        val response = client.get(urlString = "/openapi.json")
        val spec = json.decodeFromString<JsonObject>(string = response.bodyAsText())
        val parameters = spec["paths"]?.jsonObject
            ?.get(key = path)?.jsonObject
            ?.get(key = "get")?.jsonObject
            ?.get(key = "parameters")?.jsonArray
            ?: JsonArray(content = emptyList())
        val parameter = parameters.firstOrNull { element ->
            element.jsonObject["name"]?.jsonPrimitive?.content == parameterName
        }
        assertNotNull(
            actual = parameter,
            message = "$path の OpenAPI parameters に '$parameterName' が見つからない",
        )
        return parameter.jsonObject["description"]?.jsonPrimitive?.content.orEmpty()
    }
}
