package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseCountryMappingTest {
    @Test
    fun `all 22 ICD10 chapters map by ordinal modulo 14`() {
        val countries = Country.entries
        assertEquals(14, countries.size)
        for (chapter in Icd10Chapter.entries) {
            val expected = countries[chapter.ordinal % countries.size]
            val actual = DiseaseCountryMapping.of(chapter = chapter)
            assertEquals(expected, actual, "chapter=$chapter (ordinal=${chapter.ordinal})")
        }
    }

    @Test
    fun `CHAPTER_I maps to Italy`() {
        assertEquals(Country.ITALY, DiseaseCountryMapping.of(chapter = Icd10Chapter.CHAPTER_I))
    }

    @Test
    fun `CHAPTER_XV wraps to Italy via modulo`() {
        assertEquals(Country.ITALY, DiseaseCountryMapping.of(chapter = Icd10Chapter.CHAPTER_XV))
    }

    @Test
    fun `CHAPTER_XXII maps to Korea`() {
        assertEquals(Country.KOREA, DiseaseCountryMapping.of(chapter = Icd10Chapter.CHAPTER_XXII))
    }
}
