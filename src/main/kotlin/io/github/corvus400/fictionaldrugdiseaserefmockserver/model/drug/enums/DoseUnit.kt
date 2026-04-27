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

    /** ミリ当量 */
    @SerialName("meq")
    MEQ,

    /** モル */
    @SerialName("mol")
    MOL,

    /** ミリモル */
    @SerialName("mmol")
    MMOL,

    @SerialName("%")
    PERCENT,
}
