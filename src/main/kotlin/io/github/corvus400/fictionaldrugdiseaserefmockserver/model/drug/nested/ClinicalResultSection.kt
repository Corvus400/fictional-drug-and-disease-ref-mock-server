package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import kotlinx.serialization.Serializable

@Serializable
data class ClinicalResultSection(
    val heading: String,
    val content: String,
)
