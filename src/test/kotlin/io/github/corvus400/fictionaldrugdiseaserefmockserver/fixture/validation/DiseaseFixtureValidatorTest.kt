package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseFixtureValidatorTest {
    private val fullInventory: List<Disease> = generateFullInventory()

    @Test
    fun `validate returns empty violations for the full 80-disease factory inventory`() {
        val violations = DiseaseFixtureValidator.validate(diseases = fullInventory)
        assertEquals(emptyList(), violations)
    }

    private fun generateFullInventory(): List<Disease> {
        val generator = DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        )
        return generator.generate(blueprints = DiseaseBlueprintFactory.build())
    }
}
