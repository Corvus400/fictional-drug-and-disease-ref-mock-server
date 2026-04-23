package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

data class DiseaseViolation(
    val diseaseId: String,
    val field: String,
    val message: String,
)

object DiseaseFixtureValidator {
    fun validate(diseases: List<Disease>): List<DiseaseViolation> {
        return checkFieldMinimumCounts(diseases = diseases) +
            checkConditionalFields(diseases = diseases) +
            checkIdUniqueness(diseases = diseases)
    }

    private fun checkConditionalFields(diseases: List<Disease>): List<DiseaseViolation> {
        return diseases.flatMap { disease -> conditionalFieldViolationsFor(disease = disease) }
    }

    private fun conditionalFieldViolationsFor(disease: Disease): List<DiseaseViolation> {
        return when (disease.icd10Chapter) {
            Icd10Chapter.CHAPTER_I -> chapterOneViolations(disease = disease)
            Icd10Chapter.CHAPTER_II -> chapterTwoViolations(disease = disease)
            Icd10Chapter.CHAPTER_V -> chapterFiveViolations(disease = disease)
            Icd10Chapter.CHAPTER_III,
            Icd10Chapter.CHAPTER_IV,
            Icd10Chapter.CHAPTER_VI,
            Icd10Chapter.CHAPTER_VII,
            Icd10Chapter.CHAPTER_VIII,
            Icd10Chapter.CHAPTER_IX,
            Icd10Chapter.CHAPTER_X,
            Icd10Chapter.CHAPTER_XI,
            Icd10Chapter.CHAPTER_XII,
            Icd10Chapter.CHAPTER_XIII,
            Icd10Chapter.CHAPTER_XIV,
            Icd10Chapter.CHAPTER_XV,
            Icd10Chapter.CHAPTER_XVI,
            Icd10Chapter.CHAPTER_XVII,
            Icd10Chapter.CHAPTER_XVIII,
            Icd10Chapter.CHAPTER_XIX,
            Icd10Chapter.CHAPTER_XX,
            Icd10Chapter.CHAPTER_XXI,
            Icd10Chapter.CHAPTER_XXII,
            -> emptyList()
        }
    }

    private fun chapterOneViolations(disease: Disease): List<DiseaseViolation> {
        return buildList {
            if (!disease.infectious) {
                add(
                    DiseaseViolation(
                        diseaseId = disease.id,
                        field = "infectious",
                        message = "CHAPTER_I disease must have infectious=true",
                    ),
                )
            }
            if (disease.epidemiology == null) {
                add(
                    DiseaseViolation(
                        diseaseId = disease.id,
                        field = "epidemiology",
                        message = "CHAPTER_I disease must have epidemiology populated",
                    ),
                )
            }
        }
    }

    private fun chapterTwoViolations(disease: Disease): List<DiseaseViolation> {
        return buildList {
            if (disease.severityGrading == null) {
                add(
                    DiseaseViolation(
                        diseaseId = disease.id,
                        field = "severityGrading",
                        message = "CHAPTER_II disease must have severityGrading populated",
                    ),
                )
            }
            val prognosis = disease.prognosis
            if (prognosis == null || prognosis.isBlank()) {
                add(
                    DiseaseViolation(
                        diseaseId = disease.id,
                        field = "prognosis",
                        message = "CHAPTER_II disease must have non-blank prognosis",
                    ),
                )
            }
        }
    }

    private fun chapterFiveViolations(disease: Disease): List<DiseaseViolation> {
        return buildList {
            if (disease.diagnosticCriteria.required.isEmpty()) {
                add(
                    DiseaseViolation(
                        diseaseId = disease.id,
                        field = "diagnosticCriteria.required",
                        message = "CHAPTER_V disease must have diagnosticCriteria.required populated",
                    ),
                )
            }
            if (disease.symptoms.mainSymptoms.size < MIN_CHAPTER_V_MAIN_SYMPTOMS) {
                add(
                    DiseaseViolation(
                        diseaseId = disease.id,
                        field = "symptoms.mainSymptoms",
                        message = "CHAPTER_V disease must have at least " +
                            "$MIN_CHAPTER_V_MAIN_SYMPTOMS mainSymptoms",
                    ),
                )
            }
        }
    }

    private const val MIN_CHAPTER_V_MAIN_SYMPTOMS: Int = 3

    private fun checkFieldMinimumCounts(diseases: List<Disease>): List<DiseaseViolation> {
        return diseases.flatMap { disease -> fieldMinimumCountViolationsFor(disease = disease) }
    }

    private fun fieldMinimumCountViolationsFor(disease: Disease): List<DiseaseViolation> {
        return buildList {
            if (disease.symptoms.mainSymptoms.isEmpty()) {
                add(
                    DiseaseViolation(
                        diseaseId = disease.id,
                        field = "symptoms.mainSymptoms",
                        message = "mainSymptoms must have at least 1 entry",
                    ),
                )
            }
            if (disease.requiredExams.isEmpty()) {
                add(
                    DiseaseViolation(
                        diseaseId = disease.id,
                        field = "requiredExams",
                        message = "requiredExams must have at least 1 entry",
                    ),
                )
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
