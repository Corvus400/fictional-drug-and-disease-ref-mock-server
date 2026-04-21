package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested

import kotlinx.serialization.Serializable

@Serializable
data class SeverityInfo(
    val gradingSystem: String,
    val grades: List<Grade>,
)

@Serializable
data class Grade(
    val label: String,
    val criteria: String,
    val recommendedAction: String,
)
