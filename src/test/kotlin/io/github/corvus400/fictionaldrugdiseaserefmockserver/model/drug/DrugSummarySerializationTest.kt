package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugSummarySerializationTest {
    @Test
    fun `DrugSummary serializes with exactly 10 snake_case fields`() {
        val summary = DrugSummary(
            id = "drug_0001",
            brandName = "テスト販売名",
            genericName = "テスト一般名",
            therapeuticCategoryName = "経口鎮痛薬",
            regulatoryClass = listOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
            dosageForm = DosageForm.TABLET,
            brandNameKana = "テストハンバイメイ",
            atcCode = "A01AA01",
            revisedAt = "2024-03-01",
        )
        val encoded = AppJson.encodeToString(summary)
        val jsonObject = Json.parseToJsonElement(encoded).jsonObject

        assertEquals(10, jsonObject.size)
        assertEquals("drug_0001", jsonObject["id"]?.toString()?.trim('"'))
        assertEquals("テスト販売名", jsonObject["brand_name"]?.toString()?.trim('"'))
        assertEquals("テスト一般名", jsonObject["generic_name"]?.toString()?.trim('"'))
        assertEquals("経口鎮痛薬", jsonObject["therapeutic_category_name"]?.toString()?.trim('"'))
        assertEquals("/images/dosage_form/tablet?size=Original", jsonObject["image_url"]?.toString()?.trim('"'))
        assertTrue(encoded.contains(""""regulatory_class":["prescription_required"]"""))
        assertTrue(encoded.contains(""""dosage_form":"tablet""""))

        val keyCasingViolations = jsonObject.keys.filter { key ->
            key != key.lowercase() || key.contains(Regex("[A-Z]"))
        }
        assertTrue(
            actual = keyCasingViolations.isEmpty(),
            message = "Non snake_case keys detected: $keyCasingViolations",
        )
    }
}
