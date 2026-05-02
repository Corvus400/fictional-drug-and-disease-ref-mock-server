package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.ClinicalResultSection
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.NumberedParagraph
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PackageInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PharmacokineticsInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PharmacologyInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PkParameter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Reference
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.StorageCondition

/**
 * メタ情報系 (薬物動態 / 臨床成績 / 薬効薬理 / 取扱い / 承認条件 / 参考文献 / 保険給付 /
 * 関連疾患 ID / yjCode / package) の決定論的 populate を担う。
 */
internal object DrugMetaBuilders {
    fun buildPharmacokinetics(id: String, dict: DrugPlaceholderDictionary): PharmacokineticsInfo {
        return PharmacokineticsInfo(
            bloodConcentration =
            dict.renderField(
                field = ParagraphField.PHARMACOKINETICS_BLOOD,
                seed = stableHash(id = id, slot = DrugFieldSlot.PK_BLOOD.ordinal, index = 0),
            ),
            absorption =
            dict.renderField(
                field = ParagraphField.PHARMACOKINETICS_ABSORPTION,
                seed = stableHash(id = id, slot = DrugFieldSlot.PK_ABSORPTION.ordinal, index = 0),
            ),
            distribution =
            dict.renderField(
                field = ParagraphField.PHARMACOKINETICS_DISTRIBUTION,
                seed = stableHash(id = id, slot = DrugFieldSlot.PK_DISTRIBUTION.ordinal, index = 0),
            ),
            metabolism =
            dict.renderField(
                field = ParagraphField.PHARMACOKINETICS_METABOLISM,
                seed = stableHash(id = id, slot = DrugFieldSlot.PK_METABOLISM.ordinal, index = 0),
            ),
            excretion =
            dict.renderField(
                field = ParagraphField.PHARMACOKINETICS_EXCRETION,
                seed = stableHash(id = id, slot = DrugFieldSlot.PK_EXCRETION.ordinal, index = 0),
            ),
            parameters =
            listOf(
                PkParameter(name = "Cmax", value = "4.5 μg/mL"),
                PkParameter(name = "T1/2", value = "6.2 時間"),
                PkParameter(name = "AUC", value = "38.4 μg・時/mL"),
            ),
        )
    }

    fun buildClinicalResults(id: String, dict: DrugPlaceholderDictionary): List<ClinicalResultSection> {
        val countSeed = stableHash(id = id, slot = DrugFieldSlot.CLINICAL_RESULT.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = CLINICAL_RESULT_RANGE)
        val headings = listOf("有効性", "安全性", "長期投与試験")
        return (0 until count).map { offset ->
            val contentSeed =
                stableHash(
                    id = id,
                    slot = DrugFieldSlot.CLINICAL_RESULT.ordinal,
                    index = offset + 1,
                )
            ClinicalResultSection(
                heading = headings[offset % headings.size],
                content =
                dict.renderField(
                    field = ParagraphField.CLINICAL_RESULT_CONTENT,
                    seed = contentSeed,
                ),
            )
        }
    }

    fun buildPharmacology(id: String, dict: DrugPlaceholderDictionary): PharmacologyInfo {
        return PharmacologyInfo(
            mechanism =
            dict.renderField(
                field = ParagraphField.PHARMACOLOGY_MECHANISM,
                seed =
                stableHash(
                    id = id,
                    slot = DrugFieldSlot.PHARMACOLOGY_MECHANISM.ordinal,
                    index = 0,
                ),
            ),
            effect =
            dict.renderField(
                field = ParagraphField.PHARMACOLOGY_EFFECT,
                seed =
                stableHash(
                    id = id,
                    slot = DrugFieldSlot.PHARMACOLOGY_EFFECT.ordinal,
                    index = 0,
                ),
            ),
        )
    }

