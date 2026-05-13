package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiseaseSerializationTest {
    @Test
    fun `Disease serializes required identification and naming fields in snake_case`() {
        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(minimalDisease())).jsonObject
        val actual = listOf("id", "name", "name_kana", "revised_at")
            .associateWith { key -> jsonObject[key]?.jsonPrimitive?.content }

        assertEquals(
            expected = mapOf(
                "id" to "disease_0001",
                "name" to "テスト疾患",
                "name_kana" to "テストシッカン",
                "revised_at" to "2024-03-01",
            ),
            actual = actual,
        )
    }

    @Test
    fun `Disease serializes classification enums with english snake_case SerialName values`() {
        val json = AppJson.encodeToString(minimalDisease())
        val missingFragments = listOf(
            """"icd10_chapter":"chapter_iv"""",
            """"medical_department":["endocrinology"]""",
            """"chronicity":"chronic"""",
        ).filterNot { json.contains(it) }

        assertTrue(
            actual = missingFragments.isEmpty(),
            message = "Disease classification enum serialization is missing fragments: $missingFragments",
        )
    }

    @Test
    fun `Disease serializes required nested objects recursively`() {
        val json = AppJson.encodeToString(minimalDisease())
        val missingFragments = listOf(
            """"symptoms":{"main_symptoms":["多飲","多尿"]""",
            """"diagnostic_criteria":{"required":["空腹時サンプル値 126 以上"]""",
            """"treatments":{"pharmacological":[],"non_pharmacological":[],"acute_phase_protocol":[]}""",
        ).filterNot { json.contains(it) }

        assertTrue(
            actual = missingFragments.isEmpty(),
            message = "Disease required nested serialization is missing fragments: $missingFragments",
        )
    }

    @Test
    fun `Disease serializes optional list fields as empty array by default`() {
        val json = AppJson.encodeToString(minimalDisease())
        val missingEmptyArrays = listOf(
            "synonyms",
            "required_exams",
            "differential_diagnoses",
            "complications",
            "prevention",
            "related_drug_ids",
            "related_disease_ids",
        ).filterNot { field -> json.contains(""""$field":[]""") }

        assertTrue(
            actual = missingEmptyArrays.isEmpty(),
            message = "Disease optional list fields must serialize as empty arrays: $missingEmptyArrays",
        )
    }

    @Test
    fun `Disease serializes nullable optional fields as null by default`() {
        val json = AppJson.encodeToString(minimalDisease())
        val missingNulls = listOf(
            "name_english",
            "epidemiology",
            "severity_grading",
            "prognosis",
        ).filterNot { field -> json.contains(""""$field":null""") }

        assertTrue(
            actual = missingNulls.isEmpty(),
            message = "Disease nullable optional fields must serialize as null: $missingNulls",
        )
    }

    @Test
    fun `Disease serializes exactly 25 fields with snake_case keys`() {
        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(minimalDisease())).jsonObject
        val violations = buildList {
            if (jsonObject.size != 25) {
                add("expected 25 fields but was ${jsonObject.size}")
            }
            addAll(
                jsonObject.keys
                    .filter { key -> key != key.lowercase() || key.contains(Regex("[A-Z]")) }
                    .map { key -> "Non snake_case key detected: $key" },
            )
        }

        assertTrue(
            actual = violations.isEmpty(),
            message = "Disease field shape violations: $violations",
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
