package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common

object ValueRangeGenerator {
    fun pickCount(
        seed: Long,
        range: IntRange,
    ): Int = range.first + mapSeedToRangeIndex(seed = seed, range = range)

    fun pickInRange(
        seed: Long,
        range: IntRange,
    ): Int = range.first + mapSeedToRangeIndex(seed = seed, range = range)

    fun <T> pickOne(
        seed: Long,
        candidates: List<T>,
    ): T {
        require(candidates.isNotEmpty()) {
            "candidates must not be empty"
        }
        val index = mapSeedToRangeIndex(seed = seed, range = candidates.indices)
        return candidates[index]
    }

    private fun mapSeedToRangeIndex(
        seed: Long,
        range: IntRange,
    ): Int {
        val size = range.last - range.first + 1
        return Math.floorMod(seed, size.toLong()).toInt()
    }
}
