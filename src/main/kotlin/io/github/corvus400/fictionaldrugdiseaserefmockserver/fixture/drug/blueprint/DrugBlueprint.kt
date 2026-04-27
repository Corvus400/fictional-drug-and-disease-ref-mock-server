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
    val regulatoryClasses: Set<RegulatoryClass>,
    val isBiological: Boolean,
    val isChronicPrescription: Boolean,
    val dosageForm: DosageForm,
    val idOverride: String? = null,
    val nameOverride: NameOverride? = null,
    val textOverride: FixedDrugTextOverride? = null,
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

/**
 * `DrugBlueprint` の名前を固定値で上書きするためのオーバーライド情報。
 *
 * `LIQUID_SP_TREDECIM` / `LIQUID_SP_SLEEP_AID` のように Generator が通常の Fixmerge 生成を
 * バイパスして固定の和名・英名を埋め込む必要があるケースで利用する。
 */
data class NameOverride(
    val brandKatakana: String,
    val genericKatakana: String,
    val genericLatin: String,
)

/**
 * `DrugBlueprint` の `composition.appearance` と `physicochemical_properties.description` を
 * 固定値で上書きするためのオーバーライド情報。`DosageFormAppearance` の variants 派生をバイパスする。
 */
data class FixedDrugTextOverride(
    val appearance: String,
    val originalSubstanceDescription: String,
)