    fun buildHandlingPrecautions(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.HANDLING,
            field = ParagraphField.HANDLING_CONTENT,
        )
    }

    fun buildApprovalConditions(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.APPROVAL_CONDITION,
            field = ParagraphField.APPROVAL_CONDITION,
        )
    }

    fun buildReferences(id: String): List<Reference> {
        val countSeed = stableHash(id = id, slot = DrugFieldSlot.REFERENCE.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = REFERENCE_RANGE)
        return (0 until count).map { offset ->
            Reference(
                citation = "架空文献 ${offset + 1}. サンプル誌, 12, 345-348.",
                source = "サンプル誌",
            )
        }
    }

    fun buildInsuranceNotes(id: String, dict: DrugPlaceholderDictionary): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            dict = dict,
            slot = DrugFieldSlot.INSURANCE_NOTE,
            field = ParagraphField.INSURANCE_NOTE,
        )
    }

    fun buildRelatedDiseaseIds(id: String): List<String> {
        val countSeed = stableHash(id = id, slot = DrugFieldSlot.RELATED_DISEASE.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = RELATED_DISEASE_RANGE)
        return (0 until count).map { offset ->
            val indexSeed =
                stableHash(
                    id = id,
                    slot = DrugFieldSlot.RELATED_DISEASE.ordinal,
                    index = offset + 1,
                )
            val diseaseIndex =
                ValueRangeGenerator.pickInRange(seed = indexSeed, range = DISEASE_INDEX_RANGE)
            "disease_${diseaseIndex.toString().padStart(length = ID_PAD_LENGTH, padChar = '0')}"
        }.distinct()
    }

    fun buildYjCode(id: String): String {
        val suffixSeed = stableHash(id = id, slot = DrugFieldSlot.YJ_CODE.ordinal, index = 0)
        val suffix =
            ValueRangeGenerator.pickInRange(seed = suffixSeed, range = YJ_SUFFIX_RANGE)
                .toString()
                .padStart(length = YJ_SUFFIX_LENGTH, padChar = '0')
        return YJ_FICTIONAL_PREFIX + suffix
    }

    fun buildPackages(
        id: String,
        dosageForm: DosageForm,
        isBiological: Boolean,
    ): List<PackageInfo> {
        val expirationSeed =
            stableHash(id = id, slot = DrugFieldSlot.PACKAGE_EXPIRATION.ordinal, index = 0)
        val expiration = ValueRangeGenerator.pickInRange(seed = expirationSeed, range = EXPIRATION_RANGE)
        return listOf(
            PackageInfo(
                size = "100 錠 (10 錠 × 10 PTP)",
                storageCondition =
                StorageCondition(
                    temperature =
                    pickStorageTemperature(
                        form = dosageForm,
                        isBiological = isBiological,
                        drugId = id,
                    ),
                    lightProtection = false,
                    moistureProtection = false,
                ),
                expirationMonths = expiration,
            ),
        )
    }

    private fun pickStorageTemperature(
        form: DosageForm,
        isBiological: Boolean,
        drugId: String,
    ): StorageTemperature {
        val candidates =
            when {
                form == DosageForm.INJECTION_FORM && isBiological ->
                    listOf(StorageTemperature.COLD, StorageTemperature.FROZEN)
                form == DosageForm.INJECTION_FORM ->
                    listOf(StorageTemperature.ROOM_TEMPERATURE, StorageTemperature.COLD)
                form == DosageForm.EYE_DROPS ||
                    form == DosageForm.LIQUID ||
                    form == DosageForm.SUPPOSITORY ->
                    listOf(StorageTemperature.COLD, StorageTemperature.ROOM_TEMPERATURE)
                else -> listOf(StorageTemperature.ROOM_TEMPERATURE)
            }
        val seed = stableHash(id = drugId, slot = DrugFieldSlot.STORAGE_TEMPERATURE_PICK.ordinal, index = 0)
        return ValueRangeGenerator.pickOne(seed = seed, candidates = candidates)
    }

    private fun buildNumberedParagraphs(
        id: String,
        dict: DrugPlaceholderDictionary,
        slot: DrugFieldSlot,
        field: ParagraphField,
    ): List<NumberedParagraph> {
        val countSeed = stableHash(id = id, slot = slot.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = SHORT_LIST_COUNT_RANGE)
        return (0 until count).map { offset ->
            NumberedParagraph(
                order = offset + 1,
                content =
                dict.renderField(
                    field = field,
                    seed = stableHash(id = id, slot = slot.ordinal, index = offset + 1),
                ),
            )
        }
    }

    private val SHORT_LIST_COUNT_RANGE: IntRange = 1..2
    private val CLINICAL_RESULT_RANGE: IntRange = 1..3
    private val REFERENCE_RANGE: IntRange = 1..3
    private val RELATED_DISEASE_RANGE: IntRange = 1..2
    private val DISEASE_INDEX_RANGE: IntRange = 0..79
    private val YJ_SUFFIX_RANGE: IntRange = 0..999_999_999
    private val EXPIRATION_RANGE: IntRange = 24..60

    private const val YJ_FICTIONAL_PREFIX: String = "999"
    private const val YJ_SUFFIX_LENGTH: Int = 9
    private const val ID_PAD_LENGTH: Int = 4
}
