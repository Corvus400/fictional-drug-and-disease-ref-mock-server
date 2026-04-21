package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import kotlinx.serialization.Serializable

@Serializable
data class InteractionInfo(
    val combinationProhibited: List<InteractionEntry> = emptyList(),
    val combinationCaution: List<InteractionEntry> = emptyList(),
)

@Serializable
data class InteractionEntry(
    val drugId: String? = null,
    val displayName: String,
    val clinicalSymptom: String,
    val mechanism: String,
)
