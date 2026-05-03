package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import kotlin.test.Test
import kotlin.test.assertEquals

class CrossReferenceValidatorTest {
    @Test
    fun `validate returns no violations for the full 120 drugs and 80 diseases fixture`() {
        val diseases = generateAllDiseases()
        val drugs = generateAllDrugs(diseases = diseases)

        val violations =
            CrossReferenceValidator.validate(
                drugs = drugs,
                diseases = diseases,
            )

        assertEquals(
            expected = emptyList(),
            actual = violations,
        )
    }

    @Test
    fun `validate detects disease-to-drug dangling reference`() {
        val diseases = generateAllDiseases()
        val drugs = generateAllDrugs(diseases = diseases)
        val danglingDisease = diseases.first().copy(relatedDrugIds = listOf(DANGLING_DRUG_ID))
        val diseasesWithDangling = listOf(danglingDisease) + diseases.drop(1)

        val violations =
            CrossReferenceValidator.validate(
                drugs = drugs,
                diseases = diseasesWithDangling,
            )

        assertEquals(
            expected = listOf(
                CrossRefViolation(
                    sourceType = "disease",
                    sourceId = danglingDisease.id,
                    targetType = "drug",
                    danglingTargetId = DANGLING_DRUG_ID,
                ),
            ),
            actual = violations,
        )
    }

    @Test
    fun `validate detects disease-to-disease dangling reference`() {
        val diseases = generateAllDiseases()
        val drugs = generateAllDrugs(diseases = diseases)
        val danglingDisease = diseases.first().copy(relatedDiseaseIds = listOf(DANGLING_DISEASE_ID))
        val diseasesWithDangling = listOf(danglingDisease) + diseases.drop(1)

        val violations =
            CrossReferenceValidator.validate(
                drugs = drugs,
                diseases = diseasesWithDangling,
            )

        assertEquals(
            expected = listOf(
                CrossRefViolation(
                    sourceType = "disease",
                    sourceId = danglingDisease.id,
                    targetType = "disease",
                    danglingTargetId = DANGLING_DISEASE_ID,
                ),
            ),
            actual = violations,
        )
    }

    @Test
    fun `validate detects drug-to-disease dangling reference`() {
        val diseases = generateAllDiseases()
        val drugs = generateAllDrugs(diseases = diseases)
        val danglingDrug = drugs.first().copy(relatedDiseaseIds = listOf(DANGLING_DISEASE_ID))
        val drugsWithDangling = listOf(danglingDrug) + drugs.drop(1)

        val violations =
            CrossReferenceValidator.validate(
                drugs = drugsWithDangling,
                diseases = diseases,
            )

        assertEquals(
            expected = listOf(
                CrossRefViolation(
                    sourceType = "drug",
                    sourceId = danglingDrug.id,
                    targetType = "disease",
                    danglingTargetId = DANGLING_DISEASE_ID,
                ),
            ),
            actual = violations,
        )
    }

    @Test
    fun `validate detects CHAPTER_V disease without a psychotropic related drug`() {
        val diseases = generateAllDiseases()
        val drugs = generateAllDrugs(diseases = diseases)
        val nonPsychotropicDrug = drugs.first { drug ->
            drug.regulatoryClass.none { regulatoryClass ->
                regulatoryClass in PSYCHOTROPIC_CLASSES
            }
        }
        val chapterFiveDisease = diseases.first { disease -> disease.icd10Chapter == Icd10Chapter.CHAPTER_V }
        val corruptedDisease = chapterFiveDisease.copy(relatedDrugIds = listOf(nonPsychotropicDrug.id))
        val diseasesWithCorruption =
            diseases.map { disease ->
                if (disease.id == corruptedDisease.id) corruptedDisease else disease
            }

        val violations =
            CrossReferenceValidator.validate(
                drugs = drugs,
                diseases = diseasesWithCorruption,
            )

        assertEquals(
            expected = listOf(
                CrossRefViolation(
                    sourceType = "disease",
                    sourceId = corruptedDisease.id,
                    targetType = "drug",
                    danglingTargetId = "psychotropic_drug",
                ),
            ),
            actual = violations,
        )
    }

    private companion object {
        const val DANGLING_DISEASE_ID = "disease_9999"
        const val DANGLING_DRUG_ID = "drug_9999"

        val PSYCHOTROPIC_CLASSES: Set<RegulatoryClass> =
            setOf(
                RegulatoryClass.PSYCHOTROPIC_1,
                RegulatoryClass.PSYCHOTROPIC_2,
                RegulatoryClass.PSYCHOTROPIC_3,
            )

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
                        diseases = diseases,
                    ),
                )
            return generator.generate(blueprints = DrugBlueprintFactory.build())
        }
    }
}
