package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Icd10ChapterTest {
    @Test
    fun `fromSerialName chapter_i resolves to CHAPTER_I`() {
        assertEquals(
            expected = Icd10Chapter.CHAPTER_I,
            actual = Icd10Chapter.fromSerialName(serialName = "chapter_i"),
        )
    }

    @Test
    fun `fromSerialName chapter_xxii resolves to CHAPTER_XXII`() {
        assertEquals(
            expected = Icd10Chapter.CHAPTER_XXII,
            actual = Icd10Chapter.fromSerialName(serialName = "chapter_xxii"),
        )
    }

    @Test
    fun `fromSerialName returns null for legacy roman numeral key`() {
        assertNull(actual = Icd10Chapter.fromSerialName(serialName = "I"))
    }

    @Test
    fun `fromSerialName returns null for unknown key`() {
        assertNull(actual = Icd10Chapter.fromSerialName(serialName = "unknown"))
    }

    @Test
    fun `fromSerialName matches Icd10Chapter serialName for every entry`() {
        Icd10Chapter.entries.forEach { chapter ->
            assertEquals(
                expected = chapter,
                actual = Icd10Chapter.fromSerialName(serialName = chapter.serialName),
                message = "fromSerialName must round-trip via serialName for $chapter",
            )
        }
    }
}
