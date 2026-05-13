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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugSerializationTest {
    @Test
    fun `Drug serializes required identification and naming fields in snake_case`() {
        val drug = minimalDrug()
        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(drug)).jsonObject
        val actual = listOf(
            "id",
            "generic_name",
            "brand_name",
            "brand_name_kana",
            "atc_code",
            "therapeutic_category_name",
            "manufacturer",
            "revised_at",
            "image_url",
        ).associateWith { key -> jsonObject[key]?.jsonPrimitive?.content }

        assertEquals(
            expected = mapOf(
                "id" to "drug_0001",
                "generic_name" to "テスト一般名",
                "brand_name" to "テスト販売名",
                "brand_name_kana" to "テストハンバイメイ",
                "atc_code" to "N02BE01",
                "therapeutic_category_name" to "経口鎮痛薬",
                "manufacturer" to "架空製薬株式会社",
                "revised_at" to "2024-03-01",
                "image_url" to "/v1/images/dosage-forms/tablet?size=Original",
            ),
            actual = actual,
        )
    }

    @Test
    fun `Drug serializes classification enums with Japanese SerialName values`() {
        val json = AppJson.encodeToString(minimalDrug())
        val missingFragments = listOf(
            """"regulatory_class":["prescription_required"]""",
            """"dosage_form":"tablet"""",
            """"route_of_administration":"oral"""",
        ).filterNot { json.contains(it) }

        assertTrue(
            actual = missingFragments.isEmpty(),
            message = "Drug classification enum serialization is missing fragments: $missingFragments",
        )
    }

    @Test
    fun `Drug serializes required nested objects recursively`() {
        val json = AppJson.encodeToString(minimalDrug())
        val missingFragments = listOf(
            """"composition":{"active_ingredient":"サンプルシン"""",
            """"dosage":{"standard_dosage":"通常、成人には 1 回 100 mg を経口投与"""",
            """"adverse_reactions":{"serious":[],"other":{""",
            """"packages":[{"size":"100 錠 (10 錠 × 10 PTP)"""",
        ).filterNot { json.contains(it) }

        assertTrue(
            actual = missingFragments.isEmpty(),
            message = "Drug required nested serialization is missing fragments: $missingFragments",
        )
    }

    @Test
    fun `Drug serializes required contraindications and indications lists`() {
        val json = AppJson.encodeToString(minimalDrug())
        val missingFragments = listOf(
            """"contraindications":[{"order":1,"sub_order":null,"content":"本剤の成分に対し過敏症の既往歴のある患者"}]""",
            """"indications":[{"order":1,"content":"各種疾患における鎮痛"}]""",
        ).filterNot { json.contains(it) }

        assertTrue(
            actual = missingFragments.isEmpty(),
            message = "Drug required list serialization is missing fragments: $missingFragments",
        )
    }

    @Test
    fun `Drug serializes optional list fields as empty array by default`() {
        val json = AppJson.encodeToString(minimalDrug())
        val missingEmptyArrays = listOf(
            "warning",
            "indications_related_precautions",
            "dosage_related_precautions",
            "important_precautions",
            "precautions_for_specific_populations",
            "effects_on_lab_tests",
            "administration_precautions",
            "other_precautions",
            "clinical_results",
            "handling_precautions",
            "approval_conditions",
            "references",
            "insurance_notes",
            "related_disease_ids",
        ).filterNot { field -> json.contains(""""$field":[]""") }

        assertTrue(
            actual = missingEmptyArrays.isEmpty(),
            message = "Drug optional list fields must serialize as empty arrays: $missingEmptyArrays",
        )
    }

    @Test
    fun `Drug serializes nullable optional fields as null by default`() {
        val json = AppJson.encodeToString(minimalDrug())
        val missingNulls = listOf(
            "yj_code",
            "interactions",
            "overdose",
            "pharmacokinetics",
            "pharmacology",
            "physicochemical_properties",
        ).filterNot { field -> json.contains(""""$field":null""") }

        assertTrue(
            actual = missingNulls.isEmpty(),
            message = "Drug nullable optional fields must serialize as null: $missingNulls",
        )
    }

    @Test
    fun `Drug serializes exactly 39 fields with snake_case keys`() {
        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(minimalDrug())).jsonObject
        val violations = buildList {
            if (jsonObject.size != 39) {
                add("expected 39 fields but was ${jsonObject.size}")
            }
            addAll(
                jsonObject.keys
                    .filter { key -> key != key.lowercase() || key.contains(Regex("[A-Z]")) }
                    .map { key -> "Non snake_case key detected: $key" },
            )
        }

        assertTrue(
            actual = violations.isEmpty(),
            message = "Drug field shape violations: $violations",
        )
    }

    @Test
    fun `Drug serializes drug override image_url for drug_0089`() {
        val drug = minimalDrug(id = "drug_0089")
        val jsonObject = Json.parseToJsonElement(AppJson.encodeToString(drug)).jsonObject

        assertEquals("/v1/images/drugs/drug_0089?size=Original", jsonObject["image_url"]?.toString()?.trim('"'))
    }

    @Test
    fun `Drug deserialize without image_url evaluates default imageUrl`() {
        val json = AppJson.encodeToString(minimalDrug()).replace(
            ""","image_url":"/v1/images/dosage-forms/tablet?size=Original"""",
            "",
        )

        val drug = AppJson.decodeFromString<Drug>(json)

        assertEquals("/v1/images/dosage-forms/tablet?size=Original", drug.imageUrl)
    }

    private fun minimalDrug(id: String = "drug_0001"): Drug =
        Drug(
            id = id,
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
