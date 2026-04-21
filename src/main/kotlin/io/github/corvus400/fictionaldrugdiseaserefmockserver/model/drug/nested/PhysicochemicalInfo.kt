package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import kotlinx.serialization.Serializable

@Serializable
data class PhysicochemicalInfo(
    val genericNameEnglish: String,
    val molecularFormula: String,
    val molecularWeight: Double? = null,
    val description: String,
)
