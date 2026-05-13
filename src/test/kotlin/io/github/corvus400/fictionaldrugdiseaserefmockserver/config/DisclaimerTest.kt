package io.github.corvus400.fictionaldrugdiseaserefmockserver.config

import kotlin.test.Test
import kotlin.test.assertEquals

class DisclaimerTest {
    @Test
    fun `Disclaimer SHORT contains FICTIONAL DATA marker`() {
        assertEquals(
            expected = emptyList(),
            actual = listOf("FICTIONAL DATA", "架空データ").filterNot { Disclaimer.SHORT.contains(it) },
            message = "Disclaimer.SHORT must contain all required fictional-data markers",
        )
    }

    @Test
    fun `Disclaimer FULL_JA_EN contains both Japanese and English warnings`() {
        assertEquals(
            expected = emptyList(),
            actual = listOf("医療判断", "DO NOT use").filterNot { Disclaimer.FULL_JA_EN.contains(it) },
            message = "Disclaimer.FULL_JA_EN must contain Japanese and English warning markers",
        )
    }
}
