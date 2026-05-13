package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.TherapeuticCategory

object AtcIcd10Mapping {
    private val ATC_TO_CHAPTERS: Map<TherapeuticCategory, List<Icd10Chapter>> =
        mapOf(
            TherapeuticCategory.ALIMENTARY_METABOLISM to
                listOf(Icd10Chapter.CHAPTER_XI, Icd10Chapter.CHAPTER_IV),
            TherapeuticCategory.BLOOD_BLOOD_FORMING_ORGANS to listOf(Icd10Chapter.CHAPTER_III),
            TherapeuticCategory.CARDIOVASCULAR_SYSTEM to listOf(Icd10Chapter.CHAPTER_IX),
            TherapeuticCategory.DERMATOLOGICAL to listOf(Icd10Chapter.CHAPTER_XII),
            TherapeuticCategory.GENITO_URINARY_SYSTEM_AND_SEX_HORMONES to
                listOf(Icd10Chapter.CHAPTER_XIV, Icd10Chapter.CHAPTER_XV),
            TherapeuticCategory.SYSTEMIC_HORMONAL_PREPARATIONS to listOf(Icd10Chapter.CHAPTER_IV),
            TherapeuticCategory.ANTI_INFECTIVES_FOR_SYSTEMIC_USE to listOf(Icd10Chapter.CHAPTER_I),
            TherapeuticCategory.ANTINEOPLASTIC_IMMUNOMODULATING to
                listOf(Icd10Chapter.CHAPTER_II, Icd10Chapter.CHAPTER_III),
            TherapeuticCategory.MUSCULO_SKELETAL_SYSTEM to listOf(Icd10Chapter.CHAPTER_XIII),
            TherapeuticCategory.NERVOUS_SYSTEM to listOf(Icd10Chapter.CHAPTER_VI, Icd10Chapter.CHAPTER_V),
            TherapeuticCategory.ANTIPARASITIC_PRODUCTS to listOf(Icd10Chapter.CHAPTER_I),
            TherapeuticCategory.RESPIRATORY_SYSTEM to listOf(Icd10Chapter.CHAPTER_X),
            TherapeuticCategory.SENSORY_ORGANS to listOf(Icd10Chapter.CHAPTER_VII, Icd10Chapter.CHAPTER_VIII),
            TherapeuticCategory.VARIOUS to
                listOf(Icd10Chapter.CHAPTER_XVIII, Icd10Chapter.CHAPTER_XXI, Icd10Chapter.CHAPTER_XXII),
        )

    fun chaptersFor(category: TherapeuticCategory): List<Icd10Chapter> =
        ATC_TO_CHAPTERS.getValue(category)

    fun categoriesFor(chapter: Icd10Chapter): List<TherapeuticCategory> =
        ATC_TO_CHAPTERS
            .filterValues { chapters -> chapter in chapters }
            .keys
            .toList()
}
