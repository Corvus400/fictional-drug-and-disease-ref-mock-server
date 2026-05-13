package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.AtcIcd10Mapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DISEASE_RELATED_DRUG_IDS_FINAL_OVERRIDE_IDS
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DRUG_RELATED_DISEASE_IDS_FINAL_OVERRIDE_IDS
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.PreventionSeedBuckets
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.RiskFactorSeedBuckets
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.PrecautionPopulationCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.TherapeuticCategory

/**
 * drug↔disease カタログ間の **edge 違反** (存在しない ID への dangling 参照) を表す。
 *
 * 例: "drug_0001 が relatedDiseaseIds で disease_9999 を参照しているが
 *      disease_9999 は fixture に存在しない"。
 * [CrossReferenceValidator.validate] の戻り値要素。
 *
 * ## [FixtureViolation] と統合していない理由
 *
 * 単一 fixture の node 違反 (フィールド単位の不整合) は [FixtureViolation] で扱う。
 * 共通は sourceType/sourceId (= entityType/entityId) の 2 フィールドだけで、
 * こちらは `targetType` + `danglingTargetId` という ID 構造化形状、
 * [FixtureViolation] は `field` + 自由文 `message` の人間向け形状で
 * 情報の持ち方が本質的に異なる。詳細な統合可否の検討は [FixtureViolation] の KDoc を参照。
 */
data class CrossRefViolation(
    val sourceType: String,
    val sourceId: String,
    val targetType: String,
    val danglingTargetId: String,
)

object CrossReferenceValidator {
    fun validate(
        drugs: List<Drug>,
        diseases: List<Disease>,
    ): List<CrossRefViolation> {
        val drugIds: Set<String> = drugs.map { it.id }.toSet()
        val diseaseIds: Set<String> = diseases.map { it.id }.toSet()
        return collectDangling(
            sources = drugs,
            sourceIdOf = Drug::id,
            relatedIdsOf = Drug::relatedDiseaseIds,
            validTargetIds = diseaseIds,
            sourceType = TYPE_DRUG,
            targetType = TYPE_DISEASE,
        ) + collectDangling(
            sources = diseases,
            sourceIdOf = Disease::id,
            relatedIdsOf = Disease::relatedDrugIds,
            validTargetIds = drugIds,
            sourceType = TYPE_DISEASE,
            targetType = TYPE_DRUG,
        ) + collectDangling(
            sources = diseases,
            sourceIdOf = Disease::id,
            relatedIdsOf = Disease::relatedDiseaseIds,
            validTargetIds = diseaseIds,
            sourceType = TYPE_DISEASE,
            targetType = TYPE_DISEASE,
        ) + collectChapterFiveWithoutPsychotropicDrug(
            drugs = drugs,
            diseases = diseases,
        ) + collectChapterFifteenWithPregnancyContraindicatedDrug(
            drugs = drugs,
            diseases = diseases,
        ) + collectDrugToDiseaseSemanticMismatch(
            drugs = drugs,
            diseases = diseases,
        ) + collectDiseaseToDrugSemanticMismatch(
            drugs = drugs,
            diseases = diseases,
        ) + collectDiseaseFieldSemanticMismatch(
            diseases = diseases,
        )
    }

    private fun <S> collectDangling(
        sources: List<S>,
        sourceIdOf: (S) -> String,
        relatedIdsOf: (S) -> List<String>,
        validTargetIds: Set<String>,
        sourceType: String,
        targetType: String,
    ): List<CrossRefViolation> =
        sources.flatMap { source ->
            relatedIdsOf(source)
                .filter { relatedId -> relatedId !in validTargetIds }
                .map { danglingId ->
                    CrossRefViolation(
                        sourceType = sourceType,
                        sourceId = sourceIdOf(source),
                        targetType = targetType,
                        danglingTargetId = danglingId,
                    )
                }
        }

