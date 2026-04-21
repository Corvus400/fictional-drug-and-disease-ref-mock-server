package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RenalSeverity {
    @SerialName("正常")
    NORMAL,

    @SerialName("軽度低下")
    MILD,

    @SerialName("中等度低下")
    MODERATE,

    @SerialName("重度低下")
    SEVERE,

    @SerialName("末期")
    END_STAGE,
}
