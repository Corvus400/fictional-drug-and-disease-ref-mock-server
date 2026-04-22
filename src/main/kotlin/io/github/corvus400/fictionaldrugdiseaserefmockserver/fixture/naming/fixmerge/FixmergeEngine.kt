package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot

interface FixmergeEngine {
    fun coinName(slot: NameSlot, seed: Long): CoinedName
}

object FixmergeEngineFactory {
    fun default(): FixmergeEngine {
        TODO("not implemented")
    }
}
