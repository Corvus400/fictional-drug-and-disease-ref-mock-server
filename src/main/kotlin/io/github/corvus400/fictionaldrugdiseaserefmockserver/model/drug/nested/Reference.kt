package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import kotlinx.serialization.Serializable

@Serializable
data class Reference(
    val citation: String,
    val source: String? = null,
)
