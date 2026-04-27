package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiseaseSerializationTest {
    @Test
    fun `Disease serializes required identification and naming fields in snake_case`() {
        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(minimalDisease())).jsonObject
        assertEquals("disease_0001", jsonObject["id"]?.toString()?.trim('"'))
        assertEquals("テスト疾患", jsonObject["name"]?.toString()?.trim('"'))
        assertEquals("テストシッカン", jsonObject["name_kana"]?.toString()?.trim('"'))
        assertEquals("2024-03-01", jsonObject["revised_at"]?.toString()?.trim('"'))
    }

    @Test
    fun `Disease serializes classification enums with Japanese SerialName values`() {
        val json = AppJson.encodeToString(minimalDisease())
        assertTrue(json.contains(""""icd10_chapter":"内分泌、栄養および代謝疾患""""))
        assertTrue(json.contains(""""medical_department":["endocrinology"]"""))
        assertTrue(json.contains(""""chronicity":"chronic""""))
    }

    @Test
    fun `Disease serializes required nested objects recursively`() {
        val json = AppJson.encodeToString(minimalDisease())
        assertTrue(json.contains(""""symptoms":{"main_symptoms":["多飲","多尿"]"""))
        assertTrue(json.contains(""""diagnostic_criteria":{"required":["空腹時サンプル値 126 以上"]"""))
        assertTrue(
            json.contains(""""treatments":{"pharmacological":[],"non_pharmacological":[],"acute_phase_protocol":[]}"""),
        )
    }

    @Test
    fun `Disease serializes optional list fields as empty array by default`() {
        val json = AppJson.encodeToString(minimalDisease())
        assertTrue(json.contains(""""synonyms":[]"""))
        assertTrue(json.contains(""""required_exams":[]"""))
        assertTrue(json.contains(""""differential_diagnoses":[]"""))
        assertTrue(json.contains(""""complications":[]"""))
        assertTrue(json.contains(""""prevention":[]"""))
        assertTrue(json.contains(""""related_drug_ids":[]"""))
        assertTrue(json.contains(""""related_disease_ids":[]"""))
    }

    @Test
    fun `Disease serializes nullable optional fields as null by default`() {
        val json = AppJson.encodeToString(minimalDisease())
        assertTrue(json.contains(""""name_english":null"""))
        assertTrue(json.contains(""""epidemiology":null"""))
        assertTrue(json.contains(""""severity_grading":null"""))
        assertTrue(json.contains(""""prognosis":null"""))
    }

    @Test
    fun `Disease serializes exactly 24 fields with snake_case keys`() {
        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(minimalDisease())).jsonObject
        assertEquals(24, jsonObject.size)
        val keyCasingViolations = jsonObject.keys.filter { key ->
            key != key.lowercase() || key.contains(Regex("[A-Z]"))
        }
        assertTrue(
            actual = keyCasingViolations.isEmpty(),
            message = "Non snake_case keys detected: $keyCasingViolations",
        )
    }

    private fun minimalDisease(): Disease =
        Disease(
            id = "disease_0001",
            name = "テスト疾患",
            nameKana = "テストシッカン",
            icd10Chapter = Icd10Chapter.CHAPTER_IV,
            medicalDepartment = listOf(MedicalDepartment.ENDOCRINOLOGY),
            chronicity = Chronicity.CHRONIC,
            infectious = false,
            summary = "テスト疾患はインスリン様ホルモンの作用不全を特徴とする慢性代謝疾患である。",
            etiology = "原因は遺伝的素因と生活習慣の相互作用と考えられている。",
            symptoms = SymptomInfo(
                mainSymptoms = listOf("多飲", "多尿"),
            ),
            diagnosticCriteria = DiagnosticCriteriaInfo(
                required = listOf("空腹時サンプル値 126 以上"),
            ),
            treatments = TreatmentInfo(),
            revisedAt = "2024-03-01",
        )
}
