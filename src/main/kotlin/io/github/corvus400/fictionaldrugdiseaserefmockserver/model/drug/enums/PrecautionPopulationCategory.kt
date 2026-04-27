package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class PrecautionPopulationCategory {
    /** 合併症 */
    @SerialName("comorbidity")
    COMORBIDITY,

    /** 腎機能障害 */
    @SerialName("renal_impairment")
    RENAL_IMPAIRMENT,

    /** 肝機能障害 */
    @SerialName("hepatic_impairment")
    HEPATIC_IMPAIRMENT,

    /** 生殖能有する患者 */
    @SerialName("reproductive_potential")
    REPRODUCTIVE_POTENTIAL,

    /** 妊婦 */
    @SerialName("pregnant")
    PREGNANT,

    /** 授乳婦 */
    @SerialName("lactating")
    LACTATING,

    /** 小児等 */
    @SerialName("pediatric")
    PEDIATRIC,

    /** 高齢者 */
    @SerialName("geriatric")
    GERIATRIC,
    ;

    /**
     * 「特定の背景を有する患者に関する注意」カテゴリの英語 snake_case 表記 (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しいカテゴリを追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)
}
