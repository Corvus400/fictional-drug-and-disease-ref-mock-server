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
        val diseaseIds: Set<String> = diseases.map { it.id }.toSet()
        return drugs.flatMap { drug ->
            drug.relatedDiseaseIds
                .filter { relatedId -> relatedId !in diseaseIds }
                .map { danglingId ->
                    CrossRefViolation(
                        sourceType = SOURCE_TYPE_DRUG,
                        sourceId = drug.id,
                        targetType = TARGET_TYPE_DISEASE,
                        danglingTargetId = danglingId,
                    )
                }
        }
    }

    private const val SOURCE_TYPE_DRUG = "drug"
    private const val TARGET_TYPE_DISEASE = "disease"
}
