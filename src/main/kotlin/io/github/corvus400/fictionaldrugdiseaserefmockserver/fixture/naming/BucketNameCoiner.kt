package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot

class BucketNameCoiner(
    private val adapter: FixmergeNameAdapter,
) {
    fun coin(
        bucket: List<String>,
        blueprintIndex: Int,
        slot: NameSlot,
        offset: Int,
    ): CoinedName {
        val sourceIndex = ((blueprintIndex * SEED_INDEX_PRIME) + slot.ordinal + offset).mod(other = bucket.size)
        val sourceToken = bucket[sourceIndex]
        val seed = stableHash(id = sourceToken, slot = slot.ordinal, index = 0)
        return adapter.coin(slot = slot, seed = seed)
    }

    companion object {
        private const val SEED_INDEX_PRIME: Int = 31
    }
}
