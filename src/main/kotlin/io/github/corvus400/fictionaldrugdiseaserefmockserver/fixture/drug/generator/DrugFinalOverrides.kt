package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.IsoDateFormatter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReaction
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionByFrequency
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.ClinicalResultSection
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.DosageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Dose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.NumberedParagraph
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.OverdoseInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PackageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PharmacokineticsInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PharmacologyInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PkParameter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.StorageCondition
import java.time.LocalDate

internal val DRUG_FINAL_OVERRIDES: Map<String, (Drug) -> Drug> =
    mapOf(
        "drug_0080" to ::tredecimFinalOverride,
        "drug_0089" to ::arisaSleepAidFinalOverride,
    )

private fun arisaSleepAidFinalOverride(generated: Drug): Drug =
    generated.copy(
        composition =
        generated.composition.copy(
            appearance = "青色澄明の液体を充填した透明ガラスバイアル (蓋に蝶のエンボス加工付、白色ラベル) (架空)",
        ),
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
