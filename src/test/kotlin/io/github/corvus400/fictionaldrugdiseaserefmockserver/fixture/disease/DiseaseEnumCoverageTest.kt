package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseEnumCoverageTest {
    private val generator: DiseaseGenerator = buildFreshGenerator()
    private val diseases: List<Disease> =
        DiseaseBlueprintFactory.build().map { blueprint -> generator.generate(blueprint = blueprint) }

    @Test
    fun `MedicalDepartment 全 16 値が primary department に出現する`() {
        val primaries: Set<MedicalDepartment> = diseases.map { it.medicalDepartment.first() }.toSet()
        assertEquals(expected = MedicalDepartment.entries.toSet(), actual = primaries)
    }

    private companion object {
        fun buildFreshGenerator(): DiseaseGenerator =
            DiseaseGenerator(
                adapter = FixmergeNameAdapter(),
                placeholderDictionary = DiseasePlaceholderDictionary(),
            )
    }
}
