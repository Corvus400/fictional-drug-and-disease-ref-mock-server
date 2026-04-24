package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import kotlinx.serialization.Serializable

@Serializable
data class DiseaseListResponse(
    val items: List<DiseaseSummary>,
    val page: Int? = null,
    val pageSize: Int? = null,
    val totalPages: Int? = null,
    val totalCount: Int? = null,
)
