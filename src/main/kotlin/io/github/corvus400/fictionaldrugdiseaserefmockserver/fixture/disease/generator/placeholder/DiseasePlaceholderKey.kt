package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

enum class DiseasePlaceholderCategory {
    A_MEDICAL_VOCABULARY,
    B_SELF_REFERENCE,
    D_NUMERIC_RANGE,
}

enum class DiseasePlaceholderKey(
    val jsonKey: String,
    val category: DiseasePlaceholderCategory,
) {
    ACUTE_TREATMENT("acuteTreatment", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    ADDITIONAL_SYMPTOM("additionalSymptom", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    ADJUNCT_TREATMENT("adjunctTreatment", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    AGE_GROUP("ageGroup", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    ASSOCIATED_SYMPTOM("associatedSymptom", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    CHRONICITY("chronicity", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    CLINICAL_FINDING("clinicalFinding", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    DIAGNOSTIC_TEST("diagnosticTest", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    DIFFERENTIAL_CONDITION("differentialCondition", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    ESCALATED_ACTION("escalatedAction", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    ETIOLOGY_CATEGORY("etiologyCategory", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    FAVORABLE_FACTOR("favorableFactor", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    FAVORABLE_OUTCOME("favorableOutcome", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    FOLLOW_UP_EXAM("followUpExam", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    GRADING_SYSTEM("gradingSystem", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    INITIAL_SYMPTOM("initialSymptom", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    MAIN_FEATURE("mainFeature", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    MAIN_SYMPTOM("mainSymptom", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    MAINTENANCE_TREATMENT("maintenanceTreatment", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    ONSET_PATTERN("onsetPattern", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    ORGAN_SYSTEM("organSystem", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    PREVALENCE_LABEL("prevalenceLabel", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    PRIMARY_FINDING("primaryFinding", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    PROGNOSIS_INDICATOR("prognosisIndicator", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    PROGNOSTIC_FACTOR("prognosticFactor", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    PROGRESSED_COMPLICATION("progressedComplication", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    PROGRESSED_SYMPTOM("progressedSymptom", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    REGIONAL_NOTE("regionalNote", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    RISK_FACTOR("riskFactor", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    SEASONAL_NOTE("seasonalNote", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    SECOND_LINE_TREATMENT("secondLineTreatment", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    SEVERE_GRADE_LABEL("severeGradeLabel", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    SEVERITY_INDICATOR("severityIndicator", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    SEX_DOMINANCE("sexDominance", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    SPECIALIST_REFERRAL("specialistReferral", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    SUPPORTING_FINDING("supportingFinding", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    SYMPTOM_TRIGGER_CONDITION("symptomTriggerCondition", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),
    TREATMENT_CATEGORY("treatmentCategory", DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY),

    DISEASE("disease", DiseasePlaceholderCategory.B_SELF_REFERENCE),

    ANNUAL_INCIDENCE("annualIncidence", DiseasePlaceholderCategory.D_NUMERIC_RANGE),
    EVALUATION_DURATION("evaluationDuration", DiseasePlaceholderCategory.D_NUMERIC_RANGE),
    GRADE_COUNT("gradeCount", DiseasePlaceholderCategory.D_NUMERIC_RANGE),
    PEAK_AGE_YEARS("peakAgeYears", DiseasePlaceholderCategory.D_NUMERIC_RANGE),
    PREVALENCE_RATE("prevalenceRate", DiseasePlaceholderCategory.D_NUMERIC_RANGE),
    PROGNOSIS_RATE("prognosisRate", DiseasePlaceholderCategory.D_NUMERIC_RANGE),
    SEVERITY_THRESHOLD("severityThreshold", DiseasePlaceholderCategory.D_NUMERIC_RANGE),
    SEX_RATIO("sexRatio", DiseasePlaceholderCategory.D_NUMERIC_RANGE),
    SUPPORTING_FINDING_COUNT("supportingFindingCount", DiseasePlaceholderCategory.D_NUMERIC_RANGE),
    ;

    companion object {
        private val BY_JSON_KEY: Map<String, DiseasePlaceholderKey> = values().associateBy { it.jsonKey }

        fun fromJsonKey(jsonKey: String): DiseasePlaceholderKey? = BY_JSON_KEY[jsonKey]
    }
}
