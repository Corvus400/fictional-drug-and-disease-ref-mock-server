package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DosageFormGroup
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DrugCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DrugGeneratorTest {
    private val adapter: FixmergeNameAdapter = FixmergeNameAdapter()
    private val generator: DrugGenerator = DrugGenerator(adapter = adapter)

    private val sampleBlueprint: DrugBlueprint =
        DrugBlueprint(
            index = 0,
            atcFirstLetter = 'A',
            dosageFormGroup = DosageFormGroup.ORAL,
            regulatoryClasses = setOf(RegulatoryClass.ORDINARY),
            isBiological = false,
            isChronicPrescription = true,
        )

    @Test
    fun `generate returns a Drug with non-blank required name fields`() {
        val drug = generator.generate(blueprint = sampleBlueprint)
        assertTrue(drug.id.isNotBlank())
        assertTrue(drug.brandName.isNotBlank())
        assertTrue(drug.brandNameKana.isNotBlank())
        assertTrue(drug.genericName.isNotBlank())
        assertTrue(drug.atcCode.isNotBlank())
        assertTrue(drug.therapeuticCategoryName.isNotBlank())
        assertTrue(drug.manufacturer.isNotBlank())
        assertTrue(drug.composition.activeIngredient.isNotBlank())
        assertTrue(drug.composition.inactiveIngredients.isNotEmpty())
        val physicochemical = assertNotNull(drug.physicochemicalProperties)
        assertTrue(physicochemical.genericNameEnglish.isNotBlank())
    }

    @Test
    fun `generate is deterministic for the same blueprint given fresh adapter instances`() {
        val first = DrugGenerator(adapter = FixmergeNameAdapter()).generate(blueprint = sampleBlueprint)
        val second = DrugGenerator(adapter = FixmergeNameAdapter()).generate(blueprint = sampleBlueprint)
        assertEquals(first, second)
    }

    @Test
    fun `genericName equals composition activeIngredient`() {
        val drug = generator.generate(blueprint = sampleBlueprint)
        assertEquals(drug.genericName, drug.composition.activeIngredient)
    }

    @Test
    fun `manufacturer ends with 製薬 and contains no beverage raw token`() {
        val drug = generator.generate(blueprint = sampleBlueprint)
        assertTrue(
            drug.manufacturer.endsWith(suffix = "製薬"),
            "manufacturer does not end with 製薬: '${drug.manufacturer}'",
        )
        val country = DrugCountryMapping.of(atcFirstLetter = sampleBlueprint.atcFirstLetter)
        val beverageBucket = CountryBucketRepository.of(country = country).beverage
        for (raw in beverageBucket) {
            assertFalse(
                drug.manufacturer.contains(other = raw),
                "manufacturer '${drug.manufacturer}' leaks beverage raw token '$raw'",
            )
        }
    }

    @Test
    fun `physicochemicalProperties genericNameEnglish pairs with genericName via same coined name`() {
        val drug = generator.generate(blueprint = sampleBlueprint)
        val physicochemical = assertNotNull(drug.physicochemicalProperties)
        assertTrue(
            physicochemical.genericNameEnglish.isNotBlank(),
            "genericNameEnglish is blank",
        )
        assertFalse(
            physicochemical.genericNameEnglish == drug.genericName,
            "genericNameEnglish should not equal genericName (one is latin, the other katakana)",
        )
    }

    @Test
    fun `generate bulk returns one drug per blueprint`() {
        val blueprints = listOf(
            sampleBlueprint,
            sampleBlueprint.copy(index = 1, atcFirstLetter = 'C'),
            sampleBlueprint.copy(index = 2, atcFirstLetter = 'N'),
        )
        val drugs = generator.generate(blueprints = blueprints)
        assertEquals(3, drugs.size)
        for ((i, drug) in drugs.withIndex()) {
            assertEquals("drug_${i.toString().padStart(4, '0')}", drug.id)
        }
    }

    @Test
    fun `generate bulk handles the full 120-drug factory inventory deterministically given fresh adapter instances`() {
        val blueprints = DrugBlueprintFactory.build()
        val first = DrugGenerator(adapter = FixmergeNameAdapter()).generate(blueprints = blueprints)
        val second = DrugGenerator(adapter = FixmergeNameAdapter()).generate(blueprints = blueprints)
        assertEquals(blueprints.size, first.size)
        assertEquals(first, second)
        assertEquals(first.size, first.map { it.id }.toSet().size, "drug ids are not unique")
        for (drug in first) {
            assertTrue(drug.brandName.isNotBlank(), "brandName blank for ${drug.id}")
            assertTrue(drug.genericName.isNotBlank(), "genericName blank for ${drug.id}")
            assertTrue(drug.manufacturer.isNotBlank(), "manufacturer blank for ${drug.id}")
        }
    }
}
