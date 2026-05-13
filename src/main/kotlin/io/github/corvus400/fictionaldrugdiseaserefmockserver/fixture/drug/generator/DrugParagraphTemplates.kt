package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator

enum class ParagraphField {
    WARNING_CONTENT,
    CONTRAINDICATION_CONTENT,
    INDICATION_CONTENT,
    INDICATION_RELATED_PRECAUTION,
    STANDARD_DOSAGE,
    AGE_DOSE,
    RENAL_DOSE,
    HEPATIC_DOSE,
    DOSAGE_RELATED_PRECAUTION,
    IMPORTANT_PRECAUTION,
    PRECAUTION_POPULATION_NOTE,
    INTERACTION_SYMPTOM,
    INTERACTION_MECHANISM,
    ADVERSE_SYMPTOM,
    ADVERSE_INITIAL_SIGNS,
    ADVERSE_COUNTERMEASURE,
    EFFECT_ON_LAB,
    OVERDOSE_SYMPTOMS,
    OVERDOSE_MANAGEMENT,
    ADMINISTRATION_PRECAUTION,
    OTHER_PRECAUTION,
    PHARMACOKINETICS_BLOOD,
    PHARMACOKINETICS_ABSORPTION,
    PHARMACOKINETICS_DISTRIBUTION,
    PHARMACOKINETICS_METABOLISM,
    PHARMACOKINETICS_EXCRETION,
    CLINICAL_RESULT_CONTENT,
    PHARMACOLOGY_MECHANISM,
    PHARMACOLOGY_EFFECT,
    PHYSICOCHEMICAL_DESCRIPTION,
    HANDLING_CONTENT,
    APPROVAL_CONDITION,
    INSURANCE_NOTE,
}

