package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.PrevalenceUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.EpidemiologyInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.Exam
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.Grade
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.OnsetAgeRange
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.PharmaTreatment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.Prevalence
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.ProtocolStep
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SeverityInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SexDistribution
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentSection

/**
 * Disease 共通フィールド 24 の nested 構造を決定論的に populate する。
 *
 * 全値は `stableHash(id, slot.ordinal, offset)` + [ValueRangeGenerator] / [DiseaseParagraphTemplates]
 * で選択され、Blueprint 非依存の "ubiquitous populate 戦略" により UI 発火条件
 * (E5/E9/E10/E13/E14/E15 等) を常に満たす。
 *
 * ICD-10 章別の条件必須 (疾病モデル仕様§分類軸別の追加フィールド) は
 * [DiseaseGenerator] 側の when 分岐で追加保証する想定。当オブジェクトは共通 populate に集中する。
 */
internal object DiseaseNestedBuilders {
    fun buildMedicalDepartment(
        id: String,
        chapter: Icd10Chapter,
    ): List<MedicalDepartment> {
        val primary = primaryDepartmentFor(chapter = chapter)
        val secondarySeed = stableHash(id = id, slot = DiseaseFieldSlot.MEDICAL_DEPARTMENT.ordinal, index = 0)
        val secondary = ValueRangeGenerator.pickOne(
            seed = secondarySeed,
            candidates = MedicalDepartment.entries.toList(),
        )
        return listOf(primary, secondary).distinct()
    }

    fun buildSummary(
        id: String,
        dict: DiseasePlaceholderDictionary,
        context: DiseaseRenderContext,
    ): String {
        return dict.renderField(
            field = DiseaseParagraphField.OVERVIEW_DESCRIPTION,
            seed = stableHash(id = id, slot = DiseaseFieldSlot.SUMMARY.ordinal, index = 0),
            context = context,
        )
    }

    fun buildEtiology(
        id: String,
        dict: DiseasePlaceholderDictionary,
        context: DiseaseRenderContext,
    ): String {
        return dict.renderField(
            field = DiseaseParagraphField.OVERVIEW_DESCRIPTION,
            seed = stableHash(id = id, slot = DiseaseFieldSlot.ETIOLOGY.ordinal, index = 0),
            context = context,
        )
    }

    fun buildSymptoms(id: String): SymptomInfo {
        val mainCountSeed = stableHash(id = id, slot = DiseaseFieldSlot.SYMPTOM_MAIN_COUNT.ordinal, index = 0)
        val mainCount = ValueRangeGenerator.pickCount(seed = mainCountSeed, range = MAIN_SYMPTOM_RANGE)
        val mainSymptoms = (0 until mainCount).map { offset ->
            val seed = stableHash(
                id = id,
                slot = DiseaseFieldSlot.SYMPTOM_MAIN_COUNT.ordinal,
                index = offset + 1,
            )
            ValueRangeGenerator.pickOne(seed = seed, candidates = MAIN_SYMPTOM_VOCAB)
        }
        val associatedCountSeed = stableHash(
            id = id,
            slot = DiseaseFieldSlot.SYMPTOM_ASSOCIATED_COUNT.ordinal,
            index = 0,
        )
        val associatedCount = ValueRangeGenerator.pickCount(
            seed = associatedCountSeed,
            range = ASSOCIATED_SYMPTOM_RANGE,
        )
        val associatedSymptoms = (0 until associatedCount).map { offset ->
            val seed = stableHash(
                id = id,
                slot = DiseaseFieldSlot.SYMPTOM_ASSOCIATED_COUNT.ordinal,
                index = offset + 1,
            )
            ValueRangeGenerator.pickOne(seed = seed, candidates = ASSOCIATED_SYMPTOM_VOCAB)
        }
        val onsetSeed = stableHash(id = id, slot = DiseaseFieldSlot.SYMPTOM_ONSET_PATTERN.ordinal, index = 0)
        val onsetPattern = ValueRangeGenerator.pickOne(
            seed = onsetSeed,
            candidates = OnsetPattern.entries.toList(),
        )
        return SymptomInfo(
            mainSymptoms = mainSymptoms,
            associatedSymptoms = associatedSymptoms,
            onsetPattern = onsetPattern,
        )
    }

