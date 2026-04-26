package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.sampleDiseases
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseSearchServiceTest {
    @Test
    fun `applyKeyword with null keyword returns items unchanged (disease)`() {
        val items = sampleDiseases(n = 3)
        val result =
            DiseaseSearchService.applyKeyword(
                items = items,
                keyword = null,
                match = KeywordMatch.PARTIAL,
                target = DiseaseKeywordTarget.NAME,
            )
        assertEquals(items, result)
    }
}
