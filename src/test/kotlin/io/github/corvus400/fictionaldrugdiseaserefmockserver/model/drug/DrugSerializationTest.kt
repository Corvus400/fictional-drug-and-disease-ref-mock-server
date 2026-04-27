package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionByFrequency
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CompositionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.DosageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Dose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.IndicationItem
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.NumberedParagraph
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PackageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.StorageCondition
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugSerializationTest {
    @Test
    fun `Drug serializes required identification and naming fields in snake_case`() {
        val drug = minimalDrug()
        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(drug)).jsonObject
        assertEquals("drug_0001", jsonObject["id"]?.toString()?.trim('"'))
        assertEquals("テスト一般名", jsonObject["generic_name"]?.toString()?.trim('"'))
        assertEquals("テスト販売名", jsonObject["brand_name"]?.toString()?.trim('"'))
        assertEquals("テストハンバイメイ", jsonObject["brand_name_kana"]?.toString()?.trim('"'))
        assertEquals("N02BE01", jsonObject["atc_code"]?.toString()?.trim('"'))
        assertEquals("経口鎮痛薬", jsonObject["therapeutic_category_name"]?.toString()?.trim('"'))
        assertEquals("架空製薬株式会社", jsonObject["manufacturer"]?.toString()?.trim('"'))
        assertEquals("2024-03-01", jsonObject["revised_at"]?.toString()?.trim('"'))
    }

    @Test
    fun `Drug serializes classification enums with Japanese SerialName values`() {
        val json = AppJson.encodeToString(minimalDrug())
        assertTrue(json.contains(""""regulatory_class":["prescription_required"]"""))
        assertTrue(json.contains(""""dosage_form":"錠剤""""))
        assertTrue(json.contains(""""route_of_administration":"内服""""))
    }

    @Test
    fun `Drug serializes required nested objects recursively`() {
        val json = AppJson.encodeToString(minimalDrug())
        assertTrue(json.contains(""""composition":{"active_ingredient":"サンプルシン""""))
        assertTrue(json.contains(""""dosage":{"standard_dosage":"通常、成人には 1 回 100 mg を経口投与""""))
        assertTrue(json.contains(""""adverse_reactions":{"serious":[],"other":{"""))
        assertTrue(json.contains(""""packages":[{"size":"100 錠 (10 錠 × 10 PTP)""""))
    }

    @Test
    fun `Drug serializes required contraindications and indications lists`() {
        val json = AppJson.encodeToString(minimalDrug())
        assertTrue(
            json.contains(""""contraindications":[{"order":1,"sub_order":null,"content":"本剤の成分に対し過敏症の既往歴のある患者"}]"""),
        )
        assertTrue(json.contains(""""indications":[{"order":1,"content":"各種疾患における鎮痛"}]"""))
    }

    @Test
    fun `Drug serializes optional list fields as empty array by default`() {
        val json = AppJson.encodeToString(minimalDrug())
        assertTrue(json.contains(""""warning":[]"""))
        assertTrue(json.contains(""""indications_related_precautions":[]"""))
        assertTrue(json.contains(""""dosage_related_precautions":[]"""))
        assertTrue(json.contains(""""important_precautions":[]"""))
        assertTrue(json.contains(""""precautions_for_specific_populations":[]"""))
        assertTrue(json.contains(""""effects_on_lab_tests":[]"""))
        assertTrue(json.contains(""""administration_precautions":[]"""))
        assertTrue(json.contains(""""other_precautions":[]"""))
        assertTrue(json.contains(""""clinical_results":[]"""))
        assertTrue(json.contains(""""handling_precautions":[]"""))
        assertTrue(json.contains(""""approval_conditions":[]"""))
        assertTrue(json.contains(""""references":[]"""))
        assertTrue(json.contains(""""insurance_notes":[]"""))
        assertTrue(json.contains(""""related_disease_ids":[]"""))
    }

    @Test
    fun `Drug serializes nullable optional fields as null by default`() {
        val json = AppJson.encodeToString(minimalDrug())
        assertTrue(json.contains(""""yj_code":null"""))
        assertTrue(json.contains(""""interactions":null"""))
        assertTrue(json.contains(""""overdose":null"""))
        assertTrue(json.contains(""""pharmacokinetics":null"""))
        assertTrue(json.contains(""""pharmacology":null"""))
        assertTrue(json.contains(""""physicochemical_properties":null"""))
    }

    @Test
    fun `Drug serializes exactly 37 fields with snake_case keys`() {
        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(minimalDrug())).jsonObject
        assertEquals(37, jsonObject.size)
        val keyCasingViolations = jsonObject.keys.filter { key ->
            key != key.lowercase() || key.contains(Regex("[A-Z]"))
        }
        assertTrue(
            actual = keyCasingViolations.isEmpty(),
            message = "Non snake_case keys detected: $keyCasingViolations",
        )
    }

    private fun minimalDrug(): Drug =
        Drug(
            id = "drug_0001",
            genericName = "テスト一般名",
            brandName = "テスト販売名",
            brandNameKana = "テストハンバイメイ",
            atcCode = "N02BE01",
            therapeuticCategoryName = "経口鎮痛薬",
            regulatoryClass = listOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
            dosageForm = DosageForm.TABLET,
            routeOfAdministration = RouteOfAdministration.ORAL,
            composition = CompositionInfo(
                activeIngredient = "サンプルシン",
                activeIngredientAmount = Dose(amount = 100.0, unit = DoseUnit.MG, per = "1 錠中"),
                appearance = "白色の円形フィルムコーティング錠",
            ),
            contraindications = listOf(
                NumberedParagraph(order = 1, content = "本剤の成分に対し過敏症の既往歴のある患者"),
            ),
            indications = listOf(
                IndicationItem(order = 1, content = "各種疾患における鎮痛"),
            ),
            dosage = DosageInfo(standardDosage = "通常、成人には 1 回 100 mg を経口投与"),
            adverseReactions = AdverseReactionInfo(other = AdverseReactionByFrequency()),
            packages = listOf(
                PackageInfo(
                    size = "100 錠 (10 錠 × 10 PTP)",
                    storageCondition = StorageCondition(
                        temperature = StorageTemperature.ROOM_TEMPERATURE,
                        lightProtection = false,
                        moistureProtection = false,
                    ),
                    expirationMonths = 36,
                ),
            ),
            manufacturer = "架空製薬株式会社",
            revisedAt = "2024-03-01",
        )
}
