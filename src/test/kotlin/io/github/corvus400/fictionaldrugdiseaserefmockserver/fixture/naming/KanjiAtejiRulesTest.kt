package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KanjiAtejiRulesTest {
    @Test
    fun `toAteji returns identical kanji for identical kana and seed`() {
        val first = KanjiAtejiRules.toAteji("アベシ", 12345L)
        val second = KanjiAtejiRules.toAteji("アベシ", 12345L)
        assertEquals(first, second)
    }

    @Test
    fun `toAteji produces kanji string with same length as kana input`() {
        val kana = "アベシノ"
        val kanji = KanjiAtejiRules.toAteji(kana, stableHash("drug_0001", slot = 0, index = 0))
        assertEquals(kana.length, kanji.length)
    }

    @Test
    fun `toAteji output contains only Kanji characters (Unicode block CJK Unified Ideographs)`() {
        val kanji = KanjiAtejiRules.toAteji("アベシ", stableHash("disease_0001", slot = 0, index = 0))
        kanji.forEach { c ->
            assertTrue(c.code in 0x4E00..0x9FFF, "'$c' is not a Kanji character")
        }
    }
}
