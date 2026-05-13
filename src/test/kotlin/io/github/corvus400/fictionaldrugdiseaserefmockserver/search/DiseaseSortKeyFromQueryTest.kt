package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DiseaseSortKeyFromQueryTest {
    @Test
    fun `fromQuery maps canonical query strings to matching enum values`() {
        assertEquals(
            DiseaseSortKey.REVISED_AT_DESC,
            DiseaseSortKey.fromQuery("-revised_at"),
            "fromQuery must map -revised_at to REVISED_AT_DESC",
        )
        assertEquals(
            DiseaseSortKey.NAME_KANA_ASC,
            DiseaseSortKey.fromQuery("name_kana"),
            "fromQuery must map name_kana to NAME_KANA_ASC",
        )
        assertEquals(
            DiseaseSortKey.ICD10_CHAPTER_ASC,
            DiseaseSortKey.fromQuery("icd10_chapter"),
            "fromQuery must map icd10_chapter to ICD10_CHAPTER_ASC",
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
