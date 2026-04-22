package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.FixmergeEngine
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.FixmergeEngineFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot

class FixmergeNameAdapter(private val engine: FixmergeEngine = DEFAULT_ENGINE) {
    fun coin(slot: NameSlot, seed: Long): CoinedName {
        return engine.coinName(slot = slot, seed = seed)
    }

    companion object {
        private val DEFAULT_ENGINE: FixmergeEngine by lazy {
            FixmergeEngineFactory.default(forbiddenBlacklist = ForbiddenNames.all)
        }
    }
}
