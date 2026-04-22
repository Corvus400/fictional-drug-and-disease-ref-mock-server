package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.HepaticSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.PrecautionPopulationCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RenalSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReaction
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionByFrequency
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AdverseReactionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AgeDosage
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.AgeRange
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CrClRange
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.DosageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.HepaticDose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.IndicationItem
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.InteractionEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.InteractionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.NumberedParagraph
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.OverdoseInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PrecautionPopulation
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.RenalDose

/**
 * 臨床情報系 (warning / contraindications / indications / dosage / 注意系 / 副作用 / 相互作用 /
 * 過量投与) の決定論的 populate を担う。
 *
 * 全関数は `id` + `DrugFieldSlot` + offset から `stableHash` でシードを導出し、
 * `DrugParagraphTemplates` と `ValueRangeGenerator` で内容・件数を決定する。
 * Blueprint に依存せず全フィールドを ubiquitous に populate することで、医薬品モデル仕様の
 * 条件必須フィールド (警告 / 取扱い上の注意 / 適用上の注意 / 保険給付上の注意 /
 * 用法用量に関連する注意 / 薬物動態) が分類軸 6 群で常に 1 件以上を保証する。
 */
internal object DrugClinicalBuilders {
    fun buildContraindications(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.WARNING,
            field = ParagraphField.CONTRAINDICATION_CONTENT,
            countRange = CONTRAINDICATION_COUNT_RANGE,
        )
    }

    fun buildIndications(id: String, dict: DrugPlaceholderDictionary): List<IndicationItem> {
        val seed = stableHash(id = id, slot = DrugFieldSlot.DOSAGE_STANDARD.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = seed, range = INDICATION_COUNT_RANGE)
        return (0 until count).map { offset ->
            IndicationItem(
                order = offset + 1,
                content =
                dict.renderField(
                    field = ParagraphField.INDICATION_CONTENT,
                    seed =
                    stableHash(
                        id = id,
                        slot = DrugFieldSlot.DOSAGE_STANDARD.ordinal,
                        index = offset + 1,
                    ),
                ),
            )
        }
    }

    fun buildIndicationsRelatedPrecautions(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.INDICATION_RELATED_PRECAUTION,
            field = ParagraphField.INDICATION_RELATED_PRECAUTION,
            countRange = SHORT_LIST_COUNT_RANGE,
        )
    }

    fun buildDosage(id: String, dict: DrugPlaceholderDictionary): DosageInfo {
        val standardSeed = stableHash(id = id, slot = DrugFieldSlot.DOSAGE_STANDARD.ordinal, index = 0)
        val ageSeed = stableHash(id = id, slot = DrugFieldSlot.DOSAGE_AGE.ordinal, index = 0)
        val renalSeed = stableHash(id = id, slot = DrugFieldSlot.DOSAGE_RENAL.ordinal, index = 0)
        val hepaticSeed = stableHash(id = id, slot = DrugFieldSlot.DOSAGE_HEPATIC.ordinal, index = 0)
        return DosageInfo(
            standardDosage =
            dict.renderField(
                field = ParagraphField.STANDARD_DOSAGE,
                seed = standardSeed,
            ),
            ageSpecificDosage =
            listOf(
                AgeDosage(
                    range =
                    AgeRange(
                        minAgeMonths = PEDIATRIC_MIN_MONTHS,
                        maxAgeMonths = PEDIATRIC_MAX_MONTHS,
                        label = "6 歳以上 12 歳未満",
                    ),
                    dose =
                    dict.renderField(
                        field = ParagraphField.AGE_DOSE,
                        seed = ageSeed,
                    ),
                ),
            ),
            renalAdjustment =
            listOf(
                RenalDose(
                    range =
                    CrClRange(
                        minMlPerMin = RENAL_MIN_ML_PER_MIN,
                        maxMlPerMin = RENAL_MAX_ML_PER_MIN,
                        severity = RenalSeverity.MODERATE,
                        label = "30-59 mL/min、中等度腎機能低下",
                    ),
                    dose =
                    dict.renderField(
                        field = ParagraphField.RENAL_DOSE,
                        seed = renalSeed,
                    ),
                ),
            ),
            hepaticAdjustment =
            listOf(
                HepaticDose(
                    severity = HepaticSeverity.MODERATE,
                    dose =
                    dict.renderField(
                        field = ParagraphField.HEPATIC_DOSE,
                        seed = hepaticSeed,
                    ),
                ),
            ),
        )
    }

    fun buildWarning(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.WARNING,
            field = ParagraphField.WARNING_CONTENT,
            countRange = SHORT_LIST_COUNT_RANGE,
        )
    }

    fun buildDosageRelatedPrecautions(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.DOSAGE_RELATED_PRECAUTION,
            field = ParagraphField.DOSAGE_RELATED_PRECAUTION,
            countRange = SHORT_LIST_COUNT_RANGE,
        )
    }

    fun buildImportantPrecautions(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.IMPORTANT_PRECAUTION,
            field = ParagraphField.IMPORTANT_PRECAUTION,
            countRange = SHORT_LIST_COUNT_RANGE,
        )
    }

    fun buildPrecautionsForSpecificPopulations(
        id: String,
        dict: DrugPlaceholderDictionary,
    ): List<PrecautionPopulation> {
        val countSeed =
            stableHash(id = id, slot = DrugFieldSlot.PRECAUTION_POPULATION_CATEGORY.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = SHORT_LIST_COUNT_RANGE)
        val categories = PrecautionPopulationCategory.entries.toList()
        return (0 until count).map { offset ->
            val categorySeed =
                stableHash(
                    id = id,
                    slot = DrugFieldSlot.PRECAUTION_POPULATION_CATEGORY.ordinal,
                    index = offset + 1,
                )
            val noteSeed =
                stableHash(
                    id = id,
                    slot = DrugFieldSlot.PRECAUTION_POPULATION_NOTE.ordinal,
                    index = offset + 1,
                )
            PrecautionPopulation(
                category = ValueRangeGenerator.pickOne(seed = categorySeed, candidates = categories),
                note =
                dict.renderField(
                    field = ParagraphField.PRECAUTION_POPULATION_NOTE,
                    seed = noteSeed,
                ),
            )
        }
    }

    fun buildInteractions(id: String, dict: DrugPlaceholderDictionary): InteractionInfo {
        val prohibitedSeed =
            stableHash(id = id, slot = DrugFieldSlot.INTERACTION_PROHIBITED.ordinal, index = 0)
        val cautionSeed =
            stableHash(id = id, slot = DrugFieldSlot.INTERACTION_CAUTION.ordinal, index = 0)
        return InteractionInfo(
            combinationProhibited =
            listOf(
                InteractionEntry(
                    displayName = "他の $DRUG_CATEGORY_PLACEHOLDER",
                    clinicalSymptom =
                    dict.renderField(
                        field = ParagraphField.INTERACTION_SYMPTOM,
                        seed = prohibitedSeed,
                    ),
                    mechanism =
                    dict.renderField(
                        field = ParagraphField.INTERACTION_MECHANISM,
                        seed = prohibitedSeed,
                    ),
                ),
            ),
            combinationCaution =
            listOf(
                InteractionEntry(
                    displayName = "$DRUG_CATEGORY_PLACEHOLDER 系薬剤",
                    clinicalSymptom =
                    dict.renderField(
                        field = ParagraphField.INTERACTION_SYMPTOM,
                        seed = cautionSeed,
                    ),
                    mechanism =
                    dict.renderField(
                        field = ParagraphField.INTERACTION_MECHANISM,
                        seed = cautionSeed,
                    ),
                ),
            ),
        )
    }

    fun buildAdverseReactions(id: String, dict: DrugPlaceholderDictionary): AdverseReactionInfo {
        val seriousCountSeed =
            stableHash(id = id, slot = DrugFieldSlot.ADVERSE_SERIOUS.ordinal, index = 0)
        val seriousCount =
            ValueRangeGenerator.pickCount(seed = seriousCountSeed, range = SHORT_LIST_COUNT_RANGE)
        val serious =
            (0 until seriousCount).map { offset ->
                val nameSeed =
                    stableHash(
                        id = id,
                        slot = DrugFieldSlot.ADVERSE_SERIOUS.ordinal,
                        index = offset + 1,
                    )
                val freqSeed =
                    stableHash(
                        id = id,
                        slot = DrugFieldSlot.ADVERSE_SERIOUS_FREQUENCY.ordinal,
                        index = offset + 1,
                    )
                AdverseReaction(
                    name = "重篤な副作用 ${offset + 1}",
                    frequency =
                    ValueRangeGenerator.pickOne(
                        seed = freqSeed,
                        candidates = FrequencyBand.entries.toList(),
                    ),
                    symptom =
                    dict.renderField(
                        field = ParagraphField.ADVERSE_SYMPTOM,
                        seed = nameSeed,
                    ),
                    initialSigns =
                    dict.renderField(
                        field = ParagraphField.ADVERSE_INITIAL_SIGNS,
                        seed = nameSeed,
                    ),
                    countermeasure =
                    dict.renderField(
                        field = ParagraphField.ADVERSE_COUNTERMEASURE,
                        seed = nameSeed,
                    ),
                )
            }
        return AdverseReactionInfo(
            serious = serious,
            other =
            AdverseReactionByFrequency(
                over5Percent = listOf("頭痛", "めまい"),
                between1And5Percent = listOf("悪心", "下痢"),
                under1Percent = listOf("発疹"),
                frequencyUnknown = listOf("倦怠感"),
            ),
        )
    }

    fun buildEffectsOnLabTests(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.EFFECT_ON_LAB,
            field = ParagraphField.EFFECT_ON_LAB,
            countRange = SHORT_LIST_COUNT_RANGE,
        )
    }

    fun buildOverdose(id: String, dict: DrugPlaceholderDictionary): OverdoseInfo {
        val symptomsSeed = stableHash(id = id, slot = DrugFieldSlot.OVERDOSE_SYMPTOMS.ordinal, index = 0)
        val managementSeed =
            stableHash(id = id, slot = DrugFieldSlot.OVERDOSE_MANAGEMENT.ordinal, index = 0)
        return OverdoseInfo(
            symptoms =
            dict.renderField(
                field = ParagraphField.OVERDOSE_SYMPTOMS,
                seed = symptomsSeed,
            ),
            management =
            dict.renderField(
                field = ParagraphField.OVERDOSE_MANAGEMENT,
                seed = managementSeed,
            ),
        )
    }

    fun buildAdministrationPrecautions(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.ADMINISTRATION_PRECAUTION,
            field = ParagraphField.ADMINISTRATION_PRECAUTION,
            countRange = SHORT_LIST_COUNT_RANGE,
        )
    }

    fun buildOtherPrecautions(
        id: String,
        dict: DrugPlaceholderDictionary,
        blueprint: DrugBlueprint,
    ): List<NumberedParagraph> {
        // blueprint 引数は将来の軸別カスタマイズ拡張用 (現時点では count/内容に影響しない)
        val seedOffset = if (blueprint.isChronicPrescription) 1 else 0
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.OTHER_PRECAUTION,
            field = ParagraphField.OTHER_PRECAUTION,
            countRange = SHORT_LIST_COUNT_RANGE,
            extraIndexOffset = seedOffset,
        )
    }

    private fun buildNumberedParagraphs(
        id: String,
        dict: DrugPlaceholderDictionary,
        slot: DrugFieldSlot,
        field: ParagraphField,
        countRange: IntRange,
        extraIndexOffset: Int = 0,
    ): List<NumberedParagraph> {
        val countSeed = stableHash(id = id, slot = slot.ordinal, index = 0 + extraIndexOffset)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = countRange)
        return (0 until count).map { offset ->
            NumberedParagraph(
                order = offset + 1,
                content =
                dict.renderField(
                    field = field,
                    seed =
                    stableHash(
                        id = id,
                        slot = slot.ordinal,
                        index = offset + 1 + extraIndexOffset,
                    ),
                ),
            )
        }
    }

    private val CONTRAINDICATION_COUNT_RANGE: IntRange = 1..2
    private val INDICATION_COUNT_RANGE: IntRange = 1..2
    private val SHORT_LIST_COUNT_RANGE: IntRange = 1..2

    private const val PEDIATRIC_MIN_MONTHS: Int = 72
    private const val PEDIATRIC_MAX_MONTHS: Int = 144
    private const val RENAL_MIN_ML_PER_MIN: Int = 30
    private const val RENAL_MAX_ML_PER_MIN: Int = 59
    private const val DRUG_CATEGORY_PLACEHOLDER: String = "サンプル系薬"
}
