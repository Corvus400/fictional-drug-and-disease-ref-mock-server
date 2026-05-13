package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.TherapeuticCategory
import kotlin.test.Test
import kotlin.test.assertEquals

class AtcIcd10MappingTest {
    @Test
    fun `chaptersFor returns CHAPTER_XI and CHAPTER_IV for alimentary metabolism ATC`() {
        assertChapters(
            category = TherapeuticCategory.ALIMENTARY_METABOLISM,
            expected = listOf(Icd10Chapter.CHAPTER_XI, Icd10Chapter.CHAPTER_IV),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_III for blood ATC`() {
        assertChapters(
            category = TherapeuticCategory.BLOOD_BLOOD_FORMING_ORGANS,
            expected = listOf(Icd10Chapter.CHAPTER_III),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_IX for cardiovascular ATC`() {
        assertChapters(
            category = TherapeuticCategory.CARDIOVASCULAR_SYSTEM,
            expected = listOf(Icd10Chapter.CHAPTER_IX),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_XII for dermatological ATC`() {
        assertChapters(
            category = TherapeuticCategory.DERMATOLOGICAL,
            expected = listOf(Icd10Chapter.CHAPTER_XII),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_XIV and CHAPTER_XV for genito urinary ATC`() {
        assertChapters(
            category = TherapeuticCategory.GENITO_URINARY_SYSTEM_AND_SEX_HORMONES,
            expected = listOf(Icd10Chapter.CHAPTER_XIV, Icd10Chapter.CHAPTER_XV),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_IV for systemic hormonal ATC`() {
        assertChapters(
            category = TherapeuticCategory.SYSTEMIC_HORMONAL_PREPARATIONS,
            expected = listOf(Icd10Chapter.CHAPTER_IV),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_I for anti infectives ATC`() {
        assertChapters(
            category = TherapeuticCategory.ANTI_INFECTIVES_FOR_SYSTEMIC_USE,
            expected = listOf(Icd10Chapter.CHAPTER_I),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_II and CHAPTER_III for antineoplastic ATC`() {
        assertChapters(
            category = TherapeuticCategory.ANTINEOPLASTIC_IMMUNOMODULATING,
            expected = listOf(Icd10Chapter.CHAPTER_II, Icd10Chapter.CHAPTER_III),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_XIII for musculo skeletal ATC`() {
        assertChapters(
            category = TherapeuticCategory.MUSCULO_SKELETAL_SYSTEM,
            expected = listOf(Icd10Chapter.CHAPTER_XIII),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_VI and CHAPTER_V for nervous ATC`() {
        assertChapters(
            category = TherapeuticCategory.NERVOUS_SYSTEM,
            expected = listOf(Icd10Chapter.CHAPTER_VI, Icd10Chapter.CHAPTER_V),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_I for antiparasitic ATC`() {
        assertChapters(
            category = TherapeuticCategory.ANTIPARASITIC_PRODUCTS,
            expected = listOf(Icd10Chapter.CHAPTER_I),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_X for respiratory ATC`() {
        assertChapters(
            category = TherapeuticCategory.RESPIRATORY_SYSTEM,
            expected = listOf(Icd10Chapter.CHAPTER_X),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_VII and CHAPTER_VIII for sensory ATC`() {
        assertChapters(
            category = TherapeuticCategory.SENSORY_ORGANS,
            expected = listOf(Icd10Chapter.CHAPTER_VII, Icd10Chapter.CHAPTER_VIII),
        )
    }

    @Test
    fun `chaptersFor returns CHAPTER_XVIII CHAPTER_XXI and CHAPTER_XXII for various ATC`() {
        assertChapters(
            category = TherapeuticCategory.VARIOUS,
            expected = listOf(Icd10Chapter.CHAPTER_XVIII, Icd10Chapter.CHAPTER_XXI, Icd10Chapter.CHAPTER_XXII),
        )
    }

    @Test
    fun `categoriesFor returns anti infectives and antiparasitic for CHAPTER_I`() {
        assertEquals(
            expected = listOf(
                TherapeuticCategory.ANTI_INFECTIVES_FOR_SYSTEMIC_USE,
                TherapeuticCategory.ANTIPARASITIC_PRODUCTS,
            ),
            actual = AtcIcd10Mapping.categoriesFor(Icd10Chapter.CHAPTER_I),
        )
    }

    @Test
    fun `categoriesFor exposes the reverse ICD10 to ATC mapping for every chapter`() {
        assertEquals(
            expected = mapOf(
                Icd10Chapter.CHAPTER_I to listOf(
                    TherapeuticCategory.ANTI_INFECTIVES_FOR_SYSTEMIC_USE,
                    TherapeuticCategory.ANTIPARASITIC_PRODUCTS,
                ),
                Icd10Chapter.CHAPTER_II to listOf(TherapeuticCategory.ANTINEOPLASTIC_IMMUNOMODULATING),
                Icd10Chapter.CHAPTER_III to listOf(
                    TherapeuticCategory.BLOOD_BLOOD_FORMING_ORGANS,
                    TherapeuticCategory.ANTINEOPLASTIC_IMMUNOMODULATING,
                ),
                Icd10Chapter.CHAPTER_IV to listOf(
                    TherapeuticCategory.ALIMENTARY_METABOLISM,
                    TherapeuticCategory.SYSTEMIC_HORMONAL_PREPARATIONS,
                ),
                Icd10Chapter.CHAPTER_V to listOf(TherapeuticCategory.NERVOUS_SYSTEM),
                Icd10Chapter.CHAPTER_VI to listOf(TherapeuticCategory.NERVOUS_SYSTEM),
                Icd10Chapter.CHAPTER_VII to listOf(TherapeuticCategory.SENSORY_ORGANS),
                Icd10Chapter.CHAPTER_VIII to listOf(TherapeuticCategory.SENSORY_ORGANS),
                Icd10Chapter.CHAPTER_IX to listOf(TherapeuticCategory.CARDIOVASCULAR_SYSTEM),
                Icd10Chapter.CHAPTER_X to listOf(TherapeuticCategory.RESPIRATORY_SYSTEM),
                Icd10Chapter.CHAPTER_XI to listOf(TherapeuticCategory.ALIMENTARY_METABOLISM),
                Icd10Chapter.CHAPTER_XII to listOf(TherapeuticCategory.DERMATOLOGICAL),
                Icd10Chapter.CHAPTER_XIII to listOf(TherapeuticCategory.MUSCULO_SKELETAL_SYSTEM),
                Icd10Chapter.CHAPTER_XIV to listOf(TherapeuticCategory.GENITO_URINARY_SYSTEM_AND_SEX_HORMONES),
                Icd10Chapter.CHAPTER_XV to listOf(TherapeuticCategory.GENITO_URINARY_SYSTEM_AND_SEX_HORMONES),
                Icd10Chapter.CHAPTER_XVI to emptyList(),
                Icd10Chapter.CHAPTER_XVII to emptyList(),
                Icd10Chapter.CHAPTER_XVIII to listOf(TherapeuticCategory.VARIOUS),
                Icd10Chapter.CHAPTER_XIX to emptyList(),
                Icd10Chapter.CHAPTER_XX to emptyList(),
                Icd10Chapter.CHAPTER_XXI to listOf(TherapeuticCategory.VARIOUS),
                Icd10Chapter.CHAPTER_XXII to listOf(TherapeuticCategory.VARIOUS),
            ),
            actual = Icd10Chapter.entries.associateWith { chapter -> AtcIcd10Mapping.categoriesFor(chapter) },
        )
    }

    private fun assertChapters(
        category: TherapeuticCategory,
        expected: List<Icd10Chapter>,
    ) {
        assertEquals(
            expected = expected,
            actual = AtcIcd10Mapping.chaptersFor(category),
        )
    }
}
