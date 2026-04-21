package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import kotlinx.serialization.Serializable

@Serializable
data class SymptomInfo(
    val mainSymptoms: List<String>,
    val associatedSymptoms: List<String> = emptyList(),
    val onsetPattern: OnsetPattern? = null,
)
