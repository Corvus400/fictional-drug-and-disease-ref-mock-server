package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.FakeDiseaseFixturesBuilder
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.TherapeuticCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugRelatedDiseaseIdsSemanticTest {
    @Test
    fun `buildRelatedDiseaseIds for ATC A returns disease ids whose chapter is CHAPTER_XI or CHAPTER_IV`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.ALIMENTARY_METABOLISM,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_XI, Icd10Chapter.CHAPTER_IV),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC B returns disease ids whose chapter is CHAPTER_III`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.BLOOD_BLOOD_FORMING_ORGANS,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_III),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC C returns disease ids whose chapter is CHAPTER_IX`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.CARDIOVASCULAR_SYSTEM,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_IX),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC D returns disease ids whose chapter is CHAPTER_XII`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.DERMATOLOGICAL,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_XII),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC G returns disease ids whose chapter is CHAPTER_XIV or CHAPTER_XV`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.GENITO_URINARY_SYSTEM_AND_SEX_HORMONES,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_XIV, Icd10Chapter.CHAPTER_XV),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC H returns disease ids whose chapter is CHAPTER_IV`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.SYSTEMIC_HORMONAL_PREPARATIONS,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_IV),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC J returns disease ids whose chapter is CHAPTER_I`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.ANTI_INFECTIVES_FOR_SYSTEMIC_USE,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_I),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC L returns disease ids whose chapter is CHAPTER_II or CHAPTER_III`() {
        val diseases =
            FakeDiseaseFixturesBuilder()
                .withChapter(Icd10Chapter.CHAPTER_II, listOf("disease_0001", "disease_0002"))
                .withChapter(Icd10Chapter.CHAPTER_III, listOf("disease_0003", "disease_0004"))
                .withChapter(Icd10Chapter.CHAPTER_I, listOf("disease_0005", "disease_0006"))
                .build()
        val chapterByDiseaseId = diseases.associate { disease -> disease.id to disease.icd10Chapter }

        val relatedDiseaseIds =
            DrugMetaBuilders.buildRelatedDiseaseIds(
                id = "drug_0001",
                therapeuticCategory = TherapeuticCategory.ANTINEOPLASTIC_IMMUNOMODULATING,
                diseaseFixtures = diseases,
            )

        assertTrue(
            actual = relatedDiseaseIds.all { id ->
                chapterByDiseaseId.getValue(id) in listOf(Icd10Chapter.CHAPTER_II, Icd10Chapter.CHAPTER_III)
            },
            message = "ATC=L related disease ids must be limited to CHAPTER_II or CHAPTER_III: $relatedDiseaseIds",
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC M returns disease ids whose chapter is CHAPTER_XIII`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.MUSCULO_SKELETAL_SYSTEM,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_XIII),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC N returns disease ids whose chapter is CHAPTER_VI or CHAPTER_V`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.NERVOUS_SYSTEM,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_VI, Icd10Chapter.CHAPTER_V),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC P returns disease ids whose chapter is CHAPTER_I`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.ANTIPARASITIC_PRODUCTS,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_I),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC R returns disease ids whose chapter is CHAPTER_X`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.RESPIRATORY_SYSTEM,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_X),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC S returns disease ids whose chapter is CHAPTER_VII or CHAPTER_VIII`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.SENSORY_ORGANS,
            expectedChapters = listOf(Icd10Chapter.CHAPTER_VII, Icd10Chapter.CHAPTER_VIII),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds for ATC V returns disease ids whose chapter is mapped to various`() {
        assertRelatedDiseaseChapters(
            therapeuticCategory = TherapeuticCategory.VARIOUS,
            expectedChapters = listOf(
                Icd10Chapter.CHAPTER_XVIII,
                Icd10Chapter.CHAPTER_XXI,
                Icd10Chapter.CHAPTER_XXII,
            ),
        )
    }

    @Test
    fun `buildRelatedDiseaseIds falls back to all provided diseases when filtered count is zero`() {
        val diseases =
            FakeDiseaseFixturesBuilder()
                .withChapter(Icd10Chapter.CHAPTER_I, listOf("disease_9001", "disease_9002"))
                .build()
        val candidateIds = diseases.map { disease -> disease.id }.toSet()

        val relatedDiseaseIds =
            DrugMetaBuilders.buildRelatedDiseaseIds(
                id = "drug_0001",
                therapeuticCategory = TherapeuticCategory.CARDIOVASCULAR_SYSTEM,
                diseaseFixtures = diseases,
            )

        assertTrue(
            actual = relatedDiseaseIds.all { id -> id in candidateIds },
            message = "fallback must select from provided disease fixtures: $relatedDiseaseIds",
        )
    }

    @Test
    fun `buildRelatedDiseaseIds is deterministic for the same drug id category and disease fixtures`() {
        val diseases = semanticDiseaseFixtures()

        assertEquals(
            expected =
            DrugMetaBuilders.buildRelatedDiseaseIds(
                id = "drug_0001",
                therapeuticCategory = TherapeuticCategory.ALIMENTARY_METABOLISM,
                diseaseFixtures = diseases,
            ),
            actual =
            DrugMetaBuilders.buildRelatedDiseaseIds(
                id = "drug_0001",
                therapeuticCategory = TherapeuticCategory.ALIMENTARY_METABOLISM,
                diseaseFixtures = diseases,
            ),
        )
    }

    private fun assertRelatedDiseaseChapters(
        therapeuticCategory: TherapeuticCategory,
        expectedChapters: List<Icd10Chapter>,
    ) {
        val diseases = semanticDiseaseFixtures()
        val chapterByDiseaseId = diseases.associate { disease -> disease.id to disease.icd10Chapter }

        val relatedDiseaseIds =
            DrugMetaBuilders.buildRelatedDiseaseIds(
                id = "drug_${therapeuticCategory.atcInitial.lowercaseChar()}",
                therapeuticCategory = therapeuticCategory,
                diseaseFixtures = diseases,
            )

        assertTrue(
            actual = relatedDiseaseIds.all { id -> chapterByDiseaseId.getValue(id) in expectedChapters },
            message = "related disease ids for $therapeuticCategory must be limited to $expectedChapters: " +
                relatedDiseaseIds,
        )
    }

    private fun semanticDiseaseFixtures(): List<Disease> {
        val builder = FakeDiseaseFixturesBuilder()
        Icd10Chapter.entries.forEachIndexed { index, chapter ->
            builder.withChapter(chapter = chapter, ids = listOf("disease_${index.toString().padStart(4, '0')}"))
        }
        return builder.build()
    }
}
