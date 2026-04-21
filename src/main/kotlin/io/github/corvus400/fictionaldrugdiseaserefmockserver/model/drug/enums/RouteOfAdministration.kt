package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RouteOfAdministration {
    @SerialName("内服")
    ORAL,

    @SerialName("外用")
    TOPICAL,

    @SerialName("注射")
    INJECTION_ROUTE,

    @SerialName("吸入")
    INHALATION,

    @SerialName("坐剤")
    RECTAL,

    @SerialName("点眼")
    OPHTHALMIC,

    @SerialName("点鼻")
    NASAL,

    @SerialName("貼付")
    TRANSDERMAL,
}
