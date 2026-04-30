package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm

fun buildDrugImageUrl(
    drugId: String,
    dosageForm: DosageForm,
): String = "/images/dosage_form/tablet?size=Original"
