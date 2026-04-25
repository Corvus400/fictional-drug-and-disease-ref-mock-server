package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.categories

import io.github.corvus400.fictionaldrugdiseaserefmockserver.module
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoriesModuleTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GET categories returns HTTP 200`() = testApplication {
        application { module() }

        val response = client.get("/categories")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET categories returns JSON with exactly 7 top-level keys`() = testApplication {
        application { module() }

        val response = client.get("/categories")

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
}
