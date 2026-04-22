package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

/**
 * Declarative factory producing the 80-disease Blueprint inventory per the ICD-10
 * chapter distribution mandated by the disease model specification (§組合せ数計算 §(E') 分布設計).
 *
 * The Blueprint layer only captures classification axes and a seed-determining index.
 * Conditional-required fields (感染経路 / 重症度分類 / 急性期プロトコル 等) are the
 * Generator layer's responsibility.
 */
object DiseaseBlueprintFactory {
    fun build(): List<DiseaseBlueprint> {
        val chapters: List<Icd10Chapter> =
            ICD10_DISTRIBUTION.flatMap { (chapter, count) -> List(count) { chapter } }
        return chapters.mapIndexed { index, chapter ->
            DiseaseBlueprint(
                index = index,
                icd10Chapter = chapter,
                chronicity = deriveChronicity(chapter = chapter, index = index),
                isInfectious = chapter == Icd10Chapter.CHAPTER_I,
                isMentalDisorder = chapter == Icd10Chapter.CHAPTER_V,
                isRareDisease = deriveIsRareDisease(chapter = chapter, index = index),
            )
        }
    }

    private fun deriveChronicity(
        chapter: Icd10Chapter,
        index: Int,
    ): Chronicity {
        if (chapter == Icd10Chapter.CHAPTER_I) {
            return Chronicity.ACUTE
        }
        if (chapter == Icd10Chapter.CHAPTER_V) {
            return if (index % RELAPSING_CYCLE == 0) Chronicity.RELAPSING else Chronicity.CHRONIC
        }
        if (chapter in CHRONIC_CHAPTERS) {
            return Chronicity.CHRONIC
        }
        if (chapter in SUBACUTE_CHAPTERS) {
            return Chronicity.SUBACUTE
        }
        return Chronicity.ACUTE
    }

    private fun deriveIsRareDisease(
        chapter: Icd10Chapter,
        index: Int,
    ): Boolean {
        if (chapter == Icd10Chapter.CHAPTER_XVII) {
            return true
        }
        if (chapter == Icd10Chapter.CHAPTER_XXII) {
            return true
        }
        if (chapter == Icd10Chapter.CHAPTER_III) {
            return index % RARE_CYCLE == 0
        }
        return false
    }

    private const val RELAPSING_CYCLE: Int = 3
    private const val RARE_CYCLE: Int = 2

    private val CHRONIC_CHAPTERS: Set<Icd10Chapter> =
        setOf(
            Icd10Chapter.CHAPTER_II,
            Icd10Chapter.CHAPTER_IV,
            Icd10Chapter.CHAPTER_VI,
            Icd10Chapter.CHAPTER_IX,
            Icd10Chapter.CHAPTER_X,
            Icd10Chapter.CHAPTER_XI,
            Icd10Chapter.CHAPTER_XIII,
            Icd10Chapter.CHAPTER_XIV,
        )

    private val SUBACUTE_CHAPTERS: Set<Icd10Chapter> =
        setOf(
            Icd10Chapter.CHAPTER_III,
            Icd10Chapter.CHAPTER_XII,
            Icd10Chapter.CHAPTER_XVIII,
        )

    private val ICD10_DISTRIBUTION: Map<Icd10Chapter, Int> =
        linkedMapOf(
            Icd10Chapter.CHAPTER_I to 6,
            Icd10Chapter.CHAPTER_II to 6,
            Icd10Chapter.CHAPTER_III to 3,
            Icd10Chapter.CHAPTER_IV to 6,
            Icd10Chapter.CHAPTER_V to 6,
            Icd10Chapter.CHAPTER_VI to 4,
            Icd10Chapter.CHAPTER_VII to 1,
            Icd10Chapter.CHAPTER_VIII to 1,
            Icd10Chapter.CHAPTER_IX to 8,
            Icd10Chapter.CHAPTER_X to 6,
            Icd10Chapter.CHAPTER_XI to 6,
            Icd10Chapter.CHAPTER_XII to 4,
            Icd10Chapter.CHAPTER_XIII to 4,
            Icd10Chapter.CHAPTER_XIV to 4,
            Icd10Chapter.CHAPTER_XV to 4,
            Icd10Chapter.CHAPTER_XVI to 2,
            Icd10Chapter.CHAPTER_XVII to 2,
            Icd10Chapter.CHAPTER_XVIII to 2,
            Icd10Chapter.CHAPTER_XIX to 2,
            Icd10Chapter.CHAPTER_XX to 1,
            Icd10Chapter.CHAPTER_XXI to 1,
            Icd10Chapter.CHAPTER_XXII to 1,
        )
}
