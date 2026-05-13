package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
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

        val violations = buildList {
            if (jsonObject.size != 10) {
                add("expected 10 fields but was ${jsonObject.size}")
            }
            val actualValues = mapOf(
                "id" to jsonObject["id"]?.jsonPrimitive?.content,
                "brand_name" to jsonObject["brand_name"]?.jsonPrimitive?.content,
                "generic_name" to jsonObject["generic_name"]?.jsonPrimitive?.content,
                "therapeutic_category_name" to jsonObject["therapeutic_category_name"]?.jsonPrimitive?.content,
                "image_url" to jsonObject["image_url"]?.jsonPrimitive?.content,
            )
            val expectedValues = mapOf(
                "id" to "drug_0001",
                "brand_name" to "テスト販売名",
                "generic_name" to "テスト一般名",
                "therapeutic_category_name" to "経口鎮痛薬",
                "image_url" to "/v1/images/dosage-forms/tablet?size=Original",
            )
            if (actualValues != expectedValues) {
                add("expected values=$expectedValues but was $actualValues")
            }
            listOf(
                """"regulatory_class":["prescription_required"]""",
                """"dosage_form":"tablet"""",
            ).filterNot { encoded.contains(it) }
                .forEach { fragment -> add("missing fragment: $fragment") }
            addAll(
                jsonObject.keys
                    .filter { key -> key != key.lowercase() || key.contains(Regex("[A-Z]")) }
                    .map { key -> "Non snake_case key detected: $key" },
            )
        }

        assertTrue(
            actual = violations.isEmpty(),
            message = "DrugSummary serialization violations: $violations",
        )
    }
}
