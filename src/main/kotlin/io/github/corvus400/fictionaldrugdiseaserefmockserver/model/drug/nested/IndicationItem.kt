package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import kotlinx.serialization.Serializable

@Serializable
data class IndicationItem(
    val order: Int,
    val content: String,
)
