package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass

/**
 * Declarative intermediate representation of a drug fixture used by the Blueprint layer.
 *
 * Holds only classification axes and the seed-determining index. Conditional-required flags
 * and derived values are intentionally absent; Generators derive them from these axes
 * (e.g., a biological product implies at least one warning entry).
 */
data class DrugBlueprint(
    val index: Int,
    val atcFirstLetter: Char,
    val dosageFormGroup: DosageFormGroup,
    val regulatoryClasses: Set<RegulatoryClass>,
    val isBiological: Boolean,
    val isChronicPrescription: Boolean,
    val dosageForm: DosageForm = DosageForm.TABLET,
) {
    init {
        require(index >= 0) { "index must be non-negative, got $index" }
        require(atcFirstLetter in ATC_FIRST_LEVEL_CLASSES) {
            "atcFirstLetter must be one of $ATC_FIRST_LEVEL_CLASSES, got '$atcFirstLetter'"
        }
    }

    companion object {
        private val ATC_FIRST_LEVEL_CLASSES: Set<Char> =
            setOf('A', 'B', 'C', 'D', 'G', 'H', 'J', 'L', 'M', 'N', 'P', 'R', 'S', 'V')
    }
}

enum class DosageFormGroup { ORAL, EXTERNAL, INJECTION, INHALATION, OPHTHALMIC }
