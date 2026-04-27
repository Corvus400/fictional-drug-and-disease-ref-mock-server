package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.PrevalenceUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseNestedSerializationTest {
    @Test
    fun `SymptomInfo serializes camelCase fields to snake_case JSON keys`() {
        val json = AppJson.encodeToString(
            SymptomInfo(
                mainSymptoms = listOf("発熱", "咳"),
                associatedSymptoms = listOf("倦怠感"),
                onsetPattern = OnsetPattern.ACUTE,
            ),
        )
        assertEquals(
            """{"main_symptoms":["発熱","咳"],"associated_symptoms":["倦怠感"],"onset_pattern":"acute"}""",
            json,
        )
    }

    @Test
    fun `Exam serializes camelCase fields to snake_case JSON keys`() {
        val json = AppJson.encodeToString(
            Exam(
                name = "HbA1c 相当値",
                category = ExamCategory.BLOOD_TEST,
                typicalFinding = "6.5% 以上で高値",
                referenceRange = "4.6-6.2%",
            ),
        )
        assertEquals(
            """{"name":"HbA1c 相当値","category":"blood_test",""" +
                """"typical_finding":"6.5% 以上で高値","reference_range":"4.6-6.2%"}""",
            json,
        )
    }

    @Test
    fun `DiagnosticCriteriaInfo serializes required supporting and notes fields`() {
        val json = AppJson.encodeToString(
            DiagnosticCriteriaInfo(
                required = listOf("空腹時サンプル値**≧ 126 mg/dL**"),
                supporting = listOf("HbA1c 相当値**≧ 6.5%**"),
                notes = "本基準は架空値である。",
            ),
        )
        assertEquals(
            """{"required":["空腹時サンプル値**≧ 126 mg/dL**"],""" +
                """"supporting":["HbA1c 相当値**≧ 6.5%**"],""" +
                """"notes":"本基準は架空値である。"}""",
            json,
        )
    }

    @Test
    fun `SeverityInfo serializes nested grades with camelCase to snake_case keys`() {
        val json = AppJson.encodeToString(
            SeverityInfo(
                gradingSystem = "NYHA 分類",
                grades = listOf(
                    Grade(
                        label = "I 度",
                        criteria = "日常活動で症状なし",
                        recommendedAction = "生活指導のみで経過観察する。",
                    ),
                    Grade(
                        label = "II 度",
                        criteria = "軽度の労作で症状あり",
                        recommendedAction = "薬物療法を開始する。",
                    ),
                ),
            ),
        )
        assertEquals(
            """{"grading_system":"NYHA 分類",""" +
                """"grades":[""" +
                """{"label":"I 度","criteria":"日常活動で症状なし",""" +
                """"recommended_action":"生活指導のみで経過観察する。"},""" +
                """{"label":"II 度","criteria":"軽度の労作で症状あり",""" +
                """"recommended_action":"薬物療法を開始する。"}""" +
                """]}""",
            json,
        )
    }

    @Test
    fun `EpidemiologyInfo serializes nested prevalence sex ratio and onset age range`() {
        val json = AppJson.encodeToString(
            EpidemiologyInfo(
                prevalence = Prevalence(
                    rate = 12.5,
                    denominator = 100000,
                    unit = PrevalenceUnit.PER_POPULATION,
                    label = "人口10万対 12.5",
                ),
                onsetAgeRange = OnsetAgeRange(
                    minAgeYears = 40,
                    maxAgeYears = 60,
                    label = "40-60 代",
                ),
                sexRatio = SexDistribution(
                    maleRatio = 2,
                    femaleRatio = 1,
                    note = "男性優位",
                ),
                riskFactors = listOf("喫煙", "肥満"),
            ),
        )
        assertEquals(
            """{"prevalence":{"rate":12.5,"denominator":100000,"unit":"人口対",""" +
                """"label":"人口10万対 12.5"},""" +
                """"onset_age_range":{"min_age_years":40,"max_age_years":60,"label":"40-60 代"},""" +
                """"sex_ratio":{"male_ratio":2,"female_ratio":1,"note":"男性優位"},""" +
                """"risk_factors":["喫煙","肥満"]}""",
            json,
        )
    }

    @Test
    fun `TreatmentInfo serializes pharmacological non pharmacological and acute phase protocol lists`() {
        val json = AppJson.encodeToString(
            TreatmentInfo(
                pharmacological = listOf(
                    PharmaTreatment(
                        drugCategory = "経口血糖降下薬",
                        drugIds = listOf("drug_0001"),
                        indication = "HbA1c 相当値高値例",
                        notes = "第一選択薬として本剤を使用する。",
                    ),
                ),
                nonPharmacological = listOf(
                    TreatmentSection(
                        heading = "食事療法",
                        items = listOf("糖質制限"),
                        description = "標準体重 × 25 kcal/kg を目安とする。",
                    ),
                ),
                acutePhaseProtocol = listOf(
                    ProtocolStep(
                        order = 1,
                        action = "初期輸液として生理食塩液**500 mL/h** を開始する。",
                        target = "収縮期血圧 140 mmHg 未満",
                    ),
                ),
            ),
        )
        assertEquals(
            """{"pharmacological":[{"drug_category":"経口血糖降下薬","drug_ids":["drug_0001"],""" +
                """"indication":"HbA1c 相当値高値例","notes":"第一選択薬として本剤を使用する。"}],""" +
                """"non_pharmacological":[{"heading":"食事療法","items":["糖質制限"],""" +
                """"description":"標準体重 × 25 kcal/kg を目安とする。"}],""" +
                """"acute_phase_protocol":[{"order":1,""" +
                """"action":"初期輸液として生理食塩液**500 mL/h** を開始する。",""" +
                """"target":"収縮期血圧 140 mmHg 未満"}]}""",
            json,
        )
    }
}
