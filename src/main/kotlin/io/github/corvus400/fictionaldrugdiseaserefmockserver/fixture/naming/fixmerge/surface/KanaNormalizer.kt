package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.surface

object KanaNormalizer {
    private const val HIRAGANA_BASE = 0x3041
    private const val HIRAGANA_LAST = 0x3096
    private const val KATAKANA_BASE = 0x30A1
    private const val KATAKANA_LAST = 0x30F6
    private const val HIRAGANA_TO_KATAKANA_OFFSET = 0x60

    fun hiraganaToKatakana(input: String): String {
        val builder = StringBuilder(input.length)
        for (ch in input) {
            val code = ch.code
            if (code in HIRAGANA_BASE..HIRAGANA_LAST) {
                builder.append((code + HIRAGANA_TO_KATAKANA_OFFSET).toChar())
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun katakanaToHiragana(input: String): String {
        val builder = StringBuilder(input.length)
        for (ch in input) {
            val code = ch.code
            if (code in KATAKANA_BASE..KATAKANA_LAST) {
                builder.append((code - HIRAGANA_TO_KATAKANA_OFFSET).toChar())
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun isKatakanaOnly(input: String): Boolean {
        if (input.isEmpty()) {
            return false
        }
        for (ch in input) {
            if (isAllowedInKatakanaOutput(ch = ch).not()) {
                return false
            }
        }
        return true
    }

    private fun isAllowedInKatakanaOutput(ch: Char): Boolean {
        val code = ch.code
        if (code in 0x30A0..0x30FF) {
            return true
        }
        return ch == ' ' || ch == '　'
    }
}
