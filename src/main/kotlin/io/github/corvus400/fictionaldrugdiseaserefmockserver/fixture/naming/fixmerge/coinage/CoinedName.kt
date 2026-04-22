package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.Pattern

data class CoinedName(
    val latin: String,
    val katakana: String,
    val mixedSurface: String,
    val pattern: Pattern,
)
