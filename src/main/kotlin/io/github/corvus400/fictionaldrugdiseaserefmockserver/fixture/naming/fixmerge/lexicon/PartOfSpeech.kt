package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon

enum class PartOfSpeech {
    PRONOUN,
    PARTICLE,
    NOUN,
    VERB,
    ADJECTIVE,
    MODAL,
    TIME,
    INTERJ,
    OTHER,
    ;

    companion object {
        fun fromString(value: String): PartOfSpeech {
            return runCatching { valueOf(value.uppercase()) }.getOrDefault(OTHER)
        }
    }
}
