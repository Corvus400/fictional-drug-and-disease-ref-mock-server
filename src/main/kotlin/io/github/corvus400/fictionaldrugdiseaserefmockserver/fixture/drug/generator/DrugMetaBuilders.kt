package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
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
    fun buildPharmacokinetics(id: String): PharmacokineticsInfo {
        return PharmacokineticsInfo(
            bloodConcentration =
            DrugParagraphTemplates.pickTemplate(
                field = ParagraphField.PHARMACOKINETICS_BLOOD,
                seed = stableHash(id = id, slot = DrugFieldSlot.PK_BLOOD.ordinal, index = 0),
            ),
            absorption =
            DrugParagraphTemplates.pickTemplate(
                field = ParagraphField.PHARMACOKINETICS_ABSORPTION,
                seed = stableHash(id = id, slot = DrugFieldSlot.PK_ABSORPTION.ordinal, index = 0),
            ),
            distribution =
            DrugParagraphTemplates.pickTemplate(
                field = ParagraphField.PHARMACOKINETICS_DISTRIBUTION,
                seed = stableHash(id = id, slot = DrugFieldSlot.PK_DISTRIBUTION.ordinal, index = 0),
            ),
            metabolism =
            DrugParagraphTemplates.pickTemplate(
                field = ParagraphField.PHARMACOKINETICS_METABOLISM,
                seed = stableHash(id = id, slot = DrugFieldSlot.PK_METABOLISM.ordinal, index = 0),
            ),
            excretion =
            DrugParagraphTemplates.pickTemplate(
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

    fun buildClinicalResults(id: String): List<ClinicalResultSection> {
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
                DrugParagraphTemplates.pickTemplate(
                    field = ParagraphField.CLINICAL_RESULT_CONTENT,
                    seed = contentSeed,
                ),
            )
        }
    }

    fun buildPharmacology(id: String): PharmacologyInfo {
        return PharmacologyInfo(
            mechanism =
            DrugParagraphTemplates.pickTemplate(
                field = ParagraphField.PHARMACOLOGY_MECHANISM,
                seed =
                stableHash(
                    id = id,
                    slot = DrugFieldSlot.PHARMACOLOGY_MECHANISM.ordinal,
                    index = 0,
                ),
            ),
            effect =
            DrugParagraphTemplates.pickTemplate(
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

    fun buildHandlingPrecautions(id: String): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
            slot = DrugFieldSlot.HANDLING,
            field = ParagraphField.HANDLING_CONTENT,
        )
    }

    fun buildApprovalConditions(id: String): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
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

    fun buildInsuranceNotes(id: String): List<NumberedParagraph> {
        return buildNumberedParagraphs(
            id = id,
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
        val firstSeed = stableHash(id = id, slot = DrugFieldSlot.YJ_CODE.ordinal, index = 0)
        val secondSeed = stableHash(id = id, slot = DrugFieldSlot.YJ_CODE.ordinal, index = 1)
        val firstDigits =
            ValueRangeGenerator.pickInRange(seed = firstSeed, range = YJ_DIGIT_RANGE_6)
                .toString()
                .padStart(length = YJ_FIRST_DIGITS, padChar = '0')
        val secondDigits =
            ValueRangeGenerator.pickInRange(seed = secondSeed, range = YJ_DIGIT_RANGE_6)
                .toString()
                .padStart(length = YJ_FIRST_DIGITS, padChar = '0')
        return firstDigits + secondDigits
    }

    fun buildPackages(id: String): List<PackageInfo> {
        val expirationSeed =
            stableHash(id = id, slot = DrugFieldSlot.PACKAGE_EXPIRATION.ordinal, index = 0)
        val expiration = ValueRangeGenerator.pickInRange(seed = expirationSeed, range = EXPIRATION_RANGE)
        return listOf(
            PackageInfo(
                size = "100 錠 (10 錠 × 10 PTP)",
                storageCondition =
                StorageCondition(
                    temperature = StorageTemperature.ROOM_TEMPERATURE,
                    lightProtection = false,
                    moistureProtection = false,
                ),
                expirationMonths = expiration,
            ),
        )
    }

    private fun buildNumberedParagraphs(
        id: String,
        slot: DrugFieldSlot,
        field: ParagraphField,
    ): List<NumberedParagraph> {
        val countSeed = stableHash(id = id, slot = slot.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = SHORT_LIST_COUNT_RANGE)
        return (0 until count).map { offset ->
            NumberedParagraph(
                order = offset + 1,
                content =
                DrugParagraphTemplates.pickTemplate(
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
    private val DISEASE_INDEX_RANGE: IntRange = 1..80
    private val YJ_DIGIT_RANGE_6: IntRange = 0..999_999
    private val EXPIRATION_RANGE: IntRange = 24..60

    private const val YJ_FIRST_DIGITS: Int = 6
    private const val ID_PAD_LENGTH: Int = 4
}
