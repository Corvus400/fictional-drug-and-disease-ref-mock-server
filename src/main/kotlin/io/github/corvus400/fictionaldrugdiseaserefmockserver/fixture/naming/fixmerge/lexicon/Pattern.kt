package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon

enum class Pattern {
    A,
    B,
    C,
    UNKNOWN,
    ;

    companion object {
        fun fromString(value: String): Pattern {
            return when (value.uppercase()) {
                "A" -> A
                "B" -> B
                "C" -> C
                else -> UNKNOWN
            }
        }
    }
}
