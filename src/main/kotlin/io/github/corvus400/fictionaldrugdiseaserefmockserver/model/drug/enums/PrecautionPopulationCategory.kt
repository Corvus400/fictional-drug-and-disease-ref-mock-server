package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    @SerialName("生殖能有する患者")
    REPRODUCTIVE_POTENTIAL,

    @SerialName("妊婦")
    PREGNANT,

    @SerialName("授乳婦")
    LACTATING,

    @SerialName("小児等")
    PEDIATRIC,

    @SerialName("高齢者")
    GERIATRIC,
}
