package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.RiskFactorSeedBuckets
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseRiskFactorSemanticTest {
    @Test
    fun `non infectious chapter diseases do not use infection route risk factors`() {
        val diseases =
            DiseaseGenerator(
                adapter = FixmergeNameAdapter(),
                placeholderDictionary = DiseasePlaceholderDictionary(),
            ).generate(blueprints = DiseaseBlueprintFactory.build())
        val chapterIOnlyFactors = RiskFactorSeedBuckets.infectionExclusiveFactors()

        val firstViolation =
            diseases
                .asSequence()
                .filterNot { disease -> disease.icd10Chapter == Icd10Chapter.CHAPTER_I }
                .flatMap { disease ->
                    disease.epidemiology?.riskFactors.orEmpty()
                        .filter { riskFactor -> riskFactor in chapterIOnlyFactors }
                        .map { riskFactor -> "${disease.id}:${disease.icd10Chapter}:$riskFactor" }
                }
                .firstOrNull()

        assertEquals(expected = null, actual = firstViolation)
    }
}
