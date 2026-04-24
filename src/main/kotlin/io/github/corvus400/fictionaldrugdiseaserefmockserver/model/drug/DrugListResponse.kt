package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import kotlinx.serialization.Serializable

@Serializable
data class DrugListResponse(
    val items: List<DrugSummary>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalCount: Int,
)
