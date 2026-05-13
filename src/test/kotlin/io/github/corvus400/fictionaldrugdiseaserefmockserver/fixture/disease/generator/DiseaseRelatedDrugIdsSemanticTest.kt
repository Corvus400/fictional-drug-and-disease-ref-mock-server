package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.FakeDrugFixturesBuilder
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.TherapeuticCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiseaseRelatedDrugIdsSemanticTest {
    @Test
    fun `buildRelatedDrugIds for chapter I returns drug ids whose ATC category is mapped to infectious diseases`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_I,
            expectedCategories = setOf(
                TherapeuticCategory.ANTI_INFECTIVES_FOR_SYSTEMIC_USE,
                TherapeuticCategory.ANTIPARASITIC_PRODUCTS,
            ),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter II returns drug ids whose ATC category is antineoplastic`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_II,
            expectedCategories = setOf(TherapeuticCategory.ANTINEOPLASTIC_IMMUNOMODULATING),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter III returns blood or antineoplastic drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_III,
            expectedCategories = setOf(
                TherapeuticCategory.BLOOD_BLOOD_FORMING_ORGANS,
                TherapeuticCategory.ANTINEOPLASTIC_IMMUNOMODULATING,
            ),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter IV returns alimentary or hormonal drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_IV,
            expectedCategories = setOf(
                TherapeuticCategory.ALIMENTARY_METABOLISM,
                TherapeuticCategory.SYSTEMIC_HORMONAL_PREPARATIONS,
            ),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter V returns nervous system drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_V,
            expectedCategories = setOf(TherapeuticCategory.NERVOUS_SYSTEM),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter VI returns nervous system drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_VI,
            expectedCategories = setOf(TherapeuticCategory.NERVOUS_SYSTEM),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter VII returns sensory organ drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_VII,
            expectedCategories = setOf(TherapeuticCategory.SENSORY_ORGANS),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter VIII returns sensory organ drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_VIII,
            expectedCategories = setOf(TherapeuticCategory.SENSORY_ORGANS),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter IX returns cardiovascular drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_IX,
            expectedCategories = setOf(TherapeuticCategory.CARDIOVASCULAR_SYSTEM),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter X returns respiratory drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_X,
            expectedCategories = setOf(TherapeuticCategory.RESPIRATORY_SYSTEM),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter XI returns alimentary drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_XI,
            expectedCategories = setOf(TherapeuticCategory.ALIMENTARY_METABOLISM),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter XII returns dermatological drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_XII,
            expectedCategories = setOf(TherapeuticCategory.DERMATOLOGICAL),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter XIII returns musculoskeletal drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_XIII,
            expectedCategories = setOf(TherapeuticCategory.MUSCULO_SKELETAL_SYSTEM),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter XIV returns genitourinary drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_XIV,
            expectedCategories = setOf(TherapeuticCategory.GENITO_URINARY_SYSTEM_AND_SEX_HORMONES),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter XV returns pregnancy safe genitourinary drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_XV,
            expectedCategories = setOf(TherapeuticCategory.GENITO_URINARY_SYSTEM_AND_SEX_HORMONES),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter XVI falls back when no ATC category is mapped`() {
        assertRelatedDrugFallbackToAllFixtures(chapter = Icd10Chapter.CHAPTER_XVI)
    }

    @Test
    fun `buildRelatedDrugIds for chapter XVII falls back when no ATC category is mapped`() {
        assertRelatedDrugFallbackToAllFixtures(chapter = Icd10Chapter.CHAPTER_XVII)
    }

    @Test
    fun `buildRelatedDrugIds for chapter XVIII returns various drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_XVIII,
            expectedCategories = setOf(TherapeuticCategory.VARIOUS),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter XIX falls back when no ATC category is mapped`() {
        assertRelatedDrugFallbackToAllFixtures(chapter = Icd10Chapter.CHAPTER_XIX)
    }

    @Test
    fun `buildRelatedDrugIds for chapter XX falls back when no ATC category is mapped`() {
        assertRelatedDrugFallbackToAllFixtures(chapter = Icd10Chapter.CHAPTER_XX)
    }

    @Test
    fun `buildRelatedDrugIds for chapter XXI returns various drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_XXI,
            expectedCategories = setOf(TherapeuticCategory.VARIOUS),
        )
    }

    @Test
    fun `buildRelatedDrugIds for chapter XXII returns various drug ids`() {
        assertRelatedDrugCategories(
            chapter = Icd10Chapter.CHAPTER_XXII,
            expectedCategories = setOf(TherapeuticCategory.VARIOUS),
        )
    }

    @Test
    fun `buildRelatedDrugIds falls back to provided drug ids when mapped category candidates are absent`() {
        val drugFixtures =
            FakeDrugFixturesBuilder()
                .withCategory(
                    category = TherapeuticCategory.ALIMENTARY_METABOLISM,
                    ids = listOf("drug_9001", "drug_9002"),
                )
                .build()

        val relatedDrugIds =
            DiseaseNestedBuilders.buildRelatedDrugIds(
                id = "disease_9000",
                chapter = Icd10Chapter.CHAPTER_IX,
                drugFixtures = drugFixtures,
            )

        assertTrue(
            actual = relatedDrugIds.all { id -> id in setOf("drug_9001", "drug_9002") },
            message = "fallback relatedDrugIds must be selected from provided drug fixtures",
        )
    }

    @Test
    fun `buildRelatedDrugIds is deterministic for the same disease id chapter and drug fixtures`() {
        val drugFixtures = semanticDrugFixtures()

        val first =
            DiseaseNestedBuilders.buildRelatedDrugIds(
                id = "disease_0001",
                chapter = Icd10Chapter.CHAPTER_II,
                drugFixtures = drugFixtures,
            )
        val second =
            DiseaseNestedBuilders.buildRelatedDrugIds(
                id = "disease_0001",
                chapter = Icd10Chapter.CHAPTER_II,
                drugFixtures = drugFixtures,
            )

        assertEquals(expected = first, actual = second)
    }

    private fun assertRelatedDrugCategories(
        chapter: Icd10Chapter,
        expectedCategories: Set<TherapeuticCategory>,
    ) {
        val drugFixtures = semanticDrugFixtures()
        val categoryByDrugId =
            drugFixtures.associate { drug ->
                val category = TherapeuticCategory.fromAtcInitial(initial = drug.atcCode.first())
                    ?: error("unsupported ATC code ${drug.atcCode}")
                drug.id to category
            }
        val relatedDrugIds =
            DiseaseNestedBuilders.buildRelatedDrugIds(
                id = "disease_${chapter.ordinal.toString().padStart(length = 4, padChar = '0')}",
                chapter = chapter,
                drugFixtures = drugFixtures,
            )

        assertTrue(actual = relatedDrugIds.isNotEmpty(), message = "relatedDrugIds must not be empty")
        assertTrue(
            actual = relatedDrugIds.all { id -> categoryByDrugId.getValue(id) in expectedCategories },
            message = "related drug ids for $chapter must be limited to $expectedCategories: $relatedDrugIds",
        )
    }

    private fun assertRelatedDrugFallbackToAllFixtures(chapter: Icd10Chapter) {
        val drugFixtures = semanticDrugFixtures()
        val candidateDrugIds = drugFixtures.map { drug -> drug.id }.toSet()

        val relatedDrugIds =
            DiseaseNestedBuilders.buildRelatedDrugIds(
                id = "disease_${chapter.ordinal.toString().padStart(length = 4, padChar = '0')}",
                chapter = chapter,
                drugFixtures = drugFixtures,
            )

        assertTrue(actual = relatedDrugIds.isNotEmpty(), message = "relatedDrugIds must not be empty")
        assertTrue(
            actual = relatedDrugIds.all { id -> id in candidateDrugIds },
            message = "unmapped $chapter must fall back to provided fixtures: $relatedDrugIds",
        )
    }

    private fun semanticDrugFixtures(): List<Drug> =
        FakeDrugFixturesBuilder()
            .withCategory(TherapeuticCategory.ALIMENTARY_METABOLISM, listOf("drug_0000"))
            .withCategory(TherapeuticCategory.BLOOD_BLOOD_FORMING_ORGANS, listOf("drug_0020"))
            .withCategory(TherapeuticCategory.CARDIOVASCULAR_SYSTEM, listOf("drug_0026"))
            .withCategory(TherapeuticCategory.DERMATOLOGICAL, listOf("drug_0046"))
            .withCategory(TherapeuticCategory.GENITO_URINARY_SYSTEM_AND_SEX_HORMONES, listOf("drug_0054"))
            .withCategory(TherapeuticCategory.SYSTEMIC_HORMONAL_PREPARATIONS, listOf("drug_0058"))
            .withCategory(TherapeuticCategory.ANTI_INFECTIVES_FOR_SYSTEMIC_USE, listOf("drug_0062"))
            .withCategory(TherapeuticCategory.ANTINEOPLASTIC_IMMUNOMODULATING, listOf("drug_0081"))
            .withCategory(TherapeuticCategory.MUSCULO_SKELETAL_SYSTEM, listOf("drug_0084"))
            .withCategory(TherapeuticCategory.NERVOUS_SYSTEM, listOf("drug_0089"))
            .withCategory(TherapeuticCategory.ANTIPARASITIC_PRODUCTS, listOf("drug_0105"))
            .withCategory(TherapeuticCategory.RESPIRATORY_SYSTEM, listOf("drug_0106"))
            .withCategory(TherapeuticCategory.SENSORY_ORGANS, listOf("drug_0118"))
            .withCategory(TherapeuticCategory.VARIOUS, listOf("drug_0119"))
            .build()
}
