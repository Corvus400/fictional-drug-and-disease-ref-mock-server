package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseFinalOverridesTest {
    @Test
    fun `chapter XXII disease should be overridden by DISEASE_FINAL_OVERRIDES`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }

        assertEquals("魔女因子症候群", disease0079.name)
    }

    private fun generateDiseases(): List<Disease> =
        DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        ).generate(blueprints = DiseaseBlueprintFactory.build())
}
