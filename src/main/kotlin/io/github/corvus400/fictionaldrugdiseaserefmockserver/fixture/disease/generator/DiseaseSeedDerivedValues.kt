package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash

internal object DiseaseSeedDerivedValues {
    fun synonymCount(id: String): Int =
        countFor(id = id, slot = DiseaseFieldSlot.SYNONYM_COUNT_PICK)

    fun differentialCount(id: String): Int =
        countFor(id = id, slot = DiseaseFieldSlot.DIFFERENTIAL_COUNT_PICK)

    fun complicationCount(id: String): Int =
        countFor(id = id, slot = DiseaseFieldSlot.COMPLICATION_COUNT_PICK)

    fun onsetAgeSpan(id: String): Int {
        val seed = stableHash(id = id, slot = DiseaseFieldSlot.EPIDEMIOLOGY_ONSET_SPAN_PICK.ordinal, index = 0)
        return ValueRangeGenerator.pickInRange(seed = seed, range = ONSET_AGE_SPAN_RANGE)
    }

    private fun countFor(
        id: String,
        slot: DiseaseFieldSlot,
    ): Int {
        val seed = stableHash(id = id, slot = slot.ordinal, index = 0)
        return ValueRangeGenerator.pickCount(seed = seed, range = COUNT_RANGE)
    }

    private val COUNT_RANGE: IntRange = 1..3
    private val ONSET_AGE_SPAN_RANGE: IntRange = 5..20
}
