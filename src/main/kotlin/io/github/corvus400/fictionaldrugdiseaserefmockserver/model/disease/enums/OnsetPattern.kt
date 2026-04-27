package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OnsetPattern {
    /** 急性発症 */
    @SerialName("acute")
    ACUTE,

    @SerialName("亜急性発症")
    SUBACUTE,

    @SerialName("慢性経過")
    CHRONIC,

    @SerialName("間欠性")
    INTERMITTENT,

    @SerialName("再発性")
    RELAPSING,
}
