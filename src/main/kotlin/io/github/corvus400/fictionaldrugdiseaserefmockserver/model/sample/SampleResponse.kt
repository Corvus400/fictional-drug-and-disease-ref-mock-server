package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.sample

import kotlinx.serialization.Serializable

@Serializable
data class SampleResponse(
    val id: String,
    val message: String,
)
