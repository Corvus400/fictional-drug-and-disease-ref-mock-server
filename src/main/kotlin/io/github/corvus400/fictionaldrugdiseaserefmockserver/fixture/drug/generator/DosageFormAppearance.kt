package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm

object DosageFormAppearance {
    private val appearanceVariants: Map<DosageForm, List<String>> =
        mapOf(
            DosageForm.TABLET to
                listOf(
                    "白色と淡青色の二層フィルムコート錠",
                    "白色のフィルムコート錠 (PTP 包装)",
                    "淡青色の素錠 (割線あり)",
                ),
        )

    fun pickAppearance(
        form: DosageForm,
        drugId: String,
    ): String {
        val variants: List<String> = appearanceVariants.getValue(key = form)
        val seed: Long =
            stableHash(
                id = drugId,
                slot = NameSlot.DRUG_APPEARANCE.ordinal,
                index = 0,
            )
        return variants[normalize(seed = seed, modulus = variants.size)]
    }

    private fun normalize(
        seed: Long,
        modulus: Int,
    ): Int {
        val raw: Int = (seed % modulus).toInt()
        if (raw < 0) {
            return raw + modulus
        }
        return raw
    }
}
