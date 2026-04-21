package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DoseUnit {
    @SerialName("mg")
    MG,

    @SerialName("g")
    G,

    @SerialName("μg")
    MICROGRAM,

    @SerialName("mL")
    ML,

    @SerialName("L")
    L,

    @SerialName("IU")
    IU,

    @SerialName("mEq")
    MEQ,

    @SerialName("mol")
    MOL,

    @SerialName("mmol")
    MMOL,

    @SerialName("%")
    PERCENT,
}
