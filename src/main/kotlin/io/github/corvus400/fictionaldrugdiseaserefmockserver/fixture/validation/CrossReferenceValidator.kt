package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug

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

    private const val TYPE_DRUG = "drug"
    private const val TYPE_DISEASE = "disease"
}
