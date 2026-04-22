package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator

enum class DiseaseParagraphField {
    OVERVIEW_DESCRIPTION,
    SYMPTOM_DESCRIPTION,
    DIAGNOSTIC_CRITERIA_DESCRIPTION,
    TREATMENT_DESCRIPTION,
    PROGNOSIS_DESCRIPTION,
    EPIDEMIOLOGY_DESCRIPTION,
    SEVERITY_DESCRIPTION,
}

object DiseaseParagraphTemplates {
    val templates: Map<DiseaseParagraphField, List<String>> = mapOf(
        DiseaseParagraphField.OVERVIEW_DESCRIPTION to listOf(
            "{{disease}}は、{{mainFeature}}を特徴とする{{chronicity}}疾患である。{{ageGroup}}に好発し、近年診断技術の進歩により早期発見例が増加している。",
            "{{disease}}は{{organSystem}}に発現する病態で、{{onsetPattern}}の経過を呈する。推定有病率は人口 10 万対 {{prevalenceRate}} (架空)。",
            "{{disease}}は{{etiologyCategory}}に分類される疾患群で、{{mainSymptom}}を主訴に受診する例が多い。確定診断には**{{diagnosticTest}}** を用いる。",
        ),
        DiseaseParagraphField.SYMPTOM_DESCRIPTION to listOf(
            "本疾患の主症状は{{mainSymptom}}であり、{{associatedSymptom}}を随伴することが多い。発症様式は{{onsetPattern}}を示す。",
            "典型例では{{mainSymptom}}を認め、進行例では{{progressedSymptom}}を伴う。\n- {{associatedSymptom}}\n- {{additionalSymptom}}",
            "初期症状は{{initialSymptom}}が中心であり、経過とともに{{progressedSymptom}}へ移行する。{{symptomTriggerCondition}}下で増悪する傾向がある。",
        ),
        DiseaseParagraphField.DIAGNOSTIC_CRITERIA_DESCRIPTION to listOf(
            "本基準は架空値であり、実臨床の診断には使用しない。**{{primaryFinding}}** を満たし、かつ{{supportingFindingCount}} 項目以上を充足する場合に確定診断とする。",
            "{{diagnosticTest}} による所見と{{clinicalFinding}} の組み合わせで判定する。除外診断として{{differentialCondition}}を除外することが必要。",
            "下記の項目を総合して判定する。架空疾患のため数値は参考値である。\n- 必須項目: {{primaryFinding}}\n- 補助項目: {{supportingFinding}}",
        ),
        DiseaseParagraphField.TREATMENT_DESCRIPTION to listOf(
            "標準治療は{{treatmentCategory}}を中心とし、症状に応じて{{adjunctTreatment}}を併用する。治療開始から{{evaluationDuration}} 後に効果判定を行う。",
            "急性期には{{acuteTreatment}}を実施し、安定期へ移行後は{{maintenanceTreatment}}による維持療法を継続する。",
            "{{treatmentCategory}} を第一選択とする。効果不十分例では{{secondLineTreatment}}を追加し、改善が乏しい場合は{{specialistReferral}}へ紹介する。",
        ),
        DiseaseParagraphField.PROGNOSIS_DESCRIPTION to listOf(
            "早期発見により予後は良好である。診断 5 年後の{{prognosisIndicator}} は**約 {{prognosisRate}}** と報告される (架空)。",
            "未治療例では{{progressedComplication}}を合併する例が散見されるため、定期的な{{followUpExam}} による経過観察が重要である。",
            "本疾患の予後は{{prognosticFactor}} に大きく左右される。**{{favorableFactor}}** を満たす症例では{{favorableOutcome}} が期待できる。",
        ),
        DiseaseParagraphField.EPIDEMIOLOGY_DESCRIPTION to listOf(
            "本邦における推定有病率は{{prevalenceLabel}} であり、{{ageGroup}} に好発する。男女比は **{{sexRatio}}** で{{sexDominance}}を示す (架空値)。",
            "発症リスク因子として{{riskFactor}} が挙げられる。地域差は明らかでないが、{{regionalNote}}との報告がある (架空)。",
            "年間新規発症数は{{annualIncidence}} と推計される (架空値)。発症ピークは{{peakAgeYears}} 代であり、{{seasonalNote}} の傾向を示す。",
        ),
        DiseaseParagraphField.SEVERITY_DESCRIPTION to listOf(
            "重症度分類は{{gradingSystem}}に準拠し、**{{gradeCount}} 段階**で評価する。各段階に応じて治療強度を調整する。",
            "{{gradingSystem}} による評価で{{severeGradeLabel}} 以上に該当する場合は、入院加療を考慮する。",
            "重症度の判定には{{severityIndicator}} を用いる。{{severityThreshold}} を超える例では{{escalatedAction}} を推奨する (架空基準)。",
        ),
    )

    fun pickTemplate(
        field: DiseaseParagraphField,
        seed: Long,
    ): String {
        val candidates = templates[field]
            ?: error("No templates registered for field: $field")
        return ValueRangeGenerator.pickOne(seed = seed, candidates = candidates)
    }
}
