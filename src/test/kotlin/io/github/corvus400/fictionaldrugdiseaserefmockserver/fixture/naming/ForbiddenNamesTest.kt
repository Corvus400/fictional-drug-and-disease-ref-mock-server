package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ForbiddenNamesTest {
    @Test
    fun `contains returns true for listed PMDA entry`() {
        assertTrue(ForbiddenNames.contains("アスピリン"))
    }

    @Test
    fun `contains returns true for listed ICD-10 entry`() {
        assertTrue(ForbiddenNames.contains("糖尿病"))
    }

    @Test
    fun `contains returns false for fiction name not in list`() {
        assertFalse(ForbiddenNames.contains("ゼクサベン"))
    }

    @Test
    fun `containsClassSuffix rejects drug class suffixes`() {
        assertTrue(
            ForbiddenNames.containsClassSuffix("ロサスタチン"),
            "containsClassSuffix must reject names ending with a drug class suffix",
        )
        assertFalse(
            ForbiddenNames.containsClassSuffix("カイクン"),
            "containsClassSuffix must allow fiction names without a drug class suffix",
        )
    }

    @Test
    fun `all set contains at least 60 entries (30 PMDA plus 30 ICD-10)`() {
        assertTrue(ForbiddenNames.all.size >= 60)
    }
}
