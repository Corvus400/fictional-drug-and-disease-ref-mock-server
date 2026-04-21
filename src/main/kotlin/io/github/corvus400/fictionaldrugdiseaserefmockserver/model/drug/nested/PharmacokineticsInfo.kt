package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested

import kotlinx.serialization.Serializable

@Serializable
data class PharmacokineticsInfo(
    val bloodConcentration: String? = null,
    val absorption: String? = null,
    val distribution: String? = null,
    val metabolism: String? = null,
    val excretion: String? = null,
    val parameters: List<PkParameter> = emptyList(),
)

@Serializable
data class PkParameter(
    val name: String,
    val value: String,
)
