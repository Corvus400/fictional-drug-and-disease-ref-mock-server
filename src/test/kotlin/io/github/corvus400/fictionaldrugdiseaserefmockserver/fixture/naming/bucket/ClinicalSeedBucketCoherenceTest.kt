package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DISEASE_FINAL_OVERRIDES
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseNestedBuilders
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClinicalSeedBucketCoherenceTest {
    @Test
    fun `clinical seed buckets pass validator`() {
        assertEquals(expected = emptyList(), actual = BucketEntryValidator.validateAll(ClinicalSeedBucketRegistry.all))
    }

    @Test
    fun `drug numbered adverse reaction and interaction placeholders are replaced`() {
        val (_, drugs) = generateFixtures()
        val seriousNames = drugs.flatMap { drug -> drug.adverseReactions.serious.map { reaction -> reaction.name } }
        val interactionNames =
            drugs.flatMap { drug ->
                drug.interactions?.combinationProhibited.orEmpty() + drug.interactions?.combinationCaution.orEmpty()
            }.map { entry -> entry.displayName }

        assertEquals(expected = null, actual = seriousNames.firstOrNull { name -> NUMBERED_SERIOUS.matches(name) })
        assertEquals(expected = null, actual = interactionNames.firstOrNull { name -> "サンプル系薬" in name })
        assertTrue(actual = seriousNames.distinct().size >= 5)
        assertTrue(actual = interactionNames.distinct().size >= 5)
    }

    @Test
    fun `disease numbered severity treatment and exam placeholders are replaced`() {
        val (diseases, _) = generateFixtures()
        val severityLabels =
            diseases.flatMap { disease -> disease.severityGrading?.grades.orEmpty().map { grade -> grade.label } }
        val severityActions =
            diseases.flatMap { disease ->
                disease.severityGrading?.grades.orEmpty().mapNotNull { grade -> grade.recommendedAction }
            }
        val treatmentCategories =
            diseases.flatMap { disease ->
                disease.treatments.pharmacological.map { treatment -> treatment.drugCategory }
            }
        val nonPharmaItems =
            diseases.flatMap { disease ->
                disease.treatments.nonPharmacological.flatMap { treatment -> treatment.items }
            }
        val examTexts =
            diseases.flatMap { disease ->
                disease.requiredExams.flatMap { exam -> listOfNotNull(exam.typicalFinding, exam.referenceRange) }
            }

        assertEquals(expected = null, actual = severityLabels.firstOrNull { label -> NUMBERED_GRADE.matches(label) })
        assertEquals(
            expected = null,
            actual = severityActions.firstOrNull { action ->
                NUMBERED_GRADE.containsMatchIn(action)
            }
        )
        assertEquals(
            expected = null,
            actual = treatmentCategories.firstOrNull { category ->
                NUMBERED_DRUG_CATEGORY.matches(category)
            }
        )
        assertEquals(expected = null, actual = nonPharmaItems.firstOrNull { item -> NUMBERED_ITEM.matches(item) })
        assertEquals(
            expected = null,
            actual = examTexts.firstOrNull { text ->
                text.startsWith("典型所見") ||
                    text.startsWith("基準値")
            }
        )
    }

    @Test
    fun `drug references use coined journal source`() {
        val (_, drugs) = generateFixtures()
        val references = drugs.flatMap { drug -> drug.references }

        assertEquals(expected = null, actual = references.firstOrNull { reference -> "サンプル誌" in reference.citation })
        assertTrue(actual = references.map { reference -> reference.source }.distinct().size >= 3)
    }

    @Test
    fun `generated drug and disease text does not duplicate semantic suffixes`() {
        val (diseases, drugs) = generateFixtures()
        val fixtureText = Json.encodeToString(diseases) + "\n" + Json.encodeToString(drugs)
        val firstViolation =
            DUPLICATED_SEMANTIC_SUFFIXES
                .firstNotNullOfOrNull { pattern -> pattern.find(fixtureText)?.value }

        assertEquals(expected = null, actual = firstViolation)
    }

    private fun generateFixtures(): Pair<List<Disease>, List<Drug>> {
        val adapter = FixmergeNameAdapter()
        val diseasePlaceholderDictionary = DiseasePlaceholderDictionary()
        val diseaseBlueprints = DiseaseBlueprintFactory.build()
        val initialDiseases =
            DiseaseGenerator(adapter = adapter, placeholderDictionary = diseasePlaceholderDictionary)
                .generate(blueprints = diseaseBlueprints)
        val drugs =
            DrugGenerator(
                adapter = adapter,
                placeholderDictionary = DrugPlaceholderDictionary(nameAdapter = adapter, diseases = initialDiseases),
                diseases = initialDiseases,
            ).generate(blueprints = DrugBlueprintFactory.build())
        val diseases =
            initialDiseases.map { disease ->
                val withRelatedDrugIds =
                    disease.copy(
                        relatedDrugIds =
                        DiseaseNestedBuilders.buildRelatedDrugIds(
                            id = disease.id,
                            chapter = disease.icd10Chapter,
                            drugFixtures = drugs,
                        ),
                    )
                DISEASE_FINAL_OVERRIDES[withRelatedDrugIds.id]?.invoke(withRelatedDrugIds) ?: withRelatedDrugIds
            }
        return diseases to drugs
    }

    private companion object {
        val NUMBERED_SERIOUS: Regex = Regex("""^重篤な副作用 \d+$""")
        val NUMBERED_GRADE: Regex = Regex("""^Grade \d+$""")
        val NUMBERED_DRUG_CATEGORY: Regex = Regex("""^架空薬効群 \d+$""")
        val NUMBERED_ITEM: Regex = Regex("""^項目 \d+$""")
        val DUPLICATED_SEMANTIC_SUFFIXES: List<Regex> =
            listOf(
                Regex("""[\p{IsHan}\p{IsKatakana}A-Za-z0-9（）()・]+モデル モデル"""),
                Regex("""[\p{IsHan}\p{IsKatakana}A-Za-z0-9（）()・]+コンパートメント コンパートメントモデル"""),
                Regex("""[\p{IsHan}\p{IsKatakana}A-Za-z0-9（）()・]+経路 経路"""),
                Regex("""[\p{IsHan}\p{IsKatakana}A-Za-z0-9（）()・]+チャネル チャネル"""),
                Regex("""[\p{IsHan}\p{IsKatakana}A-Za-z0-9（）()・]+受容体 受容体"""),
                Regex("""[\p{IsHan}\p{IsKatakana}A-Za-z0-9（）()・]+酵素 酵素"""),
                Regex("""([\p{IsHan}\p{IsKatakana}A-Za-z0-9（）()・]+) \d+\. \1,"""),
            )
    }
}
