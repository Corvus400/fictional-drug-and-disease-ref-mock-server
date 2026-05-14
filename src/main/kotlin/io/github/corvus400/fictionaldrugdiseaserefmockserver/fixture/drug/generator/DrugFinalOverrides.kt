package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.IsoDateFormatter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.HepaticSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.PrecautionPopulationCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RenalSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReaction
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionByFrequency
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AgeDosage
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AgeRange
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.ClinicalResultSection
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CrClRange
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.DosageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Dose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.HepaticDose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.IndicationItem
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.InteractionEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.InteractionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.NumberedParagraph
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.OverdoseInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PackageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PharmacokineticsInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PharmacologyInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PhysicochemicalInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PkParameter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PrecautionPopulation
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Reference
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.RenalDose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.StorageCondition
import java.time.LocalDate

internal val DRUG_FINAL_OVERRIDES: Map<String, (Drug) -> Drug> =
    mapOf(
        "drug_0080" to ::tredecimFinalOverride,
        "drug_0089" to ::arisaSleepAidFinalOverride,
    )

internal val DRUG_RELATED_DISEASE_IDS_FINAL_OVERRIDE_IDS: Set<String> = setOf("drug_0080", "drug_0089")

private fun arisaSleepAidFinalOverride(generated: Drug): Drug =
    generated.copy(
        composition =
        generated.composition.copy(
            activeIngredientAmount = Dose(amount = 5.0, unit = DoseUnit.MG, per = "1 mL 中"),
            appearance = "青色澄明の液体を充填した透明ガラスバイアル (蓋に蝶のエンボス加工付、白色ラベル) (架空)",
        ),
        warning =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "過量投与により深い昏睡を来すおそれがあるため、服用記録を厳格に管理すること (架空)",
            ),
        ),
        contraindications =
        listOf(
            NumberedParagraph(order = 1, content = "重症呼吸機能障害のある患者 (架空)"),
            NumberedParagraph(order = 2, content = "中枢神経抑制剤を過量服用中の患者 (架空)"),
            NumberedParagraph(order = 3, content = "本剤の成分に対し過敏症の既往歴のある患者 (架空)"),
        ),
        indications = listOf(IndicationItem(order = 1, content = "不眠症 (架空)")),
        indicationsRelatedPrecautions =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "不眠の原因となる基礎疾患の検索を優先し、漫然と投与しないこと (架空)",
            ),
        ),
        dosage =
        DosageInfo(
            standardDosage = "通常、成人には就寝直前に 1 回 1 mL を経口投与する (架空)",
            ageSpecificDosage =
            listOf(
                AgeDosage(
                    range = AgeRange(minAgeMonths = 780, maxAgeMonths = null, label = "65 歳以上"),
                    dose = "初回は 0.5 mL から開始し、状態を観察しながら慎重に投与する (架空)",
                ),
            ),
            renalAdjustment =
            listOf(
                RenalDose(
                    range =
                    CrClRange(
                        minMlPerMin = null,
                        maxMlPerMin = 29,
                        severity = RenalSeverity.SEVERE,
                        label = "30 mL/min 未満、重度腎機能低下",
                    ),
                    dose = "投与間隔を延長し、過鎮静に注意する (架空)",
                ),
            ),
            hepaticAdjustment =
            listOf(
                HepaticDose(
                    severity = HepaticSeverity.MODERATE,
                    dose = "0.5 mL から開始し、翌朝の持ち越し効果を確認する (架空)",
                ),
            ),
        ),
        dosageRelatedPrecautions =
        listOf(
            NumberedParagraph(order = 1, content = "服用後は自動車の運転等危険を伴う機械の操作をしないこと (架空)"),
            NumberedParagraph(order = 2, content = "アルコール及び他の中枢神経抑制剤との併用で作用が増強される (架空)"),
        ),
        importantPrecautions =
        listOf(
            NumberedParagraph(order = 1, content = "過量服用時は 24 時間以内の救命処置を要することがある (架空)"),
            NumberedParagraph(order = 2, content = "翌朝の眠気、注意力低下及びふらつきに注意すること (架空)"),
        ),
        precautionsForSpecificPopulations =
        listOf(
            PrecautionPopulation(
                category = PrecautionPopulationCategory.GERIATRIC,
                note = "高齢者では転倒、せん妄及び持ち越し効果があらわれやすい (架空)",
            ),
            PrecautionPopulation(
                category = PrecautionPopulationCategory.PREGNANT,
                note = "妊婦又は妊娠している可能性のある女性には治療上の有益性を慎重に判断する (架空)",
            ),
            PrecautionPopulation(
                category = PrecautionPopulationCategory.LACTATING,
                note = "授乳中の投与は避け、やむを得ず投与する場合は授乳を中止する (架空)",
            ),
            PrecautionPopulation(
                category = PrecautionPopulationCategory.PEDIATRIC,
                note = "小児等を対象とした安全性は確立していない (架空)",
            ),
        ),
        interactions =
        InteractionInfo(
            combinationProhibited =
            listOf(
                InteractionEntry(
                    displayName = "アルコール大量併用",
                    clinicalSymptom = "深い鎮静又は昏睡が増強されるおそれがある (架空)",
                    mechanism = "中枢神経抑制作用が相加的に増強される (架空)",
                ),
            ),
            combinationCaution =
            listOf(
                InteractionEntry(
                    displayName = "ベンゾジアゼピン系薬剤",
                    clinicalSymptom = "眠気、ふらつき及び呼吸抑制が増強されるおそれがある (架空)",
                    mechanism = "GABA 作動性の抑制作用が重なる (架空)",
                ),
                InteractionEntry(
                    displayName = "オピオイド鎮痛薬",
                    clinicalSymptom = "呼吸抑制及び意識障害が増強されるおそれがある (架空)",
                    mechanism = "呼吸中枢抑制作用が相加的に増強される (架空)",
                ),
            ),
        ),
        adverseReactions =
        AdverseReactionInfo(
            serious =
            listOf(
                AdverseReaction(
                    name = "呼吸抑制",
                    frequency = FrequencyBand.UNKNOWN,
                    symptom = "過鎮静に伴い呼吸数低下を来すことがある (架空)",
                    initialSigns = "強い眠気、呼びかけへの反応低下、浅い呼吸 (架空)",
                    countermeasure = "投与を中止し気道確保及び呼吸管理を行う (架空)",
                ),
                AdverseReaction(
                    name = "依存性及び離脱症状",
                    frequency = FrequencyBand.UNKNOWN,
                    symptom = "連用により依存を形成し、中止時に反跳性不眠を来すことがある (架空)",
                    initialSigns = "服薬欲求、焦燥、不眠の増悪 (架空)",
                    countermeasure = "急な中止を避け、状態を観察しながら漸減する (架空)",
                ),
            ),
            other =
            AdverseReactionByFrequency(
                over5Percent = listOf("眠気", "倦怠感"),
                between1And5Percent = listOf("ふらつき", "頭痛"),
                under1Percent = listOf("健忘"),
                frequencyUnknown = listOf("悪夢"),
            ),
        ),
        effectsOnLabTests =
        listOf(
            NumberedParagraph(order = 1, content = "肝機能検査値の変動を認めることがある (架空)"),
        ),
        overdose =
        OverdoseInfo(
            symptoms = "深い昏睡 (何があっても起きない状態)。即死には至らない (架空)",
            management = "24 時間以内の医療処置 (気道確保・対症療法・必要に応じて拮抗薬投与・全身管理) が必須 (架空)",
        ),
        administrationPrecautions =
        listOf(
            NumberedParagraph(order = 1, content = "青色着色は誤飲防止のためであり、他剤と取り違えないこと (架空)"),
            NumberedParagraph(order = 2, content = "管理者立会いのもと、就寝直前に水で服用すること (架空)"),
        ),
        otherPrecautions =
        listOf(
            NumberedParagraph(order = 1, content = "連用により依存形成のおそれがあるため短期間の使用にとどめること (架空)"),
        ),
        pharmacokinetics =
        PharmacokineticsInfo(
            bloodConcentration = "経口投与後、約 1 時間で最高血中濃度に到達する (架空)",
            absorption = "消化管から速やかに吸収される (架空)",
            distribution = "中枢神経系へ移行し催眠作用を示す (架空)",
            metabolism = "主として肝代謝により不活性化される (架空)",
            excretion = "代謝物として尿中へ排泄される (架空)",
            parameters =
            listOf(
                PkParameter(name = "Cmax", value = "8.0 ng/mL"),
                PkParameter(name = "Tmax", value = "1.0 時間"),
                PkParameter(name = "T1/2", value = "2.4 時間"),
                PkParameter(name = "AUC", value = "24 ng・hr/mL"),
            ),
        ),
        clinicalResults =
        listOf(
            ClinicalResultSection(
                heading = "不眠症患者対象試験",
                content = "不眠症患者を対象とした架空比較試験で睡眠潜時の短縮が示された (架空)",
            ),
        ),
        pharmacology =
        PharmacologyInfo(
            mechanism = "GABA-A 受容体の部位に結合し神経活動を抑制する様式の催眠作用を示す (架空)",
            effect = "動物試験で睡眠潜時短縮及び睡眠時間延長を示した (架空)",
        ),
        physicochemicalProperties =
        PhysicochemicalInfo(
            genericNameEnglish = generated.physicochemicalProperties?.genericNameEnglish ?: generated.genericName,
            molecularFormula = generated.physicochemicalProperties?.molecularFormula ?: "C16H18N4O2",
            molecularWeight = generated.physicochemicalProperties?.molecularWeight,
            description = "青色澄明の液体で、誤飲防止のため青色に着色している (架空)",
        ),
        handlingPrecautions =
        listOf(
            NumberedParagraph(order = 1, content = "白色ラベルの記載を確認し、服用記録と残量を照合すること (架空)"),
            NumberedParagraph(order = 2, content = "直射日光を避け、冷暗所で保管すること (架空)"),
        ),
        approvalConditions =
        listOf(
            NumberedParagraph(order = 1, content = "適切な睡眠薬管理体制を有する施設で使用すること (架空)"),
        ),
        packages =
        listOf(
            PackageInfo(
                size = "30 mL ガラス瓶 (蝶エンボス加工蓋・白色ラベル) (架空)",
                storageCondition =
                StorageCondition(
                    temperature = StorageTemperature.ROOM_TEMPERATURE,
                    lightProtection = true,
                    moistureProtection = false,
                    additionalNote = "冷暗所保管を推奨 (架空)",
                ),
                expirationMonths = 36,
            ),
        ),
        references =
        listOf(
            Reference(citation = "ナギサ. 青色液剤型催眠薬の服薬管理に関する架空研究. 架空睡眠薬研究, 12, 345-348. (架空)", source = "架空睡眠薬研究"),
            Reference(citation = "ミハシ. 不眠症に対する短時間作用型催眠薬の架空比較試験. 架空臨床睡眠誌, 12, 345-348. (架空)", source = "架空臨床睡眠誌"),
            Reference(citation = "コウメ. 睡眠薬過量服用時の 24 時間管理に関する架空報告. 架空救急薬理, 12, 345-348. (架空)", source = "架空救急薬理"),
        ),
        insuranceNotes =
        listOf(
            NumberedParagraph(order = 1, content = "向精神薬として投与日数及び服薬管理に注意すること (架空)"),
        ),
        manufacturer = "不明",
        relatedDiseaseIds = listOf("disease_0022"),
    )

