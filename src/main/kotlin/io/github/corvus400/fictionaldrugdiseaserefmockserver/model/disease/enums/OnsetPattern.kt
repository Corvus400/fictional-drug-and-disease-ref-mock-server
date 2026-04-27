package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OnsetPattern {
    /** 急性発症 */
    @SerialName("acute")
    ACUTE,

    /** 亜急性発症 */
    @SerialName("subacute")
    SUBACUTE,

    /** 慢性経過 */
    @SerialName("chronic")
    CHRONIC,

    @SerialName("間欠性")
    INTERMITTENT,

    @SerialName("再発性")
    RELAPSING,
}
