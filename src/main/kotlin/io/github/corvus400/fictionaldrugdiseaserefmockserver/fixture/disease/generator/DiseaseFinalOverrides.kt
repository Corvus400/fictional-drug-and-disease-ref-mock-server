package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease

internal val DISEASE_FINAL_OVERRIDES: Map<String, (Disease) -> Disease> =
    mapOf("disease_0079" to ::witchFactorSyndromeOverride)

private fun witchFactorSyndromeOverride(generated: Disease): Disease =
    generated.copy(
        name = "魔女因子症候群",
    )
