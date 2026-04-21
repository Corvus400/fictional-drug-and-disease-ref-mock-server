package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested

import kotlinx.serialization.Serializable

@Serializable
data class TreatmentInfo(
    val pharmacological: List<PharmaTreatment> = emptyList(),
    val nonPharmacological: List<TreatmentSection> = emptyList(),
    val acutePhaseProtocol: List<ProtocolStep> = emptyList(),
)

@Serializable
data class PharmaTreatment(
    val drugCategory: String,
    val drugIds: List<String> = emptyList(),
    val indication: String,
    val notes: String,
)

@Serializable
data class TreatmentSection(
    val heading: String,
    val items: List<String> = emptyList(),
    val description: String? = null,
)

@Serializable
data class ProtocolStep(
    val order: Int,
    val action: String,
    val target: String? = null,
)