    fun buildDiagnosticCriteria(
        id: String,
        dict: DiseasePlaceholderDictionary,
        context: DiseaseRenderContext,
    ): DiagnosticCriteriaInfo {
        val requiredCountSeed = stableHash(
            id = id,
            slot = DiseaseFieldSlot.DIAGNOSTIC_REQUIRED_COUNT.ordinal,
            index = 0,
        )
        val requiredCount = ValueRangeGenerator.pickCount(
            seed = requiredCountSeed,
            range = DIAGNOSTIC_REQUIRED_RANGE,
        )
        val required = (0 until requiredCount).map { offset ->
            dict.renderField(
                field = DiseaseParagraphField.DIAGNOSTIC_CRITERIA_DESCRIPTION,
                seed = stableHash(
                    id = id,
                    slot = DiseaseFieldSlot.DIAGNOSTIC_REQUIRED_COUNT.ordinal,
                    index = offset + 1,
                ),
                context = context,
            )
        }
        val supportingCountSeed = stableHash(
            id = id,
            slot = DiseaseFieldSlot.DIAGNOSTIC_SUPPORTING_COUNT.ordinal,
            index = 0,
        )
        val supportingCount = ValueRangeGenerator.pickCount(
            seed = supportingCountSeed,
            range = DIAGNOSTIC_SUPPORTING_RANGE,
        )
        val supporting = (0 until supportingCount).map { offset ->
            dict.renderField(
                field = DiseaseParagraphField.DIAGNOSTIC_CRITERIA_DESCRIPTION,
                seed = stableHash(
                    id = id,
                    slot = DiseaseFieldSlot.DIAGNOSTIC_SUPPORTING_COUNT.ordinal,
                    index = offset + 1,
                ),
                context = context,
            )
        }
        val notes = dict.renderField(
            field = DiseaseParagraphField.DIAGNOSTIC_CRITERIA_DESCRIPTION,
            seed = stableHash(id = id, slot = DiseaseFieldSlot.DIAGNOSTIC_NOTES.ordinal, index = 0),
            context = context,
        )
        return DiagnosticCriteriaInfo(
            required = required,
            supporting = supporting,
            notes = notes,
        )
    }

    fun buildRequiredExams(
        id: String,
        chapter: Icd10Chapter,
    ): List<Exam> {
        val countSeed = stableHash(id = id, slot = DiseaseFieldSlot.REQUIRED_EXAM_COUNT.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = REQUIRED_EXAM_RANGE)
        val primaryCategory = primaryExamCategoryFor(chapter = chapter)
        val exams = (0 until count).map { offset ->
            val category = if (offset == 0) {
                primaryCategory
            } else {
                val categorySeed = stableHash(
                    id = id,
                    slot = DiseaseFieldSlot.REQUIRED_EXAM_CATEGORY.ordinal,
                    index = offset,
                )
                ValueRangeGenerator.pickOne(
                    seed = categorySeed,
                    candidates = ExamCategory.entries.toList(),
                )
            }
            val nameSeed = stableHash(
                id = id,
                slot = DiseaseFieldSlot.REQUIRED_EXAM_NAME.ordinal,
                index = offset,
            )
            Exam(
                name = ValueRangeGenerator.pickOne(seed = nameSeed, candidates = EXAM_NAME_VOCAB),
                category = category,
                typicalFinding = "典型所見 ${offset + 1} (架空)",
                referenceRange = "基準値 ${offset + 1} (架空)",
            )
        }
        return ensureImagingForCardiovascular(chapter = chapter, exams = exams)
    }

