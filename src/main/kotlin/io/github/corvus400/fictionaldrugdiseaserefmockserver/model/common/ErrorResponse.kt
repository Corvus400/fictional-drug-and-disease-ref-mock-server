package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
    val details: String? = null,
)
