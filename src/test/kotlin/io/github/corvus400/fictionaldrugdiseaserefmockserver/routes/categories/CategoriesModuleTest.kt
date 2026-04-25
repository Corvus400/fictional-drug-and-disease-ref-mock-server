package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.categories

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CategoriesModuleTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET categories returns HTTP 200`() = categoriesEndpointTest { response ->
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET categories returns JSON with exactly 7 top-level keys`() = categoriesEndpointTest { response ->
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        assertEquals(
            expected = setOf(
                "atc",
                "therapeutic_categories",
                "route_of_administration",
                "dosage_form",
                "regulatory_class",
                "icd10_chapters",
                "medical_departments",
            ),
            actual = body.keys,
            message = "GET /categories must expose exactly 7 top-level snake_case keys " +
                "(atc, therapeutic_categories, route_of_administration, dosage_form, " +
                "regulatory_class, icd10_chapters, medical_departments)",
        )
    }

    @Test
    fun `GET categories therapeutic_categories first element has keys id and label`() =
        categoriesEndpointTest { response ->
            val body = json.decodeFromString<JsonObject>(response.bodyAsText())
            val therapeuticCategories = body.getValue(key = "therapeutic_categories").jsonArray
            val firstEntry = therapeuticCategories.first().jsonObject
            assertEquals(
                expected = setOf("id", "label"),
                actual = firstEntry.keys,
                message = "therapeutic_categories[0] must expose exactly the snake_case keys " +
                    "{id, label} so clients can render slug-stable category options",
            )
        }

    @Test
    fun `GET categories therapeutic_categories ids are all unique (no duplicates)`() =
        categoriesEndpointTest { response ->
            val body = json.decodeFromString<JsonObject>(response.bodyAsText())
            val therapeuticCategories = body.getValue(key = "therapeutic_categories").jsonArray
            val ids = therapeuticCategories.map { entry ->
                entry.jsonObject.getValue(key = "id").jsonPrimitive.content
            }
            assertEquals(
                expected = ids.size,
                actual = ids.toSet().size,
                message = "therapeutic_categories ids must be distinct after slug-id derivation; " +
                    "duplicates indicate a slug collision or distinctBy regression. ids=$ids",
            )
        }

    @Test
    fun `GET categories therapeutic_categories is non-empty`() = categoriesEndpointTest { response ->
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val therapeuticCategories = body.getValue(key = "therapeutic_categories").jsonArray
        assertTrue(
            actual = therapeuticCategories.isNotEmpty(),
            message = "therapeutic_categories must be derived from the 120 fixed drugs and never " +
                "regress to an empty list (per-scenario empty fixture is forbidden)",
        )
    }

    /**
     * `module()` 起動 → `GET /categories` 1 回呼出までを共通化するヘルパー。
     *
     * 既存 DrugModuleTest / DiseaseModuleTest は同一テンプレを各 @Test で繰り返すパターンだが、
     * 本ヘルパーは新規 CategoriesModuleTest 内のみに閉じて重複を解消する (既存テストは無変更)。
     * 将来 admin 経由で複数 client コールが必要になった際は各テスト内に直接 testApplication を
     * 書き戻すことを許容する。
     */
    private fun categoriesEndpointTest(
        block: suspend ApplicationTestBuilder.(HttpResponse) -> Unit,
    ) = testApplication {
        application { module() }
        val response = client.get("/categories")
        block(response)
    }
}
