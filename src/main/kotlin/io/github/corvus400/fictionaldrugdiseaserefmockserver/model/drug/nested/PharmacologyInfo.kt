package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import kotlinx.serialization.Serializable

@Serializable
data class PharmacologyInfo(
    val mechanism: String,
    val effect: String,
)
