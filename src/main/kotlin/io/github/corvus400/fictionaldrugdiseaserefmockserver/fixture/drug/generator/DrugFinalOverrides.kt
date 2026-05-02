package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug

internal val DRUG_FINAL_OVERRIDES: Map<String, (Drug) -> Drug> =
    mapOf("drug_0080" to ::tredecimFinalOverride)

private fun tredecimFinalOverride(generated: Drug): Drug =
    generated.copy(
        therapeuticCategoryName = "対魔女兵器 (架空分類)",
    )
