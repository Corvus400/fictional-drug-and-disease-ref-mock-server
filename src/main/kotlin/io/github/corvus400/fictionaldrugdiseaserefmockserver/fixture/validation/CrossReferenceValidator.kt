package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.PrecautionPopulationCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass

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

    private const val TYPE_DRUG = "drug"
    private const val TYPE_DISEASE = "disease"
    private const val REQUIRED_PSYCHOTROPIC_DRUG = "psychotropic_drug"

    private val PSYCHOTROPIC_CLASSES: Set<RegulatoryClass> =
        setOf(
            RegulatoryClass.PSYCHOTROPIC_1,
            RegulatoryClass.PSYCHOTROPIC_2,
            RegulatoryClass.PSYCHOTROPIC_3,
        )
}
