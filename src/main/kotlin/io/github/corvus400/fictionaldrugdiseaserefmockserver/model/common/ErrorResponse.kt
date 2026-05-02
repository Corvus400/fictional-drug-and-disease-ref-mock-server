package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.Disclaimer
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
    val details: String? = null,
    val disclaimer: String = Disclaimer.SHORT,
)