    fun buildSeverityGrading(
        id: String,
        dict: DiseasePlaceholderDictionary,
        context: DiseaseRenderContext,
    ): SeverityInfo {
        val countSeed = stableHash(id = id, slot = DiseaseFieldSlot.SEVERITY_GRADE_COUNT.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = SEVERITY_GRADE_RANGE)
        val grades = (0 until count).map { offset ->
            val label = "Grade ${offset + 1}"
            val criteria = dict.renderField(
                field = DiseaseParagraphField.SEVERITY_DESCRIPTION,
                seed = stableHash(
                    id = id,
                    slot = DiseaseFieldSlot.SEVERITY_GRADE_LABEL.ordinal,
                    index = offset,
                ),
                context = context,
            )
            val action = dict.renderField(
                field = DiseaseParagraphField.SEVERITY_DESCRIPTION,
                seed = stableHash(
                    id = id,
                    slot = DiseaseFieldSlot.SEVERITY_GRADE_ACTION.ordinal,
                    index = offset,
                ),
                context = context,
            )
            Grade(label = label, criteria = criteria, recommendedAction = action)
        }
        return SeverityInfo(
            gradingSystem = GRADING_SYSTEM_LABEL,
            grades = grades,
        )
    }

    fun buildTreatments(
        id: String,
        chapter: Icd10Chapter,
        dict: DiseasePlaceholderDictionary,
        context: DiseaseRenderContext,
    ): TreatmentInfo {
        val pharmaCountSeed = stableHash(
            id = id,
            slot = DiseaseFieldSlot.TREATMENT_PHARMA_COUNT.ordinal,
            index = 0,
        )
        val pharmaRange = if (chapter == Icd10Chapter.CHAPTER_IV) {
            TREATMENT_PHARMA_RANGE_REQUIRED
        } else {
            TREATMENT_PHARMA_RANGE_OPTIONAL
        }
        val pharmaCount = ValueRangeGenerator.pickCount(
            seed = pharmaCountSeed,
            range = pharmaRange,
        )
        val pharmacological = (0 until pharmaCount).map { offset ->
            PharmaTreatment(
                drugCategory = "架空薬効群 ${offset + 1}",
                drugIds = emptyList(),
                indication = dict.renderField(
                    field = DiseaseParagraphField.TREATMENT_DESCRIPTION,
                    seed = stableHash(
                        id = id,
                        slot = DiseaseFieldSlot.TREATMENT_PHARMA_INDICATION.ordinal,
                        index = offset,
                    ),
                    context = context,
                ),
                notes = dict.renderField(
                    field = DiseaseParagraphField.TREATMENT_DESCRIPTION,
                    seed = stableHash(
                        id = id,
                        slot = DiseaseFieldSlot.TREATMENT_PHARMA_NOTES.ordinal,
                        index = offset,
                    ),
                    context = context,
                ),
            )
        }
        val nonPharmaCountSeed = stableHash(
            id = id,
            slot = DiseaseFieldSlot.TREATMENT_NONPHARMA_COUNT.ordinal,
            index = 0,
        )
        val nonPharmaCount = ValueRangeGenerator.pickCount(
            seed = nonPharmaCountSeed,
            range = TREATMENT_NONPHARMA_RANGE,
        )
        val nonPharmacological = (0 until nonPharmaCount).map { offset ->
            TreatmentSection(
                heading = NONPHARMA_HEADINGS[offset % NONPHARMA_HEADINGS.size],
                items = listOf("項目 1", "項目 2"),
                description = dict.renderField(
                    field = DiseaseParagraphField.TREATMENT_DESCRIPTION,
                    seed = stableHash(
                        id = id,
                        slot = DiseaseFieldSlot.TREATMENT_NONPHARMA_DESC.ordinal,
                        index = offset,
                    ),
                    context = context,
                ),
            )
        }
        val acuteCountSeed = stableHash(
            id = id,
            slot = DiseaseFieldSlot.TREATMENT_ACUTE_COUNT.ordinal,
            index = 0,
        )
        val acuteCount = ValueRangeGenerator.pickCount(
            seed = acuteCountSeed,
            range = TREATMENT_ACUTE_RANGE,
        )
        val acutePhaseProtocol = (0 until acuteCount).map { offset ->
            ProtocolStep(
                order = offset + 1,
                action = dict.renderField(
                    field = DiseaseParagraphField.TREATMENT_DESCRIPTION,
                    seed = stableHash(
                        id = id,
                        slot = DiseaseFieldSlot.TREATMENT_ACUTE_ACTION.ordinal,
                        index = offset,
                    ),
                    context = context,
                ),
                target = "目標値 ${offset + 1} (架空)",
            )
        }
        return TreatmentInfo(
            pharmacological = pharmacological,
            nonPharmacological = nonPharmacological,
            acutePhaseProtocol = acutePhaseProtocol,
        )
    }

