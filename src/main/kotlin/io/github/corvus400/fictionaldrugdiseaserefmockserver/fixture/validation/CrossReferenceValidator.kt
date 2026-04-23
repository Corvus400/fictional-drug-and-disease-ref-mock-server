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
        val drugSide = drugs.flatMap { drug ->
            drug.relatedDiseaseIds
                .filter { relatedId -> relatedId !in diseaseIds }
                .map { danglingId ->
                    CrossRefViolation(
                        sourceType = TYPE_DRUG,
                        sourceId = drug.id,
                        targetType = TYPE_DISEASE,
                        danglingTargetId = danglingId,
                    )
                }
        }
        val diseaseToDrugSide = diseases.flatMap { disease ->
            disease.relatedDrugIds
                .filter { relatedId -> relatedId !in drugIds }
                .map { danglingId ->
                    CrossRefViolation(
                        sourceType = TYPE_DISEASE,
                        sourceId = disease.id,
                        targetType = TYPE_DRUG,
                        danglingTargetId = danglingId,
                    )
                }
        }
        return drugSide + diseaseToDrugSide
    }

    private const val TYPE_DRUG = "drug"
    private const val TYPE_DISEASE = "disease"
}