    private fun collectChapterFiveWithoutPsychotropicDrug(
        drugs: List<Drug>,
        diseases: List<Disease>,
    ): List<CrossRefViolation> {
        val psychotropicDrugIds =
            drugs
                .filter { drug ->
                    drug.regulatoryClass.any { regulatoryClass ->
                        regulatoryClass in PSYCHOTROPIC_CLASSES
                    }
                }
                .map { drug -> drug.id }
                .toSet()
        return diseases
            .filter { disease -> disease.icd10Chapter == Icd10Chapter.CHAPTER_V }
            .filter { disease ->
                disease.relatedDrugIds.none { relatedDrugId ->
                    relatedDrugId in psychotropicDrugIds
                }
            }
            .map { disease ->
                CrossRefViolation(
                    sourceType = TYPE_DISEASE,
                    sourceId = disease.id,
                    targetType = TYPE_DRUG,
                    danglingTargetId = REQUIRED_PSYCHOTROPIC_DRUG,
                )
            }
    }

    private fun collectChapterFifteenWithPregnancyContraindicatedDrug(
        drugs: List<Drug>,
        diseases: List<Disease>,
    ): List<CrossRefViolation> {
        val pregnancyContraindicatedDrugIds =
            drugs
                .filter { drug ->
                    drug.precautionsForSpecificPopulations.any { precaution ->
                        precaution.category == PrecautionPopulationCategory.PREGNANT
                    }
                }
                .map { drug -> drug.id }
                .toSet()
        return diseases
            .filter { disease -> disease.icd10Chapter == Icd10Chapter.CHAPTER_XV }
            .flatMap { disease ->
                disease.relatedDrugIds
                    .filter { drugId -> drugId in pregnancyContraindicatedDrugIds }
                    .map { drugId ->
                        CrossRefViolation(
                            sourceType = TYPE_DISEASE,
                            sourceId = disease.id,
                            targetType = TYPE_DRUG,
                            danglingTargetId = drugId,
                        )
                    }
            }
    }

    private fun collectDrugToDiseaseSemanticMismatch(
        drugs: List<Drug>,
        diseases: List<Disease>,
    ): List<CrossRefViolation> {
        val diseaseById = diseases.associateBy { disease -> disease.id }
        return drugs
            .filterNot { drug -> drug.id in DRUG_RELATED_DISEASE_IDS_FINAL_OVERRIDE_IDS }
            .flatMap { drug ->
                val category = categoryOf(drug = drug) ?: return@flatMap emptyList()
                val compatibleChapters = AtcIcd10Mapping.chaptersFor(category = category).toSet()
                drug.relatedDiseaseIds.mapNotNull { diseaseId ->
                    val disease = diseaseById[diseaseId] ?: return@mapNotNull null
                    if (disease.icd10Chapter in compatibleChapters) {
                        null
                    } else {
                        CrossRefViolation(
                            sourceType = TYPE_DRUG,
                            sourceId = drug.id,
                            targetType = TYPE_DISEASE_CHAPTER_MISMATCH,
                            danglingTargetId = diseaseId,
                        )
                    }
                }
            }
    }

    private fun collectDiseaseToDrugSemanticMismatch(
        drugs: List<Drug>,
        diseases: List<Disease>,
    ): List<CrossRefViolation> {
        val drugById = drugs.associateBy { drug -> drug.id }
        return diseases
            .filterNot { disease -> disease.id in DISEASE_RELATED_DRUG_IDS_FINAL_OVERRIDE_IDS }
            .filter { disease -> AtcIcd10Mapping.categoriesFor(chapter = disease.icd10Chapter).isNotEmpty() }
            .flatMap { disease ->
                disease.relatedDrugIds.mapNotNull { drugId ->
                    val drug = drugById[drugId] ?: return@mapNotNull null
                    val category = categoryOf(drug = drug) ?: return@mapNotNull null
                    val compatibleChapters = AtcIcd10Mapping.chaptersFor(category = category)
                    if (disease.icd10Chapter in compatibleChapters) {
                        null
                    } else {
                        CrossRefViolation(
                            sourceType = TYPE_DISEASE,
                            sourceId = disease.id,
                            targetType = TYPE_DRUG_ATC_MISMATCH,
                            danglingTargetId = drugId,
                        )
                    }
                }
            }
    }

