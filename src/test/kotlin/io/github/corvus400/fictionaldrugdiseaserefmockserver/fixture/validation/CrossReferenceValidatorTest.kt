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
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.PrecautionPopulationCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import kotlin.test.Test
import kotlin.test.assertEquals

class CrossReferenceValidatorTest {
    @Test
    fun `validate returns no violations for the full 120 drugs and 80 diseases fixture`() {
        val (diseases, drugs) = generateAllFixtures()

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
        val (diseases, drugs) = generateAllFixtures()
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
        val (diseases, drugs) = generateAllFixtures()
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
        val (diseases, drugs) = generateAllFixtures()
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
        val (diseases, drugs) = generateAllFixtures()
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
            actual = violations.filter { violation -> violation.danglingTargetId == "psychotropic_drug" },
        )
    }

    @Test
    fun `validate detects CHAPTER_XV disease referencing pregnancy-contraindicated drug`() {
        val (diseases, drugs) = generateAllFixtures()
        val pregnancyContraindicatedDrug = drugs.first { drug ->
            drug.precautionsForSpecificPopulations.any { precaution ->
                precaution.category == PrecautionPopulationCategory.PREGNANT
            }
        }
        val chapterFifteenDisease = diseases.first { disease -> disease.icd10Chapter == Icd10Chapter.CHAPTER_XV }
        val corruptedDisease = chapterFifteenDisease.copy(relatedDrugIds = listOf(pregnancyContraindicatedDrug.id))
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
                    danglingTargetId = pregnancyContraindicatedDrug.id,
                ),
            ),
            actual = violations.filter { violation ->
                violation.targetType == "drug" && violation.danglingTargetId == pregnancyContraindicatedDrug.id
            },
        )
    }

    @Test
    fun `validate detects ATC-chapter mismatch when L-drug references CHAPTER_I disease`() {
        val (diseases, drugs) = generateAllFixtures()
        val chapterOneDisease = diseases.first { disease -> disease.icd10Chapter == Icd10Chapter.CHAPTER_I }
        val antineoplasticDrug = drugs.first { drug ->
            drug.atcCode.startsWith(prefix = "L") && drug.id !in FINAL_OVERRIDE_DRUG_IDS
        }
        val corruptedDrug = antineoplasticDrug.copy(relatedDiseaseIds = listOf(chapterOneDisease.id))
        val drugsWithCorruption =
            drugs.map { drug ->
                if (drug.id == corruptedDrug.id) corruptedDrug else drug
            }

        val violations =
            CrossReferenceValidator.validate(
                drugs = drugsWithCorruption,
                diseases = diseases,
            )

        assertEquals(
            expected = listOf(
                CrossRefViolation(
                    sourceType = "drug",
                    sourceId = corruptedDrug.id,
                    targetType = "disease_chapter_mismatch",
                    danglingTargetId = chapterOneDisease.id,
                ),
            ),
            actual = violations.filter { violation -> violation.targetType == "disease_chapter_mismatch" },
        )
    }

    @Test
    fun `validate detects ATC-chapter mismatch when CHAPTER_I disease references L-drug`() {
        val (diseases, drugs) = generateAllFixtures()
        val antineoplasticDrug = drugs.first { drug ->
            drug.atcCode.startsWith(prefix = "L") && drug.id !in FINAL_OVERRIDE_DRUG_IDS
        }
        val chapterOneDisease = diseases.first { disease -> disease.icd10Chapter == Icd10Chapter.CHAPTER_I }
        val corruptedDisease = chapterOneDisease.copy(relatedDrugIds = listOf(antineoplasticDrug.id))
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
                    targetType = "drug_atc_mismatch",
                    danglingTargetId = antineoplasticDrug.id,
                ),
            ),
            actual = violations.filter { violation -> violation.targetType == "drug_atc_mismatch" },
        )
    }

    @Test
    fun `validate emits both dangling and semantic violations independently`() {
        val (diseases, drugs) = generateAllFixtures()
        val chapterOneDisease = diseases.first { disease -> disease.icd10Chapter == Icd10Chapter.CHAPTER_I }
        val antineoplasticDrug = drugs.first { drug ->
            drug.atcCode.startsWith(prefix = "L") && drug.id !in FINAL_OVERRIDE_DRUG_IDS
        }
        val corruptedDrug =
            antineoplasticDrug.copy(relatedDiseaseIds = listOf(DANGLING_DISEASE_ID, chapterOneDisease.id))
        val drugsWithCorruption =
            drugs.map { drug ->
                if (drug.id == corruptedDrug.id) corruptedDrug else drug
            }

        val violations =
            CrossReferenceValidator.validate(
                drugs = drugsWithCorruption,
                diseases = diseases,
            ).filter { violation -> violation.sourceId == corruptedDrug.id }

        assertEquals(
            expected = setOf("disease", "disease_chapter_mismatch"),
            actual = violations.map { violation -> violation.targetType }.toSet(),
        )
    }

    @Test
    fun `validate skips records whose related ids were set by final overrides`() {
        val (diseases, drugs) = generateAllFixtures()

        val semanticViolations =
            CrossReferenceValidator.validate(
                drugs = drugs,
                diseases = diseases,
            ).filter { violation ->
                violation.targetType in ATC_CHAPTER_MISMATCH_TYPES &&
                    (violation.sourceId == "drug_0080" || violation.sourceId == "disease_0079")
            }

        assertEquals(
            expected = emptyList(),
            actual = semanticViolations,
        )
    }

    @Test
    fun `validate against real fixtures emits no ATC-chapter semantic violations`() {
        val (diseases, drugs) = generateAllFixtures()

        val semanticViolations =
            CrossReferenceValidator.validate(
                drugs = drugs,
                diseases = diseases,
            ).filter { violation -> violation.targetType in ATC_CHAPTER_MISMATCH_TYPES }

        assertEquals(
            expected = emptyList(),
            actual = semanticViolations,
        )
    }

    private companion object {
        const val DANGLING_DISEASE_ID = "disease_9999"
        const val DANGLING_DRUG_ID = "drug_9999"
        val ATC_CHAPTER_MISMATCH_TYPES: Set<String> = setOf("disease_chapter_mismatch", "drug_atc_mismatch")
        val FINAL_OVERRIDE_DRUG_IDS: Set<String> = setOf("drug_0080", "drug_0089")

        val PSYCHOTROPIC_CLASSES: Set<RegulatoryClass> =
            setOf(
                RegulatoryClass.PSYCHOTROPIC_1,
                RegulatoryClass.PSYCHOTROPIC_2,
                RegulatoryClass.PSYCHOTROPIC_3,
            )

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
