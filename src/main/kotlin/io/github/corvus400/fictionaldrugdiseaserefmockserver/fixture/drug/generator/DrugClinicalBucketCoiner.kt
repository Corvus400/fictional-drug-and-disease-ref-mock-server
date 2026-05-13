package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.AdverseReactionByFreqSeedBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.AdverseReactionSeedBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketSeedCoiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.DrugCategorySeedBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand

internal object DrugClinicalBucketCoiner {
    fun coinedDrugCategory(
        atcInitial: Char,
        seed: Long,
        offset: Int,
    ): String =
        BucketSeedCoiner.coin(
            bucket = DrugCategorySeedBucketRepository.get(atcInitial = atcInitial),
            seed = seed,
            slot = NameSlot.DRUG_DRUG_CATEGORY,
            offset = offset,
        )

    fun coinedSeriousAdverseReaction(
        atcInitial: Char,
        seed: Long,
        offset: Int,
    ): String =
        BucketSeedCoiner.coin(
            bucket = AdverseReactionSeedBucketRepository.get(atcInitial = atcInitial),
            seed = seed,
            slot = NameSlot.DRUG_ADVERSE_REACTION,
            offset = offset,
        )

    fun coinedAdverseReactionByFrequency(
        id: String,
        atcInitial: Char,
        frequency: FrequencyBand,
    ): List<String> {
        val count = if (frequency == FrequencyBand.OVER_5_PERCENT) 2 else 1
        return (0 until count).map { offset ->
            val seed =
                stableHash(
                    id = id,
                    slot = slotFor(frequency = frequency).ordinal,
                    index = offset,
                )
            BucketSeedCoiner.coin(
                bucket = AdverseReactionByFreqSeedBucketRepository.get(
                    atcInitial = atcInitial,
                    frequency = frequency,
                ),
                seed = seed,
                slot = NameSlot.DRUG_ADVERSE_REACTION,
                offset = offset,
            )
        }
    }

    private fun slotFor(frequency: FrequencyBand): DrugFieldSlot =
        when (frequency) {
            FrequencyBand.OVER_5_PERCENT -> DrugFieldSlot.ADVERSE_FREQ_5
            FrequencyBand.BETWEEN_1_AND_5_PERCENT -> DrugFieldSlot.ADVERSE_FREQ_1_TO_5
            FrequencyBand.UNDER_1_PERCENT -> DrugFieldSlot.ADVERSE_FREQ_UNDER_1
            FrequencyBand.UNKNOWN -> DrugFieldSlot.ADVERSE_FREQ_UNKNOWN
        }
}
