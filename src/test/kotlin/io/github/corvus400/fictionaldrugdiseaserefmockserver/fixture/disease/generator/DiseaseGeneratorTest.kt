package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DiseaseCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DiseaseGeneratorTest {
    private val adapter: FixmergeNameAdapter = FixmergeNameAdapter()
    private val generator: DiseaseGenerator = DiseaseGenerator(adapter = adapter)

    private val sampleBlueprint: DiseaseBlueprint =
        DiseaseBlueprint(
            index = 0,
            icd10Chapter = Icd10Chapter.CHAPTER_I,
            chronicity = Chronicity.ACUTE,
            isInfectious = true,
            isMentalDisorder = false,
            isRareDisease = false,
        )

    @Test
    fun `generate returns a Disease with non-blank required name fields`() {
        val disease = generator.generate(blueprint = sampleBlueprint)
        assertTrue(disease.id.isNotBlank())
        assertTrue(disease.name.isNotBlank())
        assertTrue(disease.nameKana.isNotBlank())
        val english = disease.nameEnglish
        assertTrue(english != null && english.isNotBlank(), "nameEnglish is blank or null")
    }

    @Test
    fun `generate is deterministic for the same blueprint`() {
        val first = generator.generate(blueprint = sampleBlueprint)
        val second = generator.generate(blueprint = sampleBlueprint)
        assertEquals(first, second)
    }

    @Test
    fun `name equals nameKana because both derive from the same CoinedName katakana`() {
        val disease = generator.generate(blueprint = sampleBlueprint)
        assertEquals(disease.name, disease.nameKana)
    }

    @Test
    fun `nameEnglish is latin and differs from the katakana name`() {
        val disease = generator.generate(blueprint = sampleBlueprint)
        val english = disease.nameEnglish
        assertTrue(english != null && english.isNotBlank(), "nameEnglish is blank or null")
        assertFalse(
            english == disease.name,
            "nameEnglish should not equal name (one is latin, the other katakana)",
        )
    }

    @Test
    fun `synonyms differentials and complications contain no cuisine or beverage raw tokens`() {
        val disease = generator.generate(blueprint = sampleBlueprint)
        val country = DiseaseCountryMapping.of(chapter = sampleBlueprint.icd10Chapter)
        val bucket = CountryBucketRepository.of(country = country)
        val collected: List<String> =
            listOf(disease.name, disease.nameKana) +
                disease.synonyms +
                disease.differentialDiagnoses +
                disease.complications
        for (raw in bucket.cuisine + bucket.beverage) {
            for (value in collected) {
                assertFalse(
                    value.contains(other = raw),
                    "disease value '$value' leaks non-cities raw token '$raw'",
                )
            }
        }
    }

    @Test
    fun `generate bulk returns one disease per blueprint with sequential ids`() {
        val blueprints =
            listOf(
                sampleBlueprint,
                sampleBlueprint.copy(index = 1, icd10Chapter = Icd10Chapter.CHAPTER_II),
                sampleBlueprint.copy(index = 2, icd10Chapter = Icd10Chapter.CHAPTER_V),
            )
        val diseases = generator.generate(blueprints = blueprints)
        assertEquals(3, diseases.size)
        for ((i, disease) in diseases.withIndex()) {
            assertEquals("disease_${i.toString().padStart(4, '0')}", disease.id)
        }
    }

    @Test
    fun `generate bulk handles the full disease factory inventory deterministically`() {
        val blueprints = DiseaseBlueprintFactory.build()
        val first = generator.generate(blueprints = blueprints)
        val second = generator.generate(blueprints = blueprints)
        assertEquals(blueprints.size, first.size)
        assertEquals(first, second)
        assertEquals(first.size, first.map { it.id }.toSet().size, "disease ids are not unique")
        for (disease in first) {
            assertTrue(disease.name.isNotBlank(), "name blank for ${disease.id}")
            assertTrue(disease.nameKana.isNotBlank(), "nameKana blank for ${disease.id}")
        }
    }
}
