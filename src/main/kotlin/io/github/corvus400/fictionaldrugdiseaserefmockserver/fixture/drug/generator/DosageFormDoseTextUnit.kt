package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
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

    fun administrationVerb(form: DosageForm): String =
        when (form) {
            DosageForm.TABLET,
            DosageForm.CAPSULE,
            DosageForm.POWDER,
            DosageForm.GRANULE,
            DosageForm.LIQUID,
            -> "経口投与する"
            DosageForm.INJECTION_FORM -> "投与する"
            DosageForm.OINTMENT,
            DosageForm.CREAM,
            -> "塗布する"
            DosageForm.PATCH -> "貼付する"
            DosageForm.EYE_DROPS -> "点眼する"
            DosageForm.SUPPOSITORY -> "挿入する"
            DosageForm.INHALER,
            DosageForm.NASAL_SPRAY,
            -> "吸入する"
        }

    fun upperBoundUnit(form: DosageForm): String = unitFor(form = form)

    fun maxDailyDose(
        form: DosageForm,
        seed: Long,
    ): String {
        val value = ValueRangeGenerator.pickInRange(seed = seed, range = upperBoundRange(form = form))
        return "$value ${upperBoundUnit(form = form)}"
    }

    private fun upperBoundRange(form: DosageForm): IntRange =
        when (form) {
            DosageForm.TABLET,
            DosageForm.CAPSULE,
            DosageForm.POWDER,
            DosageForm.GRANULE,
            DosageForm.OINTMENT,
            DosageForm.CREAM,
            -> 1..30
            DosageForm.LIQUID,
            DosageForm.INJECTION_FORM,
            -> 1..100
            DosageForm.PATCH -> 1..10
            DosageForm.EYE_DROPS -> 1..20
            DosageForm.SUPPOSITORY -> 1..6
            DosageForm.INHALER,
            DosageForm.NASAL_SPRAY,
            -> 1..20
        }
}