object DrugParagraphTemplates {
    val templates: Map<ParagraphField, List<String>> = mapOf(
        ParagraphField.WARNING_CONTENT to listOf(
            "本剤により重篤な{{adverseReaction}}が発現するおそれがあるため、投与開始前に{{exam}}を実施すること。 (架空)",
            "重篤な{{adverseReaction}}があらわれた場合には、直ちに投与を中止し適切な{{countermeasure}}を行うこと。 (架空)",
            "投与中は定期的に{{exam}}を行い、{{adverseReaction}}の早期発見に努めること。 (架空)",
        ),
        ParagraphField.CONTRAINDICATION_CONTENT to listOf(
            "本剤の成分に対し過敏症の既往歴のある患者には投与しないこと。 (架空)",
            "重篤な{{comorbidity}}のある患者には投与しないこと(症状を悪化させるおそれがある)。 (架空)",
            "{{drugCategory}}を投与中の患者には投与しないこと({{adverseReaction}}の発現を助長するおそれがある)。 (架空)",
        ),
        ParagraphField.INDICATION_CONTENT to listOf(
            "{{disease}}における各種症状の改善。 (架空)",
            "{{disease}}に起因する{{symptom}}の緩和。 (架空)",
            "{{disease}}及びその関連病態における補助療法。 (架空)",
        ),
        ParagraphField.INDICATION_RELATED_PRECAUTION to listOf(
            "{{disease}}の確定診断が行われた患者にのみ投与すること。 (架空)",
            "本剤の有効性は{{disease}}以外の病態では確立していないため、他の病態へは使用しないこと。 (架空)",
            "投与に先立ち、標準的な{{exam}}を実施して適応を慎重に判断すること。 (架空)",
        ),
        ParagraphField.STANDARD_DOSAGE to listOf(
            "通常、成人には**1 回 {{doseAmount}}** を 1 日 {{frequency}} 回食後経口投与する。 (架空)",
            "通常、成人には 1 日 {{totalDailyDose}} を {{frequency}} 回に分けて経口投与する。症状により適宜増減する。 (架空)",
            "通常、成人には {{doseAmount}} を {{interval}} ごとに投与する。年齢・症状により適宜調整する。 (架空)",
        ),
        ParagraphField.AGE_DOSE to listOf(
            "{{ageGroup}}には、通常 1 回 {{doseAmount}} を 1 日 {{frequency}} 回投与する。 (架空)",
            "{{ageGroup}}では体重に応じて**1 回 {{dosePerKg}}** を 1 日 {{frequency}} 回投与する。 (架空)",
            "{{ageGroup}}への投与量は {{referenceRange}} を基準に適宜調節する。 (架空)",
        ),
        ParagraphField.RENAL_DOSE to listOf(
            "{{renalLevel}} では 1 回投与量を通常量の {{reductionRatio}} とし、{{interval}} ごとに投与する。 (架空)",
            "{{renalLevel}} の患者には {{doseAmount}} を {{interval}} ごとに投与し、{{exam}} でモニターすること。 (架空)",
            "{{renalLevel}} には原則投与を推奨しない。やむを得ず投与する場合は {{exam}} を管理しながら慎重に投与する。 (架空)",
        ),
        ParagraphField.HEPATIC_DOSE to listOf(
            "{{hepaticLevel}} では初回用量を通常量の {{reductionRatio}} に減量する。 (架空)",
            "{{hepaticLevel}} では**1 回 {{doseAmount}}** を 1 日 {{frequency}} 回に減量し、{{exam}} 値を観察する。 (架空)",
            "{{hepaticLevel}} には原則投与を推奨しない。代謝遅延による {{adverseReaction}} のおそれがある。 (架空)",
        ),
        ParagraphField.DOSAGE_RELATED_PRECAUTION to listOf(
            "- {{exam}} の結果に応じて投与量を調整すること。 (架空)",
            "- 効果不十分な場合でも、1 日 {{maxDailyDose}} を超えないこと。 (架空)",
            "- 投与経路を変更する際は、{{adverseReaction}} の発現に注意しながら慎重に切り替えること。 (架空)",
        ),
        ParagraphField.IMPORTANT_PRECAUTION to listOf(
            "- 本剤投与による {{adverseReaction}} が報告されているため、投与中は定期的に {{exam}} を実施すること。 (架空)",
            "- 服用中に {{symptom}} が発現した場合は、ただちに医師に相談するよう患者に指導すること。 (架空)",
            "- 自動車の運転等、危険を伴う機械の操作時は {{symptom}} の発現に注意するよう指導すること。 (架空)",
        ),
        ParagraphField.PRECAUTION_POPULATION_NOTE to listOf(
            "{{populationCategory}} に対する本剤の安全性は確立していない。有益性が上回る場合にのみ投与する。 (架空)",
            "{{populationCategory}} への投与経験は限られている。{{exam}} を管理しながら慎重に投与する。 (架空)",
            "{{populationCategory}} では {{adverseReaction}} の発現頻度が高いとの報告があるため、慎重に投与する。 (架空)",
        ),
        ParagraphField.INTERACTION_SYMPTOM to listOf(
            "{{drugCategory}} との併用により {{adverseReaction}} が増強される可能性がある。 (架空)",
            "併用により {{symptom}} が発現又は悪化するおそれがある。 (架空)",
            "併用開始後数日以内に {{symptom}} が報告されている。 (架空)",
        ),
        ParagraphField.INTERACTION_MECHANISM to listOf(
            "{{drugCategory}} が代謝酵素 {{enzyme}} を阻害することにより、本剤の血中濃度が上昇する。 (架空)",
            "併用薬との蛋白結合置換により、本剤の遊離型分率が上昇し {{adverseReaction}} を招くおそれがある。 (架空)",
            "{{excretionRoute}} を介した排泄競合により本剤の消失半減期が延長する可能性がある。 (架空)",
        ),
        ParagraphField.ADVERSE_SYMPTOM to listOf(
            "投与開始後に {{symptom}} が発現することがある。 (架空)",
            "長期投与により {{symptom}} 及び {{labResult}} の変化が報告されている。 (架空)",
            "{{frequencyBand}} において {{symptom}} が観察されている。 (架空)",
        ),
        ParagraphField.ADVERSE_INITIAL_SIGNS to listOf(
            "初期症状として {{symptom}}、{{labResult}} の異常があらわれた場合は速やかに受診するよう指導すること。 (架空)",
            "発現時の徴候として {{symptom}} 及び {{labResult}} の変動が報告されている。 (架空)",
            "投与数日以内に {{symptom}} を呈した場合は、重篤な {{adverseReaction}} の前駆症状として扱うこと。 (架空)",
        ),
        ParagraphField.ADVERSE_COUNTERMEASURE to listOf(
            "症状発現時は投与を中止し、{{countermeasure}} を実施すること。 (架空)",
            "重症例では {{drugCategory}} の投与と {{exam}} による経過観察を行うこと。 (架空)",
            "{{symptom}} 持続時は速やかに専門医へ紹介し、{{countermeasure}} を検討する。 (架空)",
        ),
        ParagraphField.EFFECT_ON_LAB to listOf(
            "{{labResult}} が一過性に上昇することがあるため、投与中は {{exam}} を定期的に行うこと。 (架空)",
            "- {{labResult}} の変動が報告されている。 (架空)",
            "本剤の投与により {{labResult}} が見かけ上変動することがあるため、検査値の解釈には注意を要する。 (架空)",
        ),
        ParagraphField.OVERDOSE_SYMPTOMS to listOf(
            "過量投与により {{symptom}}、{{labResult}} の著明な変動が発現するおそれがある。 (架空)",
            "過剰投与時には急性の {{adverseReaction}} が報告されている。 (架空)",
            "過量摂取により {{labResult}} の変動を伴う {{symptom}} が発現しうる。 (架空)",
        ),
        ParagraphField.OVERDOSE_MANAGEMENT to listOf(
            "過量投与の疑いがある場合は、胃洗浄等の {{countermeasure}} を実施し、{{exam}} でモニタリングする。 (架空)",
            "対症療法を基本とし、必要に応じて {{drugCategory}} を用いた {{countermeasure}} を考慮する。 (架空)",
            "血液透析や強制利尿の有効性は確立していないため、支持療法を行いつつ {{exam}} を継続管理する。 (架空)",
        ),
        ParagraphField.ADMINISTRATION_PRECAUTION to listOf(
            "- {{route}} 以外の投与経路では本剤の有効性・安全性は確認されていない。 (架空)",
            "- 服用時は十分量の水で服用するよう患者に指導すること。 (架空)",
            "- 投与部位の {{symptom}} が発現した場合は投与部位の変更を検討すること。 (架空)",
        ),
        ParagraphField.OTHER_PRECAUTION to listOf(
            "長期投与時の安全性は {{studyDuration}} までの試験で確認されている。 (架空)",
            "本剤の投与により {{labResult}} に影響を及ぼす可能性があるため、検査値の解釈には留意すること。 (架空)",
            "海外市販後において {{adverseReaction}} の報告があるため、投与中は {{exam}} による観察を行うこと。 (架空)",
        ),
        ParagraphField.PHARMACOKINETICS_BLOOD to listOf(
            "健康成人に {{doseAmount}} を単回経口投与したとき、Cmax は {{cmax}}、T1/2 は {{halfLife}} であった。 (架空)",
            "反復経口投与において {{durationDays}} 日目に定常状態に達し、AUC は {{auc}} を示した。 (架空)",
            "投与後の血中濃度推移は {{modelType}}モデルで良好に記述される。 (架空)",
        ),
        ParagraphField.PHARMACOKINETICS_ABSORPTION to listOf(
            "経口投与後、速やかに消化管から吸収され、{{tmax}} で最高血中濃度に到達する。 (架空)",
            "絶対バイオアベイラビリティは**{{bioavailability}}**である。 (架空)",
            "食事の影響により吸収量が {{foodEffectRatio}} 変動するため、服用タイミングに注意する。 (架空)",
        ),
        ParagraphField.PHARMACOKINETICS_DISTRIBUTION to listOf(
            "分布容積は {{volumeOfDistribution}} であり、組織移行性は中等度である。 (架空)",
            "血漿蛋白結合率は**{{proteinBinding}}**であり、{{drugCategory}} との置換に留意する。 (架空)",
            "中枢移行は限定的で、脳脊髄液中濃度は血中濃度の {{cnsRatio}} であった。 (架空)",
        ),
        ParagraphField.PHARMACOKINETICS_METABOLISM to listOf(
            "主として {{enzyme}} により代謝され、主代謝物は {{metabolite}} である。 (架空)",
            "{{enzyme}} の遺伝子多型により代謝速度が個体差を示す可能性がある。 (架空)",
            "活性代謝物は確認されておらず、代謝物はいずれも薬理活性を有しない。 (架空)",
        ),
        ParagraphField.PHARMACOKINETICS_EXCRETION to listOf(
            "投与量の {{urinaryExcretionRatio}} が尿中に未変化体として排泄される。 (架空)",
            "主として {{excretionRoute}} を介して排泄され、消失半減期は {{halfLife}} である。 (架空)",
            "糞便中排泄率は {{fecalExcretionRatio}} であり、腎機能低下時は用量調節を考慮する。 (架空)",
        ),
        ParagraphField.CLINICAL_RESULT_CONTENT to listOf(
            "{{disease}} 患者 {{patientCount}} 例を対象とした二重盲検比較試験で有効率は**{{efficacyRate}}**であった。 (架空)",
            "{{disease}} に対するプラセボ対照試験で、{{endpoint}} に関して有意な改善が認められた (p {{pValue}})。 (架空)",
            "長期投与試験では {{durationWeeks}} 週後の効果持続率は {{retentionRate}} であった。 (架空)",
        ),
        ParagraphField.PHARMACOLOGY_MECHANISM to listOf(
            "本剤は {{targetMolecule}} に選択的に結合し、その {{action}} を介して薬理作用を発揮する。 (架空)",
            "本剤は {{pathway}}を抑制することで {{effect}} を示すと考えられる。 (架空)",
            "本剤は {{mechanism}} を介した作用により、{{disease}} の病態進行を抑制する。 (架空)",
        ),
        ParagraphField.PHARMACOLOGY_EFFECT to listOf(
            "動物試験において、本剤は {{modelName}}で用量依存的に {{effect}} を示した。 (架空)",
            "In vitro において {{targetMolecule}} に対する IC50 は {{ic50}} であった。 (架空)",
            "ヒト由来 {{tissueType}} を用いた試験では、本剤は {{effect}} を濃度依存的に増強した。 (架空)",
        ),
        ParagraphField.PHYSICOCHEMICAL_DESCRIPTION to listOf(
            "本剤原体は白色から微黄色の結晶性粉末であり、{{solventPolarity}} 溶媒に易溶、水にはほとんど溶けない。 (架空)",
            "融点は**{{meltingPoint}}**であり、光及び湿度により経時的に変化する可能性がある。 (架空)",
            "pKa は {{pKa}} であり、生理的 pH 領域において主として {{ionizationForm}} 形で存在する。 (架空)",
        ),
        ParagraphField.HANDLING_CONTENT to listOf(
            "- 調製後は速やかに使用し、{{storageTemperature}} で {{stableDuration}} 以内に使用すること。 (架空)",
            "- 光により分解するため、遮光した状態で保存すること。 (架空)",
            "- {{packageSize}} 単位での取り扱いを原則とし、分割使用時は清潔操作のもとで行うこと。 (架空)",
        ),
        ParagraphField.APPROVAL_CONDITION to listOf(
            "本剤の安全性に関する {{postMarketingPlan}} を市販後に実施すること。 (架空)",
            "本剤投与に際しては、適切な施設要件を満たす医療機関において {{exam}} を管理下で行うこと。 (架空)",
            "本剤の適正使用に関して医師等を対象とした {{trainingProgram}} を実施し、適切な情報提供を行うこと。 (架空)",
        ),
        ParagraphField.INSURANCE_NOTE to listOf(
            "本剤の保険請求にあたっては、{{disease}} の確定診断根拠を診療録に記載すること。 (架空)",
            "本剤の投与期間は 1 回につき {{reimbursementDurationDays}} 日を目安とする。 (架空)",
            "他剤との併用請求については {{insuranceRule}} に従うこと。 (架空)",
        ),
    )

    fun pickTemplate(
        field: ParagraphField,
        seed: Long,
    ): String {
        val candidates = templates[field]
            ?: error("No templates registered for field: $field")
        return ValueRangeGenerator.pickOne(seed = seed, candidates = candidates)
    }
}
