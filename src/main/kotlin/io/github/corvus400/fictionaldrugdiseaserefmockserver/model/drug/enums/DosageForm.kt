package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class DosageForm {
    /** 錠剤 */
    @SerialName("tablet")
    TABLET,

    /** カプセル */
    @SerialName("capsule")
    CAPSULE,

    /** 散剤 */
    @SerialName("powder")
    POWDER,

    /** 顆粒 */
    @SerialName("granule")
    GRANULE,

    /** 液剤 */
    @SerialName("liquid")
    LIQUID,

    /** 注射剤 */
    @SerialName("injection_form")
    INJECTION_FORM,

    /** 軟膏 */
    @SerialName("ointment")
    OINTMENT,

    /** クリーム */
    @SerialName("cream")
    CREAM,

    /** 貼付剤 */
    @SerialName("patch")
    PATCH,

    /** 点眼液 */
    @SerialName("eye_drops")
    EYE_DROPS,

    /** 坐剤 */
    @SerialName("suppository")
    SUPPOSITORY,

    /** 吸入剤 */
    @SerialName("inhaler")
    INHALER,

    /** 点鼻液 */
    @SerialName("nasal_spray")
    NASAL_SPRAY,
    ;

    /**
     * `/drugs?dosage_form=<value>` クエリフィルタで用いる英語 snake_case 表記 (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しい剤形を追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)
}
