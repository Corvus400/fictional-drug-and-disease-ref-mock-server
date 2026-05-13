package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DrugSortKeyFromQueryTest {
    @Test
    fun `fromQuery maps canonical query strings to matching enum values`() {
        assertEquals(
            expected = mapOf(
                "-revised_at" to DrugSortKey.REVISED_AT_DESC,
                "brand_name_kana" to DrugSortKey.BRAND_NAME_KANA_ASC,
                "atc_code" to DrugSortKey.ATC_CODE_ASC,
                "therapeutic_category_name" to DrugSortKey.THERAPEUTIC_CATEGORY_NAME_ASC,
            ),
            actual = listOf("-revised_at", "brand_name_kana", "atc_code", "therapeutic_category_name")
                .associateWith { DrugSortKey.fromQuery(it) },
            message = "DrugSortKey.fromQuery must map every canonical query string",
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
