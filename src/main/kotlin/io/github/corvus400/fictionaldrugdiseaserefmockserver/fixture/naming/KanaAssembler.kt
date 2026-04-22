package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

private const val SLOT_SALT: Long = 0x517CC1B727220A95L
private const val SYLLABLE_SALT: Long = -0x61C8864680B583EBL // 0x9E3779B97F4A7C15 as signed Long
private const val VOWEL_FLIP_MASK: Long = 0x1L
private const val MORAIC_NASAL_FALLBACK: String = "ン"

object KanaAssembler {
    fun assemble(
        seed: Long,
        pattern: NamePattern,
    ): String {
        val range = requireNotNull(FictionLanguage.syllableRangeByPattern[pattern]) {
            "No syllable range registered for pattern=$pattern"
        }
        val span = range.last - range.first + 1
        val syllableCount = range.first + (pickPositive(seed xor SLOT_SALT) % span)
        val builder = StringBuilder()
        for (index in 0 until syllableCount) {
            val syllableSeed = seed xor (index.toLong() * SYLLABLE_SALT)
            val consonant =
                FictionLanguage.initialConsonants[
                    pickPositive(syllableSeed) % FictionLanguage.initialConsonants.size,
                ]
            val vowel =
                FictionLanguage.vowels[
                    pickPositive(syllableSeed xor VOWEL_FLIP_MASK) % FictionLanguage.vowels.size,
                ]
            val romaji = consonant + vowel
            builder.append(FictionLanguage.romajiToKana[romaji] ?: MORAIC_NASAL_FALLBACK)
        }
        return builder.toString()
    }

    private fun pickPositive(value: Long): Int = (value and Long.MAX_VALUE).toInt() and Int.MAX_VALUE
}
