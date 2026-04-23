package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease

data class DiseaseViolation(
    val diseaseId: String,
    val field: String,
    val message: String,
)

object DiseaseFixtureValidator {
    fun validate(diseases: List<Disease>): List<DiseaseViolation> {
        return checkFieldMinimumCounts(diseases = diseases) +
            checkIdUniqueness(diseases = diseases)
    }

    private fun checkFieldMinimumCounts(diseases: List<Disease>): List<DiseaseViolation> {
        return diseases.mapNotNull { disease ->
            if (disease.symptoms.mainSymptoms.isEmpty()) {
                DiseaseViolation(
                    diseaseId = disease.id,
                    field = "symptoms.mainSymptoms",
                    message = "mainSymptoms must have at least 1 entry",
                )
            } else {
                null
            }
        }
    }

    private fun checkIdUniqueness(diseases: List<Disease>): List<DiseaseViolation> {
        return diseases
            .groupingBy { disease -> disease.id }
            .eachCount()
            .filterValues { count -> count > 1 }
            .map { (duplicatedId, count) ->
                DiseaseViolation(
                    diseaseId = duplicatedId,
                    field = "id",
                    message = "id must be unique but appears $count times",
                )
            }
    }
}
