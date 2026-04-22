package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

/**
 * Declarative intermediate representation of a disease fixture used by the Blueprint layer.
 *
 * Holds only classification axes and the seed-determining index. Conditional-required flags
 * and derived values are intentionally absent; Generators derive them from these axes
 * (e.g., infectious diseases imply at least one transmission-route field).
 *
 * `isInfectious` and `icd10Chapter` are independent classification axes — infectious disease
 * fixtures are not constrained to ICD-10 Chapter I, so Factory-side distribution control
 * stays flexible.
 */
data class DiseaseBlueprint(
    val index: Int,
    val icd10Chapter: Icd10Chapter,
    val chronicity: Chronicity,
    val isInfectious: Boolean,
    val isMentalDisorder: Boolean,
    val isRareDisease: Boolean,
) {
    init {
        require(index >= 0) { "index must be non-negative, got $index" }
    }
}
