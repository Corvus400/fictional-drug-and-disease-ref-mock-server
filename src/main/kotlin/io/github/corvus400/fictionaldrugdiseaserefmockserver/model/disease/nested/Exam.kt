package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    val name: String,
    val category: ExamCategory,
    val typicalFinding: String,
    val referenceRange: String? = null,
)