    fun buildPrognosis(
        id: String,
        dict: DiseasePlaceholderDictionary,
        context: DiseaseRenderContext,
    ): String {
        return dict.renderField(
            field = DiseaseParagraphField.PROGNOSIS_DESCRIPTION,
            seed = stableHash(id = id, slot = DiseaseFieldSlot.PROGNOSIS.ordinal, index = 0),
            context = context,
        )
    }

    fun buildPrevention(id: String): List<String> {
        val countSeed = stableHash(id = id, slot = DiseaseFieldSlot.PREVENTION_COUNT.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = PREVENTION_RANGE)
        return (0 until count).map { offset ->
            val seed = stableHash(
                id = id,
                slot = DiseaseFieldSlot.PREVENTION_ITEM.ordinal,
                index = offset,
            )
            ValueRangeGenerator.pickOne(seed = seed, candidates = PREVENTION_VOCAB)
        }
    }

    fun buildEpidemiology(
        id: String,
        chapter: Icd10Chapter,
        isRareDisease: Boolean,
    ): EpidemiologyInfo {
        val prevalenceSeed = stableHash(
            id = id,
            slot = DiseaseFieldSlot.EPIDEMIOLOGY_PREVALENCE.ordinal,
            index = 0,
        )
        val rate = ValueRangeGenerator.pickInRange(seed = prevalenceSeed, range = PREVALENCE_RATE_RANGE)
        val unit = derivePrevalenceUnit(chapter = chapter, isRareDisease = isRareDisease)
        val denominator = denominatorFor(unit = unit)
        val prevalence = Prevalence(
            rate = rate.toDouble(),
            denominator = denominator,
            unit = unit,
            label = labelFor(unit = unit, rate = rate, denominator = denominator),
        )
        val ageSeed = stableHash(id = id, slot = DiseaseFieldSlot.EPIDEMIOLOGY_ONSET_AGE.ordinal, index = 0)
        val minAge = ValueRangeGenerator.pickInRange(seed = ageSeed, range = ONSET_MIN_AGE_RANGE)
        val maxAge = minAge + ONSET_AGE_SPAN
        val onsetAgeRange = OnsetAgeRange(
            minAgeYears = minAge,
            maxAgeYears = maxAge,
            label = "$minAge-$maxAge 代",
        )
        val sexRatio = buildSexDistribution(id = id, chapter = chapter)
        val riskCountSeed = stableHash(
            id = id,
            slot = DiseaseFieldSlot.EPIDEMIOLOGY_RISK_COUNT.ordinal,
            index = 0,
        )
        val riskCount = ValueRangeGenerator.pickCount(seed = riskCountSeed, range = RISK_FACTOR_RANGE)
        val riskFactors = (0 until riskCount).map { offset ->
            val seed = stableHash(
                id = id,
                slot = DiseaseFieldSlot.EPIDEMIOLOGY_RISK_ITEM.ordinal,
                index = offset,
            )
            ValueRangeGenerator.pickOne(seed = seed, candidates = RISK_FACTOR_VOCAB)
        }
        return EpidemiologyInfo(
            prevalence = prevalence,
            onsetAgeRange = onsetAgeRange,
            sexRatio = sexRatio,
            riskFactors = riskFactors,
        )
    }

