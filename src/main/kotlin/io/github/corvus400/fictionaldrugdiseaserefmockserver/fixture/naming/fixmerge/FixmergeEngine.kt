package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.Coiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.forbidden.ForbiddenWordChecker
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.Lexicon
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.surface.LatinToKatakanaConverter

interface FixmergeEngine {
    fun coinName(slot: NameSlot, seed: Long): CoinedName
}

class DefaultFixmergeEngine(
    private val coiner: Coiner,
) : FixmergeEngine {
    override fun coinName(slot: NameSlot, seed: Long): CoinedName {
        return coiner.coin(slot = slot, seed = seed)
    }
}

object FixmergeEngineFactory {
    fun default(forbiddenBlacklist: Set<String> = emptySet()): FixmergeEngine {
        val lexicon = Lexicon.load()
        val converter = LatinToKatakanaConverter.load()
        val checker = ForbiddenWordChecker(forbidden = forbiddenBlacklist)
        val coiner = Coiner(
            lexicon = lexicon,
            converter = converter,
            checker = checker,
        )
        return DefaultFixmergeEngine(coiner = coiner)
    }
}
