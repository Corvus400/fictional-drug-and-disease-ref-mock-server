package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
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

        assertEquals(8, jsonObject.size)
        assertEquals(
            setOf(
                "id",
                "name",
                "icd10_chapter",
                "medical_department",
                "chronicity",
                "infectious",
                "name_kana",
                "revised_at",
            ),
            jsonObject.keys,
        )
        val keyCasingViolations = jsonObject.keys.filter { key ->
            key != key.lowercase() || key.contains(Regex("[A-Z]"))
        }
        assertTrue(
            actual = keyCasingViolations.isEmpty(),
            message = "Non snake_case keys detected: $keyCasingViolations",
        )
        assertEquals("disease_0001", jsonObject["id"]?.toString()?.trim('"'))
        assertEquals("テスト疾患", jsonObject["name"]?.toString()?.trim('"'))
        assertEquals("\"chapter_iv\"", jsonObject["icd10_chapter"]?.toString())
        assertEquals("[\"endocrinology\"]", jsonObject["medical_department"]?.toString())
        assertEquals("\"chronic\"", jsonObject["chronicity"]?.toString())
        assertEquals("false", jsonObject["infectious"]?.toString())
        assertEquals("テストシッカン", jsonObject["name_kana"]?.toString()?.trim('"'))
        assertEquals("2026-04-23", jsonObject["revised_at"]?.toString()?.trim('"'))
    }
}
