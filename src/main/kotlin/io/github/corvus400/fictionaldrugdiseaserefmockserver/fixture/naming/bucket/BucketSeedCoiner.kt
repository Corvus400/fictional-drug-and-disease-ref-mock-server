package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.BucketNameCoiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot

object BucketSeedCoiner {
    fun coin(
        bucket: List<String>,
        seed: Long,
        slot: NameSlot,
        offset: Int = 0,
    ): String {
        val blueprintIndex = ValueRangeGenerator.pickInRange(seed = seed, range = BUCKET_BLUEPRINT_INDEX_RANGE)
        return BucketNameCoiner(adapter = FixmergeNameAdapter())
            .coin(
                bucket = bucket,
                blueprintIndex = blueprintIndex,
                slot = slot,
                offset = offset,
            ).katakana
    }

    private val BUCKET_BLUEPRINT_INDEX_RANGE = 0..999
}
