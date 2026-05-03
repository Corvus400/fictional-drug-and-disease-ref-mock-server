package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object DiseaseFixtureValidator {
    private const val ENTITY_TYPE: String = "disease"

    fun validate(diseases: List<Disease>): List<FixtureViolation> {
        return checkFieldMinimumCounts(diseases = diseases) +
            checkConditionalFields(diseases = diseases) +
            checkIdUniqueness(diseases = diseases) +
            checkIdSequential(diseases = diseases)
    }

    private fun checkIdSequential(diseases: List<Disease>): List<FixtureViolation> {
        val observed = mutableSetOf<Int>()
        val violations = mutableListOf<FixtureViolation>()
        for (disease in diseases) {
            val match = DISEASE_ID_PATTERN.matchEntire(input = disease.id)
            if (match == null) {
                violations.add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "id",
                        message = "id must match 'disease_NNNN' pattern",
                    ),
                )
            } else {
                observed.add(match.groupValues[1].toInt())
            }
        }
        val expectedSize = diseases.size
        for (expected in 0 until expectedSize) {
            if (expected !in observed) {
                violations.add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = formatDiseaseId(index = expected),
                        field = "id",
                        message = "sequential id missing from 0..${expectedSize - 1}",
                    ),
                )
            }
        }
        for (value in observed) {
            if (value >= expectedSize) {
                violations.add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = formatDiseaseId(index = value),
                        field = "id",
                        message = "sequential id out of range 0..${expectedSize - 1}",
                    ),
                )
            }
        }
        return violations.toList()
    }

    private fun formatDiseaseId(index: Int): String {
        return "disease_${index.toString().padStart(length = DISEASE_ID_PAD_LENGTH, padChar = '0')}"
    }

    private fun checkConditionalFields(diseases: List<Disease>): List<FixtureViolation> {
        return diseases.flatMap { disease -> conditionalFieldViolationsFor(disease = disease) }
    }

    private fun conditionalFieldViolationsFor(disease: Disease): List<FixtureViolation> {
        val chapterViolations = when (disease.icd10Chapter) {
            Icd10Chapter.CHAPTER_I -> chapterOneViolations(disease = disease)
            Icd10Chapter.CHAPTER_II -> chapterTwoViolations(disease = disease)
            Icd10Chapter.CHAPTER_IV -> chapterFourViolations(disease = disease)
            Icd10Chapter.CHAPTER_V -> chapterFiveViolations(disease = disease)
            Icd10Chapter.CHAPTER_IX -> chapterNineViolations(disease = disease)
            Icd10Chapter.CHAPTER_XV -> chapterFifteenViolations(disease = disease)
            Icd10Chapter.CHAPTER_III,
            Icd10Chapter.CHAPTER_VI,
            Icd10Chapter.CHAPTER_VII,
            Icd10Chapter.CHAPTER_VIII,
            Icd10Chapter.CHAPTER_X,
            Icd10Chapter.CHAPTER_XI,
            Icd10Chapter.CHAPTER_XII,
            Icd10Chapter.CHAPTER_XIII,
            Icd10Chapter.CHAPTER_XIV,
            Icd10Chapter.CHAPTER_XVI,
            Icd10Chapter.CHAPTER_XVII,
            Icd10Chapter.CHAPTER_XVIII,
            Icd10Chapter.CHAPTER_XIX,
            Icd10Chapter.CHAPTER_XX,
            Icd10Chapter.CHAPTER_XXI,
            Icd10Chapter.CHAPTER_XXII,
            -> emptyList()
        }
        return preventionViolations(disease = disease) + chapterViolations
    }

    private fun preventionViolations(disease: Disease): List<FixtureViolation> {
        val hasRiskFactors = disease.epidemiology?.riskFactors?.isNotEmpty() == true
        if ((disease.infectious || hasRiskFactors) && disease.prevention.isEmpty()) {
            return listOf(
                FixtureViolation(
                    entityType = ENTITY_TYPE,
                    entityId = disease.id,
                    field = "prevention",
                    message = "infectious=true or non-empty riskFactors requires prevention size >= 1",
                ),
            )
        }
        return emptyList()
    }

    private fun chapterOneViolations(disease: Disease): List<FixtureViolation> {
        return buildList {
            if (!disease.infectious) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "infectious",
                        message = "CHAPTER_I disease must have infectious=true",
                    ),
                )
            }
            if (disease.epidemiology == null) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "epidemiology",
                        message = "CHAPTER_I disease must have epidemiology populated",
                    ),
                )
            }
            if (disease.symptoms.onsetPattern == null) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "symptoms.onsetPattern",
                        message = "CHAPTER_I disease must have non-null onsetPattern",
                    ),
                )
            }
        }
    }

    private fun chapterTwoViolations(disease: Disease): List<FixtureViolation> {
        return buildList {
            if (disease.severityGrading == null) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "severityGrading",
                        message = "CHAPTER_II disease must have severityGrading populated",
                    ),
                )
            }
            val prognosis = disease.prognosis
            if (prognosis == null || prognosis.isBlank()) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "prognosis",
                        message = "CHAPTER_II disease must have non-blank prognosis",
                    ),
                )
            }
        }
    }

    private fun chapterFourViolations(disease: Disease): List<FixtureViolation> {
        return buildList {
            if (disease.treatments.pharmacological.isEmpty()) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "treatments.pharmacological",
                        message = "CHAPTER_IV disease must have at least 1 pharmacological treatment",
                    ),
                )
            }
        }
    }

    private fun chapterFiveViolations(disease: Disease): List<FixtureViolation> {
        return buildList {
            if (disease.symptoms.mainSymptoms.size < MIN_CHAPTER_V_MAIN_SYMPTOMS) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "symptoms.mainSymptoms",
                        message = "CHAPTER_V disease must have at least " +
                            "$MIN_CHAPTER_V_MAIN_SYMPTOMS mainSymptoms",
                    ),
                )
            }
        }
    }

    private fun chapterNineViolations(disease: Disease): List<FixtureViolation> {
        return buildList {
            if (disease.severityGrading == null) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "severityGrading",
                        message = "CHAPTER_IX disease must have severityGrading populated",
                    ),
                )
            }
            if (disease.requiredExams.none { exam -> exam.category == ExamCategory.IMAGING }) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "requiredExams",
                        message = "CHAPTER_IX disease must include at least one IMAGING exam",
                    ),
                )
            }
        }
    }

    private fun chapterFifteenViolations(disease: Disease): List<FixtureViolation> {
        return buildList {
            val epidemiology = disease.epidemiology
            if (epidemiology == null) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "epidemiology",
                        message = "CHAPTER_XV disease must have epidemiology populated",
                    ),
                )
                return@buildList
            }
            if (epidemiology.onsetAgeRange == null) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "epidemiology.onsetAgeRange",
                        message = "CHAPTER_XV disease must have onsetAgeRange populated",
                    ),
                )
            }
            if (epidemiology.sexRatio == null) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "epidemiology.sexRatio",
                        message = "CHAPTER_XV disease must have sexRatio populated",
                    ),
                )
            }
        }
    }

    private const val MIN_CHAPTER_V_MAIN_SYMPTOMS: Int = 3
    private const val MIN_SEVERITY_GRADING_GRADES: Int = 2
    private const val DISEASE_ID_PAD_LENGTH: Int = 4
    private val DISEASE_ID_PATTERN: Regex = Regex(pattern = """^disease_(\d{4})$""")

    private fun checkFieldMinimumCounts(diseases: List<Disease>): List<FixtureViolation> {
        return diseases.flatMap { disease -> fieldMinimumCountViolationsFor(disease = disease) }
    }

    private fun fieldMinimumCountViolationsFor(disease: Disease): List<FixtureViolation> {
        return buildList {
            if (disease.symptoms.mainSymptoms.isEmpty()) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "symptoms.mainSymptoms",
                        message = "mainSymptoms must have at least 1 entry",
                    ),
                )
            }
            if (disease.requiredExams.isEmpty()) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "requiredExams",
                        message = "requiredExams must have at least 1 entry",
                    ),
                )
            }
            if (disease.diagnosticCriteria.required.isEmpty()) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "diagnosticCriteria.required",
                        message = "diagnosticCriteria.required must have at least 1 entry",
                    ),
                )
            }
            if (disease.medicalDepartment.isEmpty()) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "medicalDepartment",
                        message = "medicalDepartment must have at least 1 entry",
                    ),
                )
            }
            val severityGrading = disease.severityGrading
            if (severityGrading != null && severityGrading.grades.size < MIN_SEVERITY_GRADING_GRADES) {
                add(
                    FixtureViolation(
                        entityType = ENTITY_TYPE,
                        entityId = disease.id,
                        field = "severityGrading.grades",
                        message = "severityGrading.grades must have at least " +
                            "$MIN_SEVERITY_GRADING_GRADES entries when severityGrading is non-null",
                    ),
                )
            }
        }
    }

    private fun checkIdUniqueness(diseases: List<Disease>): List<FixtureViolation> {
        return diseases
            .groupingBy { disease -> disease.id }
            .eachCount()
            .filterValues { count -> count > 1 }
            .map { (duplicatedId, count) ->
                FixtureViolation(
                    entityType = ENTITY_TYPE,
                    entityId = duplicatedId,
                    field = "id",
                    message = "id must be unique but appears $count times",
                )
            }
    }
}
