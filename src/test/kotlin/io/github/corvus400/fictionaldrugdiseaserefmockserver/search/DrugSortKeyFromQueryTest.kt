package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DrugSortKeyFromQueryTest {
    @Test
    fun `fromQuery maps canonical query strings to matching enum values`() {
        assertEquals(DrugSortKey.REVISED_AT_DESC, DrugSortKey.fromQuery("-revised_at"))
        assertEquals(DrugSortKey.BRAND_NAME_KANA_ASC, DrugSortKey.fromQuery("brand_name_kana"))
        assertEquals(DrugSortKey.ATC_CODE_ASC, DrugSortKey.fromQuery("atc_code"))
        assertEquals(
            DrugSortKey.THERAPEUTIC_CATEGORY_NAME_ASC,
            DrugSortKey.fromQuery("therapeutic_category_name"),
        )
    }

    @Test
    fun `fromQuery returns REVISED_AT_DESC when raw is null`() {
        assertEquals(DrugSortKey.REVISED_AT_DESC, DrugSortKey.fromQuery(null))
    }

    @Test
    fun `fromQuery throws IllegalArgumentException for unknown sort keys with raw value`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            DrugSortKey.fromQuery("unknown_key")
        }

        assertEquals("Unknown sort key: unknown_key", exception.message)
    }
}
