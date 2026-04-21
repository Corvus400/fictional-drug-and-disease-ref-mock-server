package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import kotlinx.serialization.Serializable

@Serializable
data class NumberedParagraph(
    val order: Int,
    val subOrder: Int? = null,
    val content: String,
)
