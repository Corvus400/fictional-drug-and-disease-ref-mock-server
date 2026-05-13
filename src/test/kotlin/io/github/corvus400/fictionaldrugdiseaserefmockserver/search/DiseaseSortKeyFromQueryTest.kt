package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DiseaseSortKeyFromQueryTest {
    @Test
    fun `fromQuery maps canonical query strings to matching enum values`() {
        assertEquals(
            expected = mapOf(
                "-revised_at" to DiseaseSortKey.REVISED_AT_DESC,
                "name_kana" to DiseaseSortKey.NAME_KANA_ASC,
                "icd10_chapter" to DiseaseSortKey.ICD10_CHAPTER_ASC,
            ),
            actual = mapOf(
                "-revised_at" to DiseaseSortKey.fromQuery("-revised_at"),
                "name_kana" to DiseaseSortKey.fromQuery("name_kana"),
                "icd10_chapter" to DiseaseSortKey.fromQuery("icd10_chapter"),
            ),
            "fromQuery must map canonical disease sort query strings",
        )
    }

    @Test
    fun `fromQuery returns REVISED_AT_DESC when raw is null`() {
        assertEquals(DiseaseSortKey.REVISED_AT_DESC, DiseaseSortKey.fromQuery(null))
    }

    @Test
    fun `fromQuery throws IllegalArgumentException for unknown sort keys with raw value`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            DiseaseSortKey.fromQuery("unknown_key")
        }

        assertEquals("Unknown sort key: unknown_key", exception.message)
    }
}
