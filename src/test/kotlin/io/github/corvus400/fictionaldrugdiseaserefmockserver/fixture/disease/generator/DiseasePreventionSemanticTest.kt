package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.PreventionSeedBuckets
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseasePreventionSemanticTest {
    @Test
    fun `non infectious chapter diseases do not use infection control prevention items`() {
        val diseases =
            DiseaseGenerator(
                adapter = FixmergeNameAdapter(),
                placeholderDictionary = DiseasePlaceholderDictionary(),
            ).generate(blueprints = DiseaseBlueprintFactory.build())
        val infectionControlItems = PreventionSeedBuckets.infectionExclusiveItems()

        val firstViolation =
            diseases
                .asSequence()
                .filterNot { disease -> disease.icd10Chapter == Icd10Chapter.CHAPTER_I }
                .flatMap { disease ->
                    disease.prevention
                        .filter { item -> item in infectionControlItems }
                        .map { item -> "${disease.id}:${disease.icd10Chapter}:$item" }
                }
                .firstOrNull()

        assertEquals(expected = null, actual = firstViolation)
    }
}
