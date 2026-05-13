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
        val spec = json.decodeFromString<JsonObject>(response.bodyAsText())

        assertEquals(
            expected = OpenApiRootSnapshot(
                status = HttpStatusCode.OK,
                hasOpenApi = true,
                hasPaths = true,
                hasInfo = true,
            ),
            actual = OpenApiRootSnapshot(
                status = response.status,
                hasOpenApi = spec.containsKey("openapi"),
                hasPaths = spec.containsKey("paths"),
                hasInfo = spec.containsKey("info"),
            ),
            message = "OpenAPI root contract mismatch",
        )
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
    fun `endpoints have non-empty openapi metadata`() = testApplication {
        application { module() }
        val response = client.get("/openapi.json")
        val spec = json.decodeFromString<JsonObject>(response.bodyAsText())
        val paths = spec["paths"]?.jsonObject ?: JsonObject(emptyMap())

        val endpoints = listOf(
            OpenApiMetadataExpectation(path = "/v1/categories", requiresParameters = false),
            OpenApiMetadataExpectation(path = "/v1/images/dosage-forms/{form}", requiresParameters = true),
            OpenApiMetadataExpectation(path = "/v1/images/drugs/{drugId}", requiresParameters = true),
        )

        val failures = endpoints.flatMap { expectation ->
            val operation = paths[expectation.path]?.jsonObject?.get("get")?.jsonObject
            expectation.metadataFailures(operation)
        }

        assertTrue(
            failures.isEmpty(),
            "OpenAPI metadata が不足しているエンドポイントがあります:\n${failures.joinToString(separator = "\n")}",
        )
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
    fun `info description contains fictional data disclaimer`() = testApplication {
        application { module() }
        val response = client.get("/openapi.json")
        val spec = json.decodeFromString<JsonObject>(response.bodyAsText())
        val description = spec["info"]?.jsonObject?.get("description")?.jsonPrimitive?.content.orEmpty()

        assertEquals(
            expected = FictionalDataDescriptionSnapshot(
                mentionsFictionalData = true,
                mentionsJapaneseFictionalData = true,
            ),
            actual = FictionalDataDescriptionSnapshot(
                mentionsFictionalData = description.contains("FICTIONAL DATA"),
                mentionsJapaneseFictionalData = description.contains("架空データ"),
            ),
            message = "contract assertion failed",
        )
    }

    @Test
    fun `drug regulatory_class description contains english SerialName via interpolation`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/v1/drugs", parameterName = "regulatory_class")
        assertTrue(
            description.contains("prescription_required"),
            "regulatory_class description に英語 SerialName 'prescription_required' が含まれていない: $description",
        )
    }

    @Test
    fun `drug route description contains english SerialName via interpolation`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/v1/drugs", parameterName = "route")
        assertTrue(
            description.contains("oral"),
            "route description に英語 SerialName 'oral' が含まれていない: $description",
        )
    }

    @Test
    fun `drug dosage_form description contains english SerialName via interpolation`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/v1/drugs", parameterName = "dosage_form")
        assertTrue(
            description.contains("tablet"),
            "dosage_form description に英語 SerialName 'tablet' が含まれていない: $description",
        )
    }

    @Test
    fun `disease icd10_chapter parameter has description`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/v1/diseases", parameterName = "icd10_chapter")
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
        val description = fetchParameterDescription(path = "/v1/diseases", parameterName = "department")
        assertTrue(
            description.contains("internal_medicine"),
            "department description に英語 SerialName 'internal_medicine' が含まれていない: $description",
        )
    }

    @Test
    fun `disease chronicity parameter has description`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/v1/diseases", parameterName = "chronicity")
        assertTrue(
            description.contains("chronic"),
            "chronicity description に英語 SerialName 'chronic' が含まれていない: $description",
        )
    }

    @Test
    fun `disease infectious parameter has description`() = testApplication {
        application { module() }
        val description = fetchParameterDescription(path = "/v1/diseases", parameterName = "infectious")

        assertEquals(
            expected = InfectiousParameterDescriptionSnapshot(
                nonEmpty = true,
                containsBooleanLiterals = true,
            ),
            actual = InfectiousParameterDescriptionSnapshot(
                nonEmpty = description.isNotEmpty(),
                containsBooleanLiterals = description.contains("true") && description.contains("false"),
            ),
            message = "infectious description に Boolean literal 'true' / 'false' が含まれていない: $description",
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

    private data class OpenApiMetadataExpectation(
        val path: String,
        val requiresParameters: Boolean,
    ) {
        fun metadataFailures(operation: JsonObject?): List<String> = buildList {
            if (operation == null) {
                add("$path: GET operation が存在しない")
                return@buildList
            }

            val description = operation["description"]?.jsonPrimitive?.content.orEmpty()
            if (description.isBlank()) {
                add("$path: description が空")
            }

            val responses = operation["responses"]?.jsonObject.orEmpty()
            if (responses.isEmpty()) {
                add("$path: responses が空")
            }

            if (requiresParameters) {
                val parameters = operation["parameters"]?.jsonArray.orEmpty()
                if (parameters.isEmpty()) {
                    add("$path: parameters が空")
                }
            }
        }
    }

    private data class OpenApiRootSnapshot(
        val status: HttpStatusCode,
        val hasOpenApi: Boolean,
        val hasPaths: Boolean,
        val hasInfo: Boolean,
    )

    private data class FictionalDataDescriptionSnapshot(
        val mentionsFictionalData: Boolean,
        val mentionsJapaneseFictionalData: Boolean,
    )

    private data class InfectiousParameterDescriptionSnapshot(
        val nonEmpty: Boolean,
        val containsBooleanLiterals: Boolean,
    )
}
