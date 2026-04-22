package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon

data class LexiconEntry(
    val token: String,
    val frequency: Int,
    val pos: PartOfSpeech,
    val meaning: String,
    val meaningKana: String,
    val pattern: Pattern,
    val katakana: String,
    val sourceLines: List<String> = emptyList(),
)
