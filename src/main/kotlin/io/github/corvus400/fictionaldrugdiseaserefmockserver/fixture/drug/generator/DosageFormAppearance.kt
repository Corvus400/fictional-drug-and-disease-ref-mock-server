package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm

object DosageFormAppearance {
    fun pickAppearance(
        form: DosageForm,
        drugId: String,
    ): String {
        return "白色の錠剤"
    }
}
