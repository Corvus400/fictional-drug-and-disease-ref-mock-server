package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Chronicity {
    @SerialName("急性")
    ACUTE,

    @SerialName("亜急性")
    SUBACUTE,

    @SerialName("慢性")
    CHRONIC,

    @SerialName("再発性")
    RELAPSING,
}
