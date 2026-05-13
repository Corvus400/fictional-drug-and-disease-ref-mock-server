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
            0,
            blueprint.index,
            "contract assertion failed"
        )
        assertEquals(
            Icd10Chapter.CHAPTER_I,
            blueprint.icd10Chapter,
            "contract assertion failed"
        )
        assertEquals(
            Chronicity.ACUTE,
            blueprint.chronicity,
            "contract assertion failed"
        )
        assertEquals(
            true,
            blueprint.isInfectious,
            "contract assertion failed"
        )
        assertEquals(
            false,
            blueprint.isMentalDisorder,
            "contract assertion failed"
        )
        assertEquals(
            false,
            blueprint.isRareDisease,
            "contract assertion failed"
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
}
