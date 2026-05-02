package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation.DiseaseFixtureValidator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DiseaseFinalOverridesTest {
    @Test
    fun `chapter XXII disease should be overridden by DISEASE_FINAL_OVERRIDES`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }

        assertEquals("魔女因子症候群", disease0079.name)
    }

    @Test
    fun `chapter XXII disease should reflect witch factor syndrome details`() {
        val diseases = generateDiseases()
        val disease0079 = diseases.first { it.id == "disease_0079" }

        assertEquals("マジョインシショウコウグン", disease0079.nameKana)
        assertTrue(disease0079.nameEnglish?.contains("Witch Factor") == true)
        assertTrue(disease0079.synonyms.any { synonym -> synonym.contains("魔女化症") })
        assertTrue(disease0079.summary.contains("魔女因子"))
        assertEquals(false, disease0079.infectious)
        assertEquals(Chronicity.CHRONIC, disease0079.chronicity)
        assertTrue(disease0079.symptoms.mainSymptoms.size >= 3)
        assertTrue(disease0079.requiredExams.isNotEmpty())
        assertTrue(disease0079.prognosis?.contains("不可逆") == true)
        assertTrue(disease0079.summary.endsWith("(架空)"))
        assertTrue(DiseaseFixtureValidator.validate(diseases = diseases).isEmpty())
    }

    @Test
    fun `non chapter XXII disease should not be affected by witch factor override`() {
        val disease0078 = generateDiseases().first { it.id == "disease_0078" }

        assertNotEquals("魔女因子症候群", disease0078.name)
    }

    private fun generateDiseases(): List<Disease> =
        DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        ).generate(blueprints = DiseaseBlueprintFactory.build())
}
