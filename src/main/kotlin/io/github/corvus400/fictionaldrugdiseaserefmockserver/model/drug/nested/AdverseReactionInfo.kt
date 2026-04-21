package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand
import kotlinx.serialization.Serializable

@Serializable
data class AdverseReactionInfo(
    val serious: List<AdverseReaction> = emptyList(),
    val other: AdverseReactionByFrequency,
)

@Serializable
data class AdverseReaction(
    val name: String,
    val frequency: FrequencyBand,
    val symptom: String,
    val initialSigns: String,
    val countermeasure: String,
)

@Serializable
data class AdverseReactionByFrequency(
    val over5Percent: List<String> = emptyList(),
    val between1And5Percent: List<String> = emptyList(),
    val under1Percent: List<String> = emptyList(),
    val frequencyUnknown: List<String> = emptyList(),
)
