package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.HepaticSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.PrecautionPopulationCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RenalSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugNestedSerializationTest {
    @Test
    fun `NumberedParagraph serializes subOrder as snake_case key sub_order`() {
        val json = AppJson.encodeToString(
            NumberedParagraph(order = 1, subOrder = 2, content = "本剤"),
        )
        assertEquals(
            """{"order":1,"sub_order":2,"content":"本剤"}""",
            json,
        )
    }

    @Test
    fun `IndicationItem serializes order and content`() {
        val json = AppJson.encodeToString(
            IndicationItem(order = 1, content = "各種疾患における鎮痛"),
        )
        assertEquals(
            """{"order":1,"content":"各種疾患における鎮痛"}""",
            json,
        )
    }

    @Test
    fun `CompositionInfo serializes camelCase fields to snake_case JSON keys`() {
        val json = AppJson.encodeToString(
            CompositionInfo(
                activeIngredient = "サンプルシン",
                activeIngredientAmount = Dose(amount = 100.0, unit = DoseUnit.MG, per = "1 錠中"),
                inactiveIngredients = listOf("乳糖", "結晶セルロース"),
                appearance = "白色の円形フィルムコーティング錠",
                identificationCode = "SMP100",
            ),
        )
        assertEquals(
            """{"active_ingredient":"サンプルシン","active_ingredient_amount":""" +
                """{"amount":100.0,"unit":"mg","per":"1 錠中"},""" +
                """"inactive_ingredients":["乳糖","結晶セルロース"],""" +
                """"appearance":"白色の円形フィルムコーティング錠",""" +
                """"identification_code":"SMP100"}""",
            json,
        )
    }

    @Test
    fun `DosageInfo serializes camelCase list fields to snake_case JSON keys`() {
        val json = AppJson.encodeToString(
            DosageInfo(
                standardDosage = "通常、成人には**1 回 100 mg**を経口投与",
                ageSpecificDosage = listOf(
                    AgeDosage(
                        range = AgeRange(minAgeMonths = 72, maxAgeMonths = 144, label = "6 歳以上 12 歳未満"),
                        dose = "通常用量の 1/2",
                    ),
                ),
                renalAdjustment = listOf(
                    RenalDose(
                        range = CrClRange(
                            minMlPerMin = 30,
                            maxMlPerMin = 59,
                            severity = RenalSeverity.MODERATE,
                            label = "30-59 mL/min",
                        ),
                        dose = "通常用量の 1/2 に減量",
                    ),
                ),
                hepaticAdjustment = listOf(
                    HepaticDose(severity = HepaticSeverity.SEVERE, dose = "投与回避"),
                ),
            ),
        )
        assertEquals(
            """{"standard_dosage":"通常、成人には**1 回 100 mg**を経口投与",""" +
                """"age_specific_dosage":[{"range":""" +
                """{"min_age_months":72,"max_age_months":144,"label":"6 歳以上 12 歳未満"},""" +
                """"dose":"通常用量の 1/2"}],""" +
                """"renal_adjustment":[{"range":""" +
                """{"min_ml_per_min":30,"max_ml_per_min":59,"severity":"moderate","label":"30-59 mL/min"},""" +
                """"dose":"通常用量の 1/2 に減量"}],""" +
                """"hepatic_adjustment":[{"severity":"severe","dose":"投与回避"}]}""",
            json,
        )
    }

    @Test
    fun `PrecautionPopulation serializes category enum with Japanese SerialName`() {
        val json = AppJson.encodeToString(
            PrecautionPopulation(
                category = PrecautionPopulationCategory.PREGNANT,
                note = "妊婦には投与しないこと。",
            ),
        )
        assertEquals(
            """{"category":"pregnant","note":"妊婦には投与しないこと。"}""",
            json,
        )
    }

    @Test
    fun `InteractionInfo serializes camelCase fields to snake_case JSON keys`() {
        val json = AppJson.encodeToString(
            InteractionInfo(
                combinationProhibited = listOf(
                    InteractionEntry(
                        drugId = "drug_0001",
                        displayName = "他の NSAIDs",
                        clinicalSymptom = "腎機能障害の増悪",
                        mechanism = "プロスタグランジン合成阻害の増強",
                    ),
                ),
                combinationCaution = emptyList(),
            ),
        )
        assertEquals(
            """{"combination_prohibited":[""" +
                """{"drug_id":"drug_0001","display_name":"他の NSAIDs",""" +
                """"clinical_symptom":"腎機能障害の増悪","mechanism":"プロスタグランジン合成阻害の増強"}],""" +
                """"combination_caution":[]}""",
            json,
        )
    }

    @Test
    fun `AdverseReactionInfo serializes frequency band enum and snake_case frequency fields`() {
        val json = AppJson.encodeToString(
            AdverseReactionInfo(
                serious = listOf(
                    AdverseReaction(
                        name = "肝機能障害",
                        frequency = FrequencyBand.UNDER_1_PERCENT,
                        symptom = "AST、ALT 著明上昇",
                        initialSigns = "倦怠感",
                        countermeasure = "投与中止",
                    ),
                ),
                other = AdverseReactionByFrequency(
                    over5Percent = listOf("悪心"),
                    between1And5Percent = listOf("発疹"),
                    under1Percent = emptyList(),
                    frequencyUnknown = listOf("眠気"),
                ),
            ),
        )
        assertEquals(
            """{"serious":[{"name":"肝機能障害","frequency":"under_1_percent","symptom":"AST、ALT 著明上昇",""" +
                """"initial_signs":"倦怠感","countermeasure":"投与中止"}],""" +
                """"other":{"over5_percent":["悪心"],"between1_and5_percent":["発疹"],""" +
                """"under1_percent":[],"frequency_unknown":["眠気"]}}""",
            json,
        )
    }

    @Test
    fun `OverdoseInfo serializes symptoms and management fields`() {
        val json = AppJson.encodeToString(
            OverdoseInfo(symptoms = "傾眠、めまい", management = "対症療法"),
        )
        assertEquals(
            """{"symptoms":"傾眠、めまい","management":"対症療法"}""",
            json,
        )
    }

    @Test
    fun `PharmacokineticsInfo serializes camelCase fields to snake_case JSON keys`() {
        val json = AppJson.encodeToString(
            PharmacokineticsInfo(
                bloodConcentration = "Cmax は 2 時間",
                absorption = null,
                distribution = null,
                metabolism = "肝代謝",
                excretion = "尿中排泄",
                parameters = listOf(PkParameter(name = "Cmax", value = "4.5 μg/mL")),
            ),
        )
        assertEquals(
            """{"blood_concentration":"Cmax は 2 時間","absorption":null,"distribution":null,""" +
                """"metabolism":"肝代謝","excretion":"尿中排泄",""" +
                """"parameters":[{"name":"Cmax","value":"4.5 μg/mL"}]}""",
            json,
        )
    }

    @Test
    fun `PharmacologyInfo serializes mechanism and effect fields`() {
        val json = AppJson.encodeToString(
            PharmacologyInfo(
                mechanism = "サンプル受容体を選択的に阻害",
                effect = "鎮痛作用",
            ),
        )
        assertEquals(
            """{"mechanism":"サンプル受容体を選択的に阻害","effect":"鎮痛作用"}""",
            json,
        )
    }

    @Test
    fun `PhysicochemicalInfo serializes camelCase fields to snake_case JSON keys`() {
        val json = AppJson.encodeToString(
            PhysicochemicalInfo(
                genericNameEnglish = "Samplecine",
                molecularFormula = "C16H20N2O",
                molecularWeight = 256.34,
                description = "白色の結晶性粉末",
            ),
        )
        assertEquals(
            """{"generic_name_english":"Samplecine","molecular_formula":"C16H20N2O",""" +
                """"molecular_weight":256.34,"description":"白色の結晶性粉末"}""",
            json,
        )
    }

    @Test
    fun `ClinicalResultSection serializes heading and content fields`() {
        val json = AppJson.encodeToString(
            ClinicalResultSection(
                heading = "有効性",
                content = "第 III 相試験において有効率は **72%**",
            ),
        )
        assertEquals(
            """{"heading":"有効性","content":"第 III 相試験において有効率は **72%**"}""",
            json,
        )
    }

    @Test
    fun `PackageInfo serializes nested storage condition with snake_case keys`() {
        val json = AppJson.encodeToString(
            PackageInfo(
                size = "100 錠 (10 錠 × 10 PTP)",
                storageCondition = StorageCondition(
                    temperature = StorageTemperature.ROOM_TEMPERATURE,
                    lightProtection = true,
                    moistureProtection = false,
                    additionalNote = "凍結を避ける",
                ),
                expirationMonths = 36,
            ),
        )
        assertEquals(
            """{"size":"100 錠 (10 錠 × 10 PTP)",""" +
                """"storage_condition":{"temperature":"room_temperature","light_protection":true,""" +
                """"moisture_protection":false,"additional_note":"凍結を避ける"},""" +
                """"expiration_months":36}""",
            json,
        )
    }

    @Test
    fun `Reference serializes citation and source fields`() {
        val json = AppJson.encodeToString(
            Reference(citation = "架空の文献 A", source = "架空医学雑誌"),
        )
        assertEquals(
            """{"citation":"架空の文献 A","source":"架空医学雑誌"}""",
            json,
        )
    }
}
