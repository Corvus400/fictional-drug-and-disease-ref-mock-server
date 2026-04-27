package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RouteOfAdministration {
    /** 内服 */
    @SerialName("oral")
    ORAL,

    /** 外用 */
    @SerialName("topical")
    TOPICAL,

    /** 注射 */
    @SerialName("injection_route")
    INJECTION_ROUTE,

    /** 吸入 */
    @SerialName("inhalation")
    INHALATION,

    /** 坐剤 */
    @SerialName("rectal")
    RECTAL,

    @SerialName("点眼")
    OPHTHALMIC,

    @SerialName("点鼻")
    NASAL,

    @SerialName("貼付")
    TRANSDERMAL,
}
