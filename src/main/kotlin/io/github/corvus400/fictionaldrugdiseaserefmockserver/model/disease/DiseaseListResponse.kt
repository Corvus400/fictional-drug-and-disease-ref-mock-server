package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import kotlinx.serialization.Serializable

@Serializable
data class DiseaseListResponse(
    val items: List<DiseaseSummary>,
)
