package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DosageForm {
    /** 錠剤 */
    @SerialName("tablet")
    TABLET,

    @SerialName("カプセル")
    CAPSULE,

    @SerialName("散剤")
    POWDER,

    @SerialName("顆粒")
    GRANULE,

    @SerialName("液剤")
    LIQUID,

    @SerialName("注射剤")
    INJECTION_FORM,

    @SerialName("軟膏")
    OINTMENT,

    @SerialName("クリーム")
    CREAM,

    @SerialName("貼付剤")
    PATCH,

    @SerialName("点眼液")
    EYE_DROPS,

    @SerialName("坐剤")
    SUPPOSITORY,

    @SerialName("吸入剤")
    INHALER,

    @SerialName("点鼻液")
    NASAL_SPRAY,
}
