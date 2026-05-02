package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugFinalOverridesTest {
    @Test
    fun `drug_0080 should be overridden by DRUG_FINAL_OVERRIDES`() {
        val drug0080 = generateDrugs().first { it.id == "drug_0080" }

        assertEquals("対魔女兵器 (架空分類)", drug0080.therapeuticCategoryName)
    }

    private fun generateDrugs(): List<Drug> {
        val adapter = FixmergeNameAdapter()
        val diseases =
            DiseaseGenerator(
                adapter = adapter,
                placeholderDictionary = DiseasePlaceholderDictionary(),
            ).generate(blueprints = DiseaseBlueprintFactory.build())
        val drugDictionary = DrugPlaceholderDictionary(nameAdapter = adapter, diseases = diseases)
        return DrugGenerator(adapter = adapter, placeholderDictionary = drugDictionary)
            .generate(blueprints = DrugBlueprintFactory.build())
    }
}
