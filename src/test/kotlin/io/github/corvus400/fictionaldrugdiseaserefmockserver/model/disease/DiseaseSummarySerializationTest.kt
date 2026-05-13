package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertTrue

class DiseaseSummarySerializationTest {
    @Test
    fun `DiseaseSummary serializes with exactly 8 snake_case fields`() {
        val summary = DiseaseSummary(
            id = "disease_0001",
            name = "テスト疾患",
            icd10Chapter = Icd10Chapter.CHAPTER_IV,
            medicalDepartment = listOf(MedicalDepartment.ENDOCRINOLOGY),
            chronicity = Chronicity.CHRONIC,
            infectious = false,
            nameKana = "テストシッカン",
            revisedAt = "2026-04-23",
        )

        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(summary)).jsonObject

        val violations = buildList {
            if (jsonObject.size != 8) {
                add("expected 8 fields but was ${jsonObject.size}")
            }
            val expectedKeys = setOf(
                "id",
                "name",
                "icd10_chapter",
                "medical_department",
                "chronicity",
                "infectious",
                "name_kana",
                "revised_at",
            )
            if (jsonObject.keys != expectedKeys) {
                add("expected keys=$expectedKeys but was ${jsonObject.keys}")
            }
            addAll(
                jsonObject.keys
                    .filter { key -> key != key.lowercase() || key.contains(Regex("[A-Z]")) }
                    .map { key -> "Non snake_case key detected: $key" },
            )
            val actualValues = mapOf(
                "id" to jsonObject["id"]?.jsonPrimitive?.content,
                "name" to jsonObject["name"]?.jsonPrimitive?.content,
                "icd10_chapter" to jsonObject["icd10_chapter"]?.toString(),
                "medical_department" to jsonObject["medical_department"]?.toString(),
                "chronicity" to jsonObject["chronicity"]?.toString(),
                "infectious" to jsonObject["infectious"]?.toString(),
                "name_kana" to jsonObject["name_kana"]?.jsonPrimitive?.content,
                "revised_at" to jsonObject["revised_at"]?.jsonPrimitive?.content,
            )
            val expectedValues = mapOf(
                "id" to "disease_0001",
                "name" to "テスト疾患",
                "icd10_chapter" to "\"chapter_iv\"",
                "medical_department" to "[\"endocrinology\"]",
                "chronicity" to "\"chronic\"",
                "infectious" to "false",
                "name_kana" to "テストシッカン",
                "revised_at" to "2026-04-23",
            )
            if (actualValues != expectedValues) {
                add("expected values=$expectedValues but was $actualValues")
            }
        }

        assertTrue(
            actual = violations.isEmpty(),
            message = "DiseaseSummary serialization violations: $violations",
        )
    }
}
