package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import kotlinx.serialization.Serializable

@Serializable
data class DiseaseListResponse(
    val items: List<DiseaseSummary>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalCount: Int,
)
