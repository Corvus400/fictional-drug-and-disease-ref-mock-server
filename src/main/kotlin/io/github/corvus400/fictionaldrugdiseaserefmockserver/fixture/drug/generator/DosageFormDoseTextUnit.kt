package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm

object DosageFormDoseTextUnit {
    fun unitFor(form: DosageForm): String =
        when (form) {
            DosageForm.TABLET -> "錠"
            DosageForm.CAPSULE -> "カプセル"
            DosageForm.POWDER -> "包"
            DosageForm.GRANULE -> "包"
            DosageForm.LIQUID -> "mL"
            DosageForm.INJECTION_FORM -> "mL"
            DosageForm.OINTMENT -> "g"
            DosageForm.CREAM -> "g"
            DosageForm.PATCH -> "枚"
            DosageForm.EYE_DROPS -> "滴"
            DosageForm.SUPPOSITORY -> "個"
            DosageForm.INHALER -> "噴霧"
            DosageForm.NASAL_SPRAY -> "噴霧"
        }
}
