package io.github.corvus400.fictionaldrugdiseaserefmockserver.config

import kotlin.test.Test
import kotlin.test.assertContains

class DisclaimerTest {
    @Test
    fun `Disclaimer SHORT contains FICTIONAL DATA marker`() {
        assertContains(Disclaimer.SHORT, "FICTIONAL DATA")
        assertContains(Disclaimer.SHORT, "架空データ")
    }

    @Test
    fun `Disclaimer FULL_JA_EN contains both Japanese and English warnings`() {
        assertContains(Disclaimer.FULL_JA_EN, "医療判断")
        assertContains(Disclaimer.FULL_JA_EN, "DO NOT use")
    }
}
