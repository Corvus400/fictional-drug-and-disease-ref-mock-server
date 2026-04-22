package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease

class DiseaseGenerator(
    @Suppress("unused") private val adapter: FixmergeNameAdapter,
) {
    fun generate(blueprint: DiseaseBlueprint): Disease {
        TODO("not implemented")
    }

    fun generate(blueprints: List<DiseaseBlueprint>): List<Disease> {
        TODO("not implemented")
    }
}
