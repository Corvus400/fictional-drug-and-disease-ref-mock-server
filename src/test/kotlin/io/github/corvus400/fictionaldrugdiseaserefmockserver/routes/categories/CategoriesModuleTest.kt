package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.categories

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private val SEVEN_TOP_LEVEL_KEYS: List<String> = listOf(
    "atc",
    "therapeutic_categories",
    "route_of_administration",
    "dosage_form",
    "regulatory_class",
    "icd10_chapters",
    "medical_departments",
)

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
    fun `GET categories atc 0 has keys code, label (not a bare string)`() = categoriesEndpointTest { response ->
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val atcArray = body["atc"]?.jsonArray
        assertNotNull(atcArray, "GET /categories must expose .atc as a JSON array")
        val first = atcArray[0].jsonObject
        val code = first["code"]?.jsonPrimitive
        assertNotNull(
            actual = code,
            message = "GET /categories .atc[0] must contain a string `code` property " +
                "(must not be a bare string element)",
        )
        assertTrue(
            actual = code.isString,
            message = "GET /categories .atc[0].code must be a JSON string, not a number/bool",
        )
        val label = first["label"]?.jsonPrimitive
        assertNotNull(
            actual = label,
            message = "GET /categories .atc[0] must contain a string `label` property",
        )
        assertTrue(
            actual = label.isString,
            message = "GET /categories .atc[0].label must be a JSON string, not a number/bool",
        )
    }

    @Test
    fun `GET categories atc has 14 entries covering A to V`() = categoriesEndpointTest { response ->
        val body = json.decodeFromString<JsonObject>(response.bodyAsText())
        val atcArray = body["atc"]?.jsonArray
        assertNotNull(atcArray, "GET /categories must expose .atc as a JSON array")
        val codes = atcArray.map { element -> element.jsonObject.getValue(key = "code").jsonPrimitive.content }
        assertEquals(
            expected = setOf("A", "B", "C", "D", "G", "H", "J", "L", "M", "N", "P", "R", "S", "V"),
            actual = codes.toSet(),
            message = "GET /categories .atc must cover all 14 ATC first-letter anatomical groups " +
                "(A,B,C,D,G,H,J,L,M,N,P,R,S,V)",
        )
        assertEquals(
            expected = 14,
            actual = codes.size,
            message = "GET /categories .atc must contain exactly 14 entries (no duplicates, no extras)",
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

    @Test
    fun `GET categories icd10_chapters first entry has keys roman, code, label`() =
        categoriesEndpointTest { response ->
            val body = json.decodeFromString<JsonObject>(response.bodyAsText())
            val chapters = body.getValue(key = "icd10_chapters").jsonArray
            val firstEntry = chapters.first().jsonObject
            assertEquals(
                expected = setOf("roman", "code", "label"),
                actual = firstEntry.keys,
                message = "icd10_chapters[0] must expose exactly the keys roman, code, label " +
                    "(structure pin for ICD-10 chapter entry)",
            )
        }

    @Test
    fun `GET categories icd10_chapters has exactly 22 entries`() =
        categoriesEndpointTest { response ->
            val body = json.decodeFromString<JsonObject>(response.bodyAsText())
            val chapters = body.getValue(key = "icd10_chapters").jsonArray
            assertEquals(
                expected = 22,
                actual = chapters.size,
                message = "icd10_chapters must contain exactly 22 entries (ICD-10 全 22 章)",
            )
        }

    @Test
    fun `GET categories icd10_chapters roman values cover I to XXII`() =
        categoriesEndpointTest { response ->
            val body = json.decodeFromString<JsonObject>(response.bodyAsText())
            val chapters = body.getValue(key = "icd10_chapters").jsonArray
            val romanValues = chapters.map { entry ->
                entry.jsonObject.getValue(key = "roman").jsonPrimitive.content
            }.toSet()
            assertEquals(
                expected = setOf(
                    "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI",
                    "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII",
                ),
                actual = romanValues,
                message = "icd10_chapters roman values must cover the full I..XXII range " +
                    "(WHO ICD-10 22 章を網羅)",
            )
        }

    /**
     * §基本方針 9 (シナリオ非依存原則) の事前条件として、`/categories` が返す全 7 lists は
     * 起動時固定 fixture から導出された **空でない** メタデータでなければならない。
     * 空であれば「全シナリオで同じ」を満たしてもメタデータとして無価値なため、
     * シナリオ非依存性検証 (Red 2 / Red 3) の意味そのものが消える。
     */
    @Test
    fun `GET categories under default scenario returns 7 top-level keys with non-zero counts`() =
        categoriesEndpointTest { response ->
            val body = json.decodeFromString<JsonObject>(response.bodyAsText())
            val countsByKey: Map<String, Int> = SEVEN_TOP_LEVEL_KEYS.associateWith { key ->
                (body[key] as? JsonArray)?.size
                    ?: error("`/categories` key '$key' must be a JSON array but was ${body[key]}")
            }
            val zeroKeys: List<String> = countsByKey.filterValues { count -> count == 0 }.keys.toList()
            assertTrue(
                actual = zeroKeys.isEmpty(),
                message = "GET /categories must return non-zero counts for ALL 7 keys; " +
                    "the following keys still return 0 entries: $zeroKeys (counts=$countsByKey)",
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
