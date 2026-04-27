package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HepaticSeverity {
    /** 軽度 */
    @SerialName("mild")
    MILD,

    @SerialName("中等度")
    MODERATE,

    @SerialName("重度")
    SEVERE,
}
