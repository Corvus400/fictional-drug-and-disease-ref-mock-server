package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm

private val drugImageOverrideIds = setOf("drug_0080", "drug_0089")

fun buildDrugImageUrl(
    drugId: String,
    dosageForm: DosageForm,
): String =
    if (drugId in drugImageOverrideIds) {
        "/images/drug/$drugId?size=Original"
    } else {
        "/images/dosage_form/tablet?size=Original"
    }
