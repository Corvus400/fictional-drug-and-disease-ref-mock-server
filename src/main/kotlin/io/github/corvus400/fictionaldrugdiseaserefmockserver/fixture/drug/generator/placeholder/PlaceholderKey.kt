package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder

enum class PlaceholderCategory {
    A_MEDICAL_VOCABULARY,
    B_COINED_NAME,
    C_DISEASE_REFERENCE,
    D_NUMERIC_RANGE,
}

enum class PlaceholderKey(
    val jsonKey: String,
    val category: PlaceholderCategory,
) {
    ACTION("action", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    ADVERSE_REACTION("adverseReaction", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    AGE_GROUP("ageGroup", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    COMORBIDITY("comorbidity", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    COUNTERMEASURE("countermeasure", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    DRUG_CATEGORY("drugCategory", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    EFFECT("effect", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    ENDPOINT("endpoint", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    ENZYME("enzyme", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    EXAM("exam", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    EXCRETION_ROUTE("excretionRoute", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    FREQUENCY_BAND("frequencyBand", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    HEPATIC_LEVEL("hepaticLevel", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    INSURANCE_RULE("insuranceRule", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    IONIZATION_FORM("ionizationForm", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    LAB_RESULT("labResult", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    MECHANISM("mechanism", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    MODEL_NAME("modelName", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    MODEL_TYPE("modelType", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    PATHWAY("pathway", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    POPULATION_CATEGORY("populationCategory", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    POST_MARKETING_PLAN("postMarketingPlan", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    RENAL_LEVEL("renalLevel", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    ROUTE("route", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    SOLVENT_POLARITY("solventPolarity", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    STUDY_DURATION("studyDuration", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    SYMPTOM("symptom", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    TISSUE_TYPE("tissueType", PlaceholderCategory.A_MEDICAL_VOCABULARY),
    TRAINING_PROGRAM("trainingProgram", PlaceholderCategory.A_MEDICAL_VOCABULARY),

    METABOLITE("metabolite", PlaceholderCategory.B_COINED_NAME),
    TARGET_MOLECULE("targetMolecule", PlaceholderCategory.B_COINED_NAME),

    DISEASE("disease", PlaceholderCategory.C_DISEASE_REFERENCE),

    AUC("auc", PlaceholderCategory.D_NUMERIC_RANGE),
    BIOAVAILABILITY("bioavailability", PlaceholderCategory.D_NUMERIC_RANGE),
    CMAX("cmax", PlaceholderCategory.D_NUMERIC_RANGE),
    CNS_RATIO("cnsRatio", PlaceholderCategory.D_NUMERIC_RANGE),
    DOSE_AMOUNT("doseAmount", PlaceholderCategory.D_NUMERIC_RANGE),
    DOSE_PER_KG("dosePerKg", PlaceholderCategory.D_NUMERIC_RANGE),
    DURATION_DAYS("durationDays", PlaceholderCategory.D_NUMERIC_RANGE),
    DURATION_WEEKS("durationWeeks", PlaceholderCategory.D_NUMERIC_RANGE),
    EFFICACY_RATE("efficacyRate", PlaceholderCategory.D_NUMERIC_RANGE),
    FECAL_EXCRETION_RATIO("fecalExcretionRatio", PlaceholderCategory.D_NUMERIC_RANGE),
    FOOD_EFFECT_RATIO("foodEffectRatio", PlaceholderCategory.D_NUMERIC_RANGE),
    FREQUENCY("frequency", PlaceholderCategory.D_NUMERIC_RANGE),
    HALF_LIFE("halfLife", PlaceholderCategory.D_NUMERIC_RANGE),
    IC50("ic50", PlaceholderCategory.D_NUMERIC_RANGE),
    INTERVAL("interval", PlaceholderCategory.D_NUMERIC_RANGE),
    MAX_DAILY_DOSE("maxDailyDose", PlaceholderCategory.D_NUMERIC_RANGE),
    MELTING_POINT("meltingPoint", PlaceholderCategory.D_NUMERIC_RANGE),
    PACKAGE_SIZE("packageSize", PlaceholderCategory.D_NUMERIC_RANGE),
    PATIENT_COUNT("patientCount", PlaceholderCategory.D_NUMERIC_RANGE),
    P_KA("pKa", PlaceholderCategory.D_NUMERIC_RANGE),
    PROTEIN_BINDING("proteinBinding", PlaceholderCategory.D_NUMERIC_RANGE),
    P_VALUE("pValue", PlaceholderCategory.D_NUMERIC_RANGE),
    REDUCTION_RATIO("reductionRatio", PlaceholderCategory.D_NUMERIC_RANGE),
    REFERENCE_RANGE("referenceRange", PlaceholderCategory.D_NUMERIC_RANGE),
    REIMBURSEMENT_DURATION_DAYS("reimbursementDurationDays", PlaceholderCategory.D_NUMERIC_RANGE),
    RETENTION_RATE("retentionRate", PlaceholderCategory.D_NUMERIC_RANGE),
    STABLE_DURATION("stableDuration", PlaceholderCategory.D_NUMERIC_RANGE),
    STORAGE_TEMPERATURE("storageTemperature", PlaceholderCategory.D_NUMERIC_RANGE),
    TMAX("tmax", PlaceholderCategory.D_NUMERIC_RANGE),
    TOTAL_DAILY_DOSE("totalDailyDose", PlaceholderCategory.D_NUMERIC_RANGE),
    URINARY_EXCRETION_RATIO("urinaryExcretionRatio", PlaceholderCategory.D_NUMERIC_RANGE),
    VOLUME_OF_DISTRIBUTION("volumeOfDistribution", PlaceholderCategory.D_NUMERIC_RANGE),
    ;

    companion object {
        private val BY_JSON_KEY: Map<String, PlaceholderKey> = entries.associateBy { it.jsonKey }

        fun fromJsonKey(jsonKey: String): PlaceholderKey? = BY_JSON_KEY[jsonKey]
    }
}
