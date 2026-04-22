package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug

class DrugGenerator(
    private val adapter: FixmergeNameAdapter,
) {
    fun generate(blueprint: DrugBlueprint): Drug {
        TODO("not implemented")
    }

    fun generate(blueprints: List<DrugBlueprint>): List<Drug> {
        TODO("not implemented")
    }
}
