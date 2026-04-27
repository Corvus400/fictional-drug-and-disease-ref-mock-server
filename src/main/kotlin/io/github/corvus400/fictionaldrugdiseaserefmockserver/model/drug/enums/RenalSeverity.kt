package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RenalSeverity {
    /** 正常 */
    @SerialName("normal")
    NORMAL,

    /** 軽度低下 */
    @SerialName("mild")
    MILD,

    /** 中等度低下 */
    @SerialName("moderate")
    MODERATE,

    /** 重度低下 */
    @SerialName("severe")
    SEVERE,

    @SerialName("末期")
    END_STAGE,
}