    fun buildRelatedDrugIds(id: String): List<String> {
        val countSeed = stableHash(id = id, slot = DiseaseFieldSlot.RELATED_DRUG_COUNT.ordinal, index = 0)
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = RELATED_DRUG_RANGE)
        return (0 until count).map { offset ->
            val indexSeed = stableHash(
                id = id,
                slot = DiseaseFieldSlot.RELATED_DRUG_INDEX.ordinal,
                index = offset + 1,
            )
            val drugIndex = ValueRangeGenerator.pickInRange(seed = indexSeed, range = DRUG_INDEX_RANGE)
            "drug_${drugIndex.toString().padStart(length = ID_PAD_LENGTH, padChar = '0')}"
        }.distinct()
    }

    fun buildRelatedDiseaseIds(
        id: String,
        selfIndex: Int,
    ): List<String> {
        val countSeed = stableHash(
            id = id,
            slot = DiseaseFieldSlot.RELATED_DISEASE_COUNT.ordinal,
            index = 0,
        )
        val count = ValueRangeGenerator.pickCount(seed = countSeed, range = RELATED_DISEASE_RANGE)
        return (0 until count).map { offset ->
            val indexSeed = stableHash(
                id = id,
                slot = DiseaseFieldSlot.RELATED_DISEASE_INDEX.ordinal,
                index = offset + 1,
            )
            val diseaseIndex = ValueRangeGenerator.pickInRange(seed = indexSeed, range = DISEASE_INDEX_RANGE)
            val resolved = if (diseaseIndex == selfIndex) {
                ((diseaseIndex + 1) % (DISEASE_INDEX_RANGE.last - DISEASE_INDEX_RANGE.first + 1)) +
                    DISEASE_INDEX_RANGE.first
            } else {
                diseaseIndex
            }
            "disease_${resolved.toString().padStart(length = ID_PAD_LENGTH, padChar = '0')}"
        }.distinct()
    }

    private fun ensureImagingForCardiovascular(
        chapter: Icd10Chapter,
        exams: List<Exam>,
    ): List<Exam> {
        if (chapter != Icd10Chapter.CHAPTER_IX) {
            return exams
        }
        if (exams.any { it.category == ExamCategory.IMAGING }) {
            return exams
        }
        val cardiacImaging = Exam(
            name = "心エコー",
            category = ExamCategory.IMAGING,
            typicalFinding = "左室駆出率低下 (架空)",
            referenceRange = "55% 以上 (架空)",
        )
        return listOf(cardiacImaging) + exams
    }

    private fun derivePrevalenceUnit(chapter: Icd10Chapter, isRareDisease: Boolean): PrevalenceUnit = when {
        chapter == Icd10Chapter.CHAPTER_XV -> PrevalenceUnit.PER_BIRTH
        chapter == Icd10Chapter.CHAPTER_XVI -> PrevalenceUnit.PER_BIRTH
        chapter == Icd10Chapter.CHAPTER_XVII -> PrevalenceUnit.PER_BIRTH
        isRareDisease -> PrevalenceUnit.PER_PATIENT
        else -> PrevalenceUnit.PER_POPULATION
    }

    private fun denominatorFor(unit: PrevalenceUnit): Int = when (unit) {
        PrevalenceUnit.PER_BIRTH -> DENOMINATOR_PER_BIRTH
        PrevalenceUnit.PER_PATIENT -> DENOMINATOR_PER_PATIENT
        PrevalenceUnit.PER_POPULATION -> DENOMINATOR_PER_POPULATION
    }

    private fun labelFor(unit: PrevalenceUnit, rate: Int, denominator: Int): String = when (unit) {
        PrevalenceUnit.PER_BIRTH -> "出生 $denominator 対 $rate (架空)"
        PrevalenceUnit.PER_PATIENT -> "患者 $denominator 対 $rate (架空)"
        PrevalenceUnit.PER_POPULATION -> "人口 $denominator 対 $rate (架空)"
    }

    private fun buildSexDistribution(id: String, chapter: Icd10Chapter): SexDistribution {
        if (chapter == Icd10Chapter.CHAPTER_XV) {
            return SexDistribution(maleRatio = 0, femaleRatio = SEX_RATIO_BASE, note = "女性優位 (架空)")
        }
        val sexSeed = stableHash(id = id, slot = DiseaseFieldSlot.EPIDEMIOLOGY_SEX_RATIO.ordinal, index = 0)
        val maleRatio = ValueRangeGenerator.pickInRange(seed = sexSeed, range = SEX_RATIO_RANGE)
        return SexDistribution(maleRatio = maleRatio, femaleRatio = SEX_RATIO_BASE, note = "架空比率")
    }

    private fun primaryExamCategoryFor(chapter: Icd10Chapter): ExamCategory = when (chapter) {
        Icd10Chapter.CHAPTER_I -> ExamCategory.BLOOD_TEST
        Icd10Chapter.CHAPTER_II -> ExamCategory.PATHOLOGY
        Icd10Chapter.CHAPTER_III -> ExamCategory.BLOOD_TEST
        Icd10Chapter.CHAPTER_IV -> ExamCategory.BLOOD_TEST
        Icd10Chapter.CHAPTER_V -> ExamCategory.INTERVIEW
        Icd10Chapter.CHAPTER_VI -> ExamCategory.PHYSIOLOGICAL
        Icd10Chapter.CHAPTER_VII -> ExamCategory.PHYSIOLOGICAL
        Icd10Chapter.CHAPTER_VIII -> ExamCategory.PHYSIOLOGICAL
        Icd10Chapter.CHAPTER_IX -> ExamCategory.IMAGING
        Icd10Chapter.CHAPTER_X -> ExamCategory.IMAGING
        Icd10Chapter.CHAPTER_XI -> ExamCategory.IMAGING
        Icd10Chapter.CHAPTER_XII -> ExamCategory.PATHOLOGY
        Icd10Chapter.CHAPTER_XIII -> ExamCategory.IMAGING
        Icd10Chapter.CHAPTER_XIV -> ExamCategory.IMAGING
        Icd10Chapter.CHAPTER_XV -> ExamCategory.IMAGING
        Icd10Chapter.CHAPTER_XVI -> ExamCategory.IMAGING
        Icd10Chapter.CHAPTER_XVII -> ExamCategory.IMAGING
        Icd10Chapter.CHAPTER_XVIII -> ExamCategory.INTERVIEW
        Icd10Chapter.CHAPTER_XIX -> ExamCategory.IMAGING
        Icd10Chapter.CHAPTER_XX -> ExamCategory.INTERVIEW
        Icd10Chapter.CHAPTER_XXI -> ExamCategory.INTERVIEW
        Icd10Chapter.CHAPTER_XXII -> ExamCategory.INTERVIEW
    }

    private fun primaryDepartmentFor(chapter: Icd10Chapter): MedicalDepartment =
        when (chapter) {
            Icd10Chapter.CHAPTER_I -> MedicalDepartment.INFECTIOUS_DISEASE
            Icd10Chapter.CHAPTER_II -> MedicalDepartment.INTERNAL_MEDICINE
            Icd10Chapter.CHAPTER_III -> MedicalDepartment.INTERNAL_MEDICINE
            Icd10Chapter.CHAPTER_IV -> MedicalDepartment.ENDOCRINOLOGY
            Icd10Chapter.CHAPTER_V -> MedicalDepartment.PSYCHIATRY
            Icd10Chapter.CHAPTER_VI -> MedicalDepartment.NEUROLOGY
            Icd10Chapter.CHAPTER_VII -> MedicalDepartment.OPHTHALMOLOGY
            Icd10Chapter.CHAPTER_VIII -> MedicalDepartment.OTOLARYNGOLOGY
            Icd10Chapter.CHAPTER_IX -> MedicalDepartment.CARDIOLOGY
            Icd10Chapter.CHAPTER_X -> MedicalDepartment.INTERNAL_MEDICINE
            Icd10Chapter.CHAPTER_XI -> MedicalDepartment.GASTROENTEROLOGY
            Icd10Chapter.CHAPTER_XII -> MedicalDepartment.DERMATOLOGY
            Icd10Chapter.CHAPTER_XIII -> MedicalDepartment.ORTHOPEDICS
            Icd10Chapter.CHAPTER_XIV -> MedicalDepartment.UROLOGY
            Icd10Chapter.CHAPTER_XV -> MedicalDepartment.GYNECOLOGY
            Icd10Chapter.CHAPTER_XVI -> MedicalDepartment.PEDIATRICS
            Icd10Chapter.CHAPTER_XVII -> MedicalDepartment.PEDIATRICS
            Icd10Chapter.CHAPTER_XVIII -> MedicalDepartment.INTERNAL_MEDICINE
            Icd10Chapter.CHAPTER_XIX -> MedicalDepartment.SURGERY
            Icd10Chapter.CHAPTER_XX -> MedicalDepartment.EMERGENCY
            Icd10Chapter.CHAPTER_XXI -> MedicalDepartment.INTERNAL_MEDICINE
            Icd10Chapter.CHAPTER_XXII -> MedicalDepartment.INTERNAL_MEDICINE
        }

    private val MAIN_SYMPTOM_RANGE: IntRange = 3..5
    private val ASSOCIATED_SYMPTOM_RANGE: IntRange = 2..3
    private val DIAGNOSTIC_REQUIRED_RANGE: IntRange = 2..3
    private val DIAGNOSTIC_SUPPORTING_RANGE: IntRange = 1..2
    private val REQUIRED_EXAM_RANGE: IntRange = 2..3
    private val SEVERITY_GRADE_RANGE: IntRange = 2..3
    private val TREATMENT_PHARMA_RANGE_REQUIRED: IntRange = 1..2
    private val TREATMENT_PHARMA_RANGE_OPTIONAL: IntRange = 0..2
    private val TREATMENT_NONPHARMA_RANGE: IntRange = 1..2
    private val TREATMENT_ACUTE_RANGE: IntRange = 1..2
    private val PREVENTION_RANGE: IntRange = 1..2
    private val RELATED_DRUG_RANGE: IntRange = 1..2
    private val RELATED_DISEASE_RANGE: IntRange = 1..2
    private val PREVALENCE_RATE_RANGE: IntRange = 1..50
    private val ONSET_MIN_AGE_RANGE: IntRange = 20..60
    private val SEX_RATIO_RANGE: IntRange = 1..4
    private val RISK_FACTOR_RANGE: IntRange = 2..3
    private val DRUG_INDEX_RANGE: IntRange = 0..119
    private val DISEASE_INDEX_RANGE: IntRange = 0..79

    private const val ID_PAD_LENGTH: Int = 4
    private const val ONSET_AGE_SPAN: Int = 10
    private const val DENOMINATOR_PER_BIRTH: Int = 1_000
    private const val DENOMINATOR_PER_PATIENT: Int = 100
    private const val DENOMINATOR_PER_POPULATION: Int = 100_000
    private const val SEX_RATIO_BASE: Int = 1
    private const val GRADING_SYSTEM_LABEL: String = "架空重症度分類"

    private val MAIN_SYMPTOM_VOCAB: List<String> =
        listOf("倦怠感", "発熱", "頭痛", "食欲不振", "体重減少", "疼痛", "動悸", "息切れ")
    private val ASSOCIATED_SYMPTOM_VOCAB: List<String> =
        listOf("悪心", "下痢", "便秘", "めまい", "不眠", "発汗", "浮腫")
    private val EXAM_NAME_VOCAB: List<String> =
        listOf("血液生化学検査", "心電図", "単純X線撮影", "CT検査", "MRI検査", "内視鏡検査", "問診票")
    private val PREVENTION_VOCAB: List<String> =
        listOf("定期健診の受診", "適切な手指衛生", "バランスの取れた食事", "十分な睡眠", "適度な運動")
    private val RISK_FACTOR_VOCAB: List<String> =
        listOf("家族歴", "喫煙", "肥満", "運動不足", "長時間の接触", "飛沫伝播", "媒介生物への曝露")
    private val NONPHARMA_HEADINGS: List<String> =
        listOf("食事療法", "運動療法", "理学療法", "生活指導")
}