private fun tredecimFinalOverride(generated: Drug): Drug =
    generated.copy(
        therapeuticCategoryName = "対魔女兵器 (架空分類)",
        composition =
        generated.composition.copy(
            activeIngredient = "トレデキム",
            activeIngredientAmount = Dose(amount = 10.0, unit = DoseUnit.ML),
            appearance = "無色澄明の液体を充填した透明ガラスバイアル (ラベルに「13」表記、銀色アルミシール) (架空)",
            identificationCode = "13",
        ),
        warning =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "本剤は魔女因子保有個体に対する致死毒物であり、治療目的で投与してはならない (架空)",
            ),
        ),
        contraindications =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "治療適応を有する患者への投与は禁忌である (架空)",
            ),
        ),
        indications = emptyList(),
        dosage =
        DosageInfo(
            standardDosage = "致死目的で経口 0.05 mL を超えて使用しない。治療用量は設定されていない (架空)",
        ),
        dosageRelatedPrecautions =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "治療薬としての用量調整は設定されていない (架空)",
            ),
        ),
        importantPrecautions =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "地下保管区画から持ち出す場合はガラスバイアルの「13」識別を照合すること (架空)",
            ),
        ),
        interactions = null,
        adverseReactions =
        AdverseReactionInfo(
            serious =
            listOf(
                AdverseReaction(
                    name = "肉体破壊",
                    frequency = FrequencyBand.UNKNOWN,
                    symptom = "魔女因子干渉により急速な全身崩壊を来す (架空)",
                    initialSigns = "創部または口腔内からの灼熱感、意識混濁 (架空)",
                    countermeasure = "曝露を中止し隔離管理を行う (架空)",
                ),
                AdverseReaction(
                    name = "眼球結晶化",
                    frequency = FrequencyBand.UNKNOWN,
                    symptom = "死亡後に眼球を中心とした結晶化を認める (架空)",
                    initialSigns = "視界白濁、眼痛、結晶様反射 (架空)",
                    countermeasure = "検体を密封し魔女因子汚染として取り扱う (架空)",
                ),
            ),
            other = AdverseReactionByFrequency(frequencyUnknown = listOf("急性疼痛", "意識消失")),
        ),
        overdose =
        OverdoseInfo(
            symptoms = "経口では数分以内、傷口からの混入では即時に致死的な魔女因子崩壊を来す (架空)",
            management = "解毒法は確立していない。隔離、曝露部位封鎖、検体保全を優先する (架空)",
        ),
        administrationPrecautions =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "治療目的の経口投与、注射、外用を行わないこと (架空)",
            ),
        ),
        otherPrecautions =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "研究用試料としてのみ取り扱い、一般医療現場へ流通させないこと (架空)",
            ),
        ),
        pharmacokinetics =
        PharmacokineticsInfo(
            bloodConcentration = "魔女因子保有個体では曝露後数分以内に致死濃度へ到達する (架空)",
            absorption = "経口および創傷面から速やかに吸収される (架空)",
            distribution = "魔女因子活性部位へ選択的に集積する (架空)",
            parameters = listOf(PkParameter(name = "致死濃度到達時間", value = "数分以内")),
        ),
        clinicalResults =
        listOf(
            ClinicalResultSection(
                heading = "ヒト臨床試験",
                content = "治療薬ではないためヒト臨床試験は実施されていない (架空)",
            ),
        ),
        pharmacology =
        PharmacologyInfo(
            mechanism = "魔女因子へ不可逆的に干渉し、不死性を支える再生過程を破壊する (架空)",
            effect = "魔女因子保有個体に対して急速な致死作用を示す (架空)",
        ),
        handlingPrecautions =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "地下金庫内の施錠区画で保管し、開封記録を残すこと (架空)",
            ),
        ),
        packages =
        listOf(
            PackageInfo(
                size = "10 mL ガラスバイアル (ラベル「13」表記) (架空)",
                storageCondition =
                StorageCondition(
                    temperature = StorageTemperature.COLD,
                    lightProtection = true,
                    moistureProtection = true,
                    additionalNote = "施錠区画にて管理 (架空)",
                ),
                expirationMonths = 60,
            ),
        ),
        references = emptyList(),
        insuranceNotes =
        listOf(
            NumberedParagraph(
                order = 1,
                content = "保険適用外であり、研究用試料として管理する (架空)",
            ),
        ),
        manufacturer = "魔女因子研究所",
        revisedAt = IsoDateFormatter.formatDate(date = LocalDate.of(2026, 5, 1)),
        relatedDiseaseIds = listOf("disease_0079"),
    )
