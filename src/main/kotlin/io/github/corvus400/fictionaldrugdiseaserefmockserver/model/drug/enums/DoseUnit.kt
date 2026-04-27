package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DoseUnit {
    /** ミリグラム */
    @SerialName("mg")
    MG,

    /** グラム */
    @SerialName("g")
    G,

    /** マイクログラム */
    @SerialName("microgram")
    MICROGRAM,

    /** ミリリットル */
    @SerialName("ml")
    ML,

    /** リットル */
    @SerialName("l")
    L,

    /** 国際単位 */
    @SerialName("iu")
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
