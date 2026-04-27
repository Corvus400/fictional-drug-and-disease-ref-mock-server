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
    fun `DiseaseSummary serializes with exactly 6 snake_case fields`() {
        val summary = DiseaseSummary(
            id = "disease_0001",
            name = "テスト疾患",
            icd10Chapter = Icd10Chapter.CHAPTER_IV,
            medicalDepartment = listOf(MedicalDepartment.ENDOCRINOLOGY),
            chronicity = Chronicity.CHRONIC,
            infectious = false,
        )

        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(summary)).jsonObject

        assertEquals(6, jsonObject.size)
        assertEquals(
            setOf("id", "name", "icd10_chapter", "medical_department", "chronicity", "infectious"),
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
        assertEquals("\"内分泌、栄養および代謝疾患\"", jsonObject["icd10_chapter"]?.toString())
        assertEquals("[\"endocrinology\"]", jsonObject["medical_department"]?.toString())
        assertEquals("\"chronic\"", jsonObject["chronicity"]?.toString())
        assertEquals("false", jsonObject["infectious"]?.toString())
    }
}
