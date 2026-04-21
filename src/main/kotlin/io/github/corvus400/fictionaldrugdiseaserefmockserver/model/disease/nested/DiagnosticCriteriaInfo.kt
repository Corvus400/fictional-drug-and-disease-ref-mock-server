package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested

import kotlinx.serialization.Serializable

@Serializable
data class DiagnosticCriteriaInfo(
    val required: List<String>,
    val supporting: List<String> = emptyList(),
    val notes: String? = null,
)