    private fun categoryOf(drug: Drug): TherapeuticCategory? =
        drug.atcCode.firstOrNull()?.let(TherapeuticCategory::fromAtcInitial)

    private fun collectDiseaseFieldSemanticMismatch(diseases: List<Disease>): List<CrossRefViolation> =
        diseases.flatMap { disease ->
            collectRiskFactorSemanticMismatch(disease = disease) +
                collectPreventionSemanticMismatch(disease = disease) +
                collectChapterOneOnlyTextMismatch(
                    disease = disease,
                    targetType = TYPE_DISEASE_SUMMARY_CHAPTER_MISMATCH,
                    fieldPath = "summary",
                    textValues = listOf(disease.summary, disease.etiology),
                ) +
                collectChapterOneOnlyTextMismatch(
                    disease = disease,
                    targetType = TYPE_DISEASE_DIAGNOSTIC_CHAPTER_MISMATCH,
                    fieldPath = "diagnosticCriteria",
                    textValues =
                    disease.diagnosticCriteria.required +
                        disease.diagnosticCriteria.supporting +
                        listOfNotNull(disease.diagnosticCriteria.notes),
                ) +
                collectChapterOneOnlyTextMismatch(
                    disease = disease,
                    targetType = TYPE_DISEASE_TREATMENT_CHAPTER_MISMATCH,
                    fieldPath = "treatments",
                    textValues = treatmentTextValues(disease = disease),
                ) +
                collectChapterOneOnlyTextMismatch(
                    disease = disease,
                    targetType = TYPE_DISEASE_PROGNOSIS_CHAPTER_MISMATCH,
                    fieldPath = "prognosis",
                    textValues = listOfNotNull(disease.prognosis),
                ) +
                collectChapterOneOnlyTextMismatch(
                    disease = disease,
                    targetType = TYPE_DISEASE_SEVERITY_CHAPTER_MISMATCH,
                    fieldPath = "severityGrading",
                    textValues = severityTextValues(disease = disease),
                )
        }

    private fun collectRiskFactorSemanticMismatch(disease: Disease): List<CrossRefViolation> {
        val riskFactors = disease.epidemiology?.riskFactors.orEmpty()
        val chapterIOnlyFactors = RiskFactorSeedBuckets.infectionExclusiveFactors()
        val mismatches =
            if (disease.icd10Chapter == Icd10Chapter.CHAPTER_I) {
                if (riskFactors.none { riskFactor -> riskFactor in chapterIOnlyFactors }) {
                    listOf("epidemiology.riskFactors:<missing-chapter-I-infection-risk-factor>")
                } else {
                    emptyList()
                }
            } else {
                riskFactors
                    .filter { riskFactor -> riskFactor in chapterIOnlyFactors }
                    .map { riskFactor -> "epidemiology.riskFactors:$riskFactor" }
            }
        return mismatches.map { mismatch ->
            diseaseFieldViolation(
                disease = disease,
                targetType = TYPE_DISEASE_RISK_FACTOR_CHAPTER_MISMATCH,
                value = mismatch,
            )
        }
    }

    private fun collectPreventionSemanticMismatch(disease: Disease): List<CrossRefViolation> {
        val expectedItems =
            PreventionSeedBuckets.preventionFor(chapter = disease.icd10Chapter).toSet() +
                OVERRIDE_ALLOWED_PREVENTION_ITEMS.getValue(disease.id)
        return disease.prevention
            .filter { item -> item !in expectedItems }
            .map { item ->
                diseaseFieldViolation(
                    disease = disease,
                    targetType = TYPE_DISEASE_PREVENTION_CHAPTER_MISMATCH,
                    value = "prevention:$item",
                )
            }
    }

    private fun collectChapterOneOnlyTextMismatch(
        disease: Disease,
        targetType: String,
        fieldPath: String,
        textValues: List<String>,
    ): List<CrossRefViolation> {
        if (disease.icd10Chapter == Icd10Chapter.CHAPTER_I) {
            return emptyList()
        }
        return textValues
            .flatMap { text ->
                CHAPTER_I_ONLY_TEXT_MARKERS
                    .filter { marker -> marker in text }
                    .map { marker ->
                        diseaseFieldViolation(
                            disease = disease,
                            targetType = targetType,
                            value = "$fieldPath:$marker",
                        )
                    }
            }
    }

