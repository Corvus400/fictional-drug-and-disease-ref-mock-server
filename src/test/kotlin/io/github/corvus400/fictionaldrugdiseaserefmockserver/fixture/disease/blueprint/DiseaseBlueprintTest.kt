package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DiseaseBlueprintTest {
    @Test
    fun `DiseaseBlueprint holds index and icd10Chapter and other classification axes`() {
        val blueprint =
            DiseaseBlueprint(
                index = 0,
                icd10Chapter = Icd10Chapter.CHAPTER_I,
                chronicity = Chronicity.ACUTE,
                isInfectious = true,
                isMentalDisorder = false,
                isRareDisease = false,
            )
        assertEquals(
            expected = DiseaseBlueprintSnapshot(
                index = 0,
                icd10Chapter = Icd10Chapter.CHAPTER_I,
                chronicity = Chronicity.ACUTE,
                isInfectious = true,
                isMentalDisorder = false,
                isRareDisease = false,
            ),
            actual = blueprint.snapshot(),
        )
    }

    @Test
    fun `DiseaseBlueprint rejects negative index`() {
        assertFailsWith<IllegalArgumentException> {
            DiseaseBlueprint(
                index = -1,
                icd10Chapter = Icd10Chapter.CHAPTER_I,
                chronicity = Chronicity.ACUTE,
                isInfectious = false,
                isMentalDisorder = false,
                isRareDisease = false,
            )
        }
    }

    private fun DiseaseBlueprint.snapshot(): DiseaseBlueprintSnapshot =
        DiseaseBlueprintSnapshot(
            index = index,
            icd10Chapter = icd10Chapter,
            chronicity = chronicity,
            isInfectious = isInfectious,
            isMentalDisorder = isMentalDisorder,
            isRareDisease = isRareDisease,
        )

    private data class DiseaseBlueprintSnapshot(
        val index: Int,
        val icd10Chapter: Icd10Chapter,
        val chronicity: Chronicity,
        val isInfectious: Boolean,
        val isMentalDisorder: Boolean,
        val isRareDisease: Boolean,
    )
}
