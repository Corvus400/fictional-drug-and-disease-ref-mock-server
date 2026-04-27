package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FrequencyBand {
    /** 5% 以上 */
    @SerialName("over_5_percent")
    OVER_5_PERCENT,

    @SerialName("1-5%")
    BETWEEN_1_AND_5_PERCENT,

    @SerialName("1%未満")
    UNDER_1_PERCENT,

    @SerialName("頻度不明")
    UNKNOWN,
}