    private fun treatmentTextValues(disease: Disease): List<String> =
        disease.treatments.pharmacological.flatMap { treatment ->
            listOf(treatment.drugCategory, treatment.indication, treatment.notes)
        } + disease.treatments.nonPharmacological.flatMap { section ->
            listOf(section.heading) + section.items + listOfNotNull(section.description)
        } + disease.treatments.acutePhaseProtocol.flatMap { step ->
            listOf(step.action) + listOfNotNull(step.target)
        }

    private fun severityTextValues(disease: Disease): List<String> {
        val severity = disease.severityGrading ?: return emptyList()
        return listOf(severity.gradingSystem) +
            severity.grades.flatMap { grade ->
                listOf(grade.label, grade.criteria, grade.recommendedAction)
            }
    }

    private fun diseaseFieldViolation(
        disease: Disease,
        targetType: String,
        value: String,
    ): CrossRefViolation =
        CrossRefViolation(
            sourceType = TYPE_DISEASE,
            sourceId = disease.id,
            targetType = targetType,
            danglingTargetId = value,
        )

    private const val TYPE_DRUG = "drug"
    private const val TYPE_DISEASE = "disease"
    private const val TYPE_DISEASE_CHAPTER_MISMATCH = "disease_chapter_mismatch"
    private const val TYPE_DRUG_ATC_MISMATCH = "drug_atc_mismatch"
    private const val TYPE_DISEASE_RISK_FACTOR_CHAPTER_MISMATCH = "disease_risk_factor_chapter_mismatch"
    private const val TYPE_DISEASE_PREVENTION_CHAPTER_MISMATCH = "disease_prevention_chapter_mismatch"
    private const val TYPE_DISEASE_SUMMARY_CHAPTER_MISMATCH = "disease_summary_chapter_mismatch"
    private const val TYPE_DISEASE_DIAGNOSTIC_CHAPTER_MISMATCH = "disease_diagnostic_chapter_mismatch"
    private const val TYPE_DISEASE_TREATMENT_CHAPTER_MISMATCH = "disease_treatment_chapter_mismatch"
    private const val TYPE_DISEASE_PROGNOSIS_CHAPTER_MISMATCH = "disease_prognosis_chapter_mismatch"
    private const val TYPE_DISEASE_SEVERITY_CHAPTER_MISMATCH = "disease_severity_chapter_mismatch"
    private const val REQUIRED_PSYCHOTROPIC_DRUG = "psychotropic_drug"

    private val CHAPTER_I_ONLY_TEXT_MARKERS: Set<String> =
        setOf(
            "感染性疾患",
            "病原体関連疾患",
            "伝播性疾患",
            "感染症に整合する",
            "感染症評価パネル",
            "感染症管理",
            "抗微生物薬適正使用",
            "病原体量の早期低下",
            "感染制御",
            "感染症重症度分類",
        )

    private val OVERRIDE_ALLOWED_PREVENTION_ITEMS: Map<String, Set<String>> =
        mapOf(
            "disease_0022" to
                setOf(
                    "規則的な睡眠覚醒リズムの維持 (架空)",
                    "就寝前の刺激物と強い光の回避 (架空)",
                    "ストレス管理と適度な運動 (架空)",
                ),
            "disease_0079" to
                setOf(
                    "強いストレス・トラウマ刺激の回避 (架空)",
                    "悪意・不信感の拡散抑制 (架空)",
                    "全国検査による魔女因子高値の早期発見 (架空)",
                    "15 歳以前の精神安定環境の維持 (架空)",
                ),
        ).withDefault { emptySet() }

    private val PSYCHOTROPIC_CLASSES: Set<RegulatoryClass> =
        setOf(
            RegulatoryClass.PSYCHOTROPIC_1,
            RegulatoryClass.PSYCHOTROPIC_2,
            RegulatoryClass.PSYCHOTROPIC_3,
        )
}
