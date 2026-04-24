package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import kotlin.test.Test

class CrossReferenceInitCheckTest {
    @Test
    fun `run succeeds for the full 120 drugs and 80 diseases fixture without throwing`() {
        val diseases = generateAllDiseases()
        val drugs = generateAllDrugs(diseases = diseases)

        CrossReferenceInitCheck.run(
            drugs = drugs,
            diseases = diseases,
        )
    }

    private companion object {
        fun generateAllDiseases(): List<Disease> {
            val adapter = FixmergeNameAdapter()
            val generator =
                DiseaseGenerator(
                    adapter = adapter,
                    placeholderDictionary = DiseasePlaceholderDictionary(),
                )
            return generator.generate(blueprints = DiseaseBlueprintFactory.build())
        }

        fun generateAllDrugs(diseases: List<Disease>): List<Drug> {
            val adapter = FixmergeNameAdapter()
            val generator =
                DrugGenerator(
                    adapter = adapter,
                    placeholderDictionary =
                    DrugPlaceholderDictionary(
                        nameAdapter = adapter,
                        diseaseProvider = DiseaseFixtureProvider(all = diseases),
                    ),
                )
            return generator.generate(blueprints = DrugBlueprintFactory.build())
        }
    }
}
