package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm

fun buildDrugImageUrl(
    drugId: String,
    dosageForm: DosageForm,
): String =
    if (drugId == "drug_0089") {
        "/images/drug/drug_0089?size=Original"
    } else {
        "/images/dosage_form/tablet?size=Original"
    }
