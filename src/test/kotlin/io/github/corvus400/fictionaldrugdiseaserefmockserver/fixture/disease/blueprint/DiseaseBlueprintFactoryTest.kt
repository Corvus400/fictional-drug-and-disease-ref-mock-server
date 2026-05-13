package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseBlueprintFactoryTest {
    @Test
    fun `factory produces exactly 80 blueprints`() {
        val blueprints = DiseaseBlueprintFactory.build()
        assertEquals(80, blueprints.size)
    }

    @Test
    fun `factory distributes blueprints according to ICD-10 chapter counts`() {
        val blueprints = DiseaseBlueprintFactory.build()
        val distribution = blueprints.groupBy { it.icd10Chapter }.mapValues { it.value.size }
        val expected =
            mapOf(
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
        assertEquals(
            expected,
            distribution,
            "contract assertion failed"
        )
        assertEquals(
            22,
            distribution.keys.size,
            "contract assertion failed"
        )
    }

    @Test
    fun `each blueprint has a unique sequential id derivable from index in disease_NNNN format`() {
        val blueprints = DiseaseBlueprintFactory.build()
        val expectedIds = (1..80).map { "disease_%04d".format(it) }
        val actualIds = blueprints.map { "disease_%04d".format(it.index + 1) }
        assertEquals(expectedIds, actualIds)
    }

    @Test
    fun `isInfectious is true exactly for CHAPTER_I blueprints`() {
        val blueprints = DiseaseBlueprintFactory.build()
        val infectiousCount = blueprints.count { it.isInfectious }
        val chapter1Count = blueprints.count { it.icd10Chapter == Icd10Chapter.CHAPTER_I }
        assertEquals(chapter1Count, infectiousCount)
    }
}
