package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

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
import kotlin.test.assertFailsWith

class CrossReferenceInitCheckTest {
    @Test
    fun `run succeeds for the full 120 drugs and 80 diseases fixture without throwing`() {
        val (diseases, drugs) = generateAllFixtures()

        CrossReferenceInitCheck.run(
            drugs = drugs,
            diseases = diseases,
        )
    }

    @Test
    fun `run throws IllegalStateException on drug to disease dangling reference`() {
        val (diseases, drugs) = generateAllFixtures()
        val danglingDrug = drugs.first().copy(relatedDiseaseIds = listOf(DANGLING_DISEASE_ID))
        val drugsWithDangling = listOf(danglingDrug) + drugs.drop(1)

        val error =
            assertFailsWith<IllegalStateException> {
                CrossReferenceInitCheck.run(
                    drugs = drugsWithDangling,
                    diseases = diseases,
                )
            }
        val message = error.message.orEmpty()
        check(DANGLING_DISEASE_ID in message) {
            "Expected exception message to contain dangling target id $DANGLING_DISEASE_ID, got: $message"
        }
        check(danglingDrug.id in message) {
            "Expected exception message to contain source id ${danglingDrug.id}, got: $message"
        }
    }

    @Test
    fun `run throws IllegalStateException on disease to drug dangling reference`() {
        val (diseases, drugs) = generateAllFixtures()
        val danglingDisease = diseases.first().copy(relatedDrugIds = listOf(DANGLING_DRUG_ID))
        val diseasesWithDangling = listOf(danglingDisease) + diseases.drop(1)

        val error =
            assertFailsWith<IllegalStateException> {
                CrossReferenceInitCheck.run(
                    drugs = drugs,
                    diseases = diseasesWithDangling,
                )
            }
        val message = error.message.orEmpty()
        check(DANGLING_DRUG_ID in message) {
            "Expected exception message to contain dangling target id $DANGLING_DRUG_ID, got: $message"
        }
        check(danglingDisease.id in message) {
            "Expected exception message to contain source id ${danglingDisease.id}, got: $message"
        }
    }

    @Test
    fun `run throws IllegalStateException on disease to disease dangling reference`() {
        val (diseases, drugs) = generateAllFixtures()
        val danglingDisease = diseases.first().copy(relatedDiseaseIds = listOf(DANGLING_DISEASE_ID))
        val diseasesWithDangling = listOf(danglingDisease) + diseases.drop(1)

        val error =
            assertFailsWith<IllegalStateException> {
                CrossReferenceInitCheck.run(
                    drugs = drugs,
                    diseases = diseasesWithDangling,
                )
            }
        val message = error.message.orEmpty()
        check(DANGLING_DISEASE_ID in message) {
            "Expected exception message to contain dangling target id $DANGLING_DISEASE_ID, got: $message"
        }
        check(danglingDisease.id in message) {
            "Expected exception message to contain source id ${danglingDisease.id}, got: $message"
        }
    }

    private companion object {
        const val DANGLING_DISEASE_ID = "disease_9999"
        const val DANGLING_DRUG_ID = "drug_9999"

        fun generateAllFixtures(): Pair<List<Disease>, List<Drug>> {
            val adapter = FixmergeNameAdapter()
            val diseaseBlueprints = DiseaseBlueprintFactory.build()
            val diseaseGenerator =
                DiseaseGenerator(
                    adapter = adapter,
                    placeholderDictionary = DiseasePlaceholderDictionary(),
                )
            val initialDiseases = diseaseGenerator.generate(blueprints = diseaseBlueprints)
            val drugGenerator =
                DrugGenerator(
                    adapter = adapter,
                    placeholderDictionary =
                    DrugPlaceholderDictionary(
                        nameAdapter = adapter,
                        diseases = initialDiseases,
                    ),
                    diseases = initialDiseases,
                )
            val drugs = drugGenerator.generate(blueprints = DrugBlueprintFactory.build())
            val diseases =
                DiseaseGenerator(
                    adapter = adapter,
                    placeholderDictionary = DiseasePlaceholderDictionary(),
                    drugs = drugs,
                )
                    .generate(blueprints = diseaseBlueprints)
            return diseases to drugs
        }
    }
}
