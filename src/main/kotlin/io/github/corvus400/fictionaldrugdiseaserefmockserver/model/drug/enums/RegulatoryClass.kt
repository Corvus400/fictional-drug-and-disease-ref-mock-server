package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RegulatoryClass {
    /** 毒薬 */
    @SerialName("poison")
    POISON,

    /** 劇薬 */
    @SerialName("potent")
    POTENT,

    /** 普通薬 */
    @SerialName("ordinary")
    ORDINARY,

    /** 向精神薬第1種 */
    @SerialName("psychotropic_1")
    PSYCHOTROPIC_1,

    /** 向精神薬第2種 */
    @SerialName("psychotropic_2")
    PSYCHOTROPIC_2,

    /** 向精神薬第3種 */
    @SerialName("psychotropic_3")
    PSYCHOTROPIC_3,

    /** 麻薬 */
    @SerialName("narcotic")
    NARCOTIC,

    @SerialName("覚醒剤原料")
    STIMULANT_PRECURSOR,

    @SerialName("生物由来製品")
    BIOLOGICAL,

    @SerialName("特定生物由来製品")
    SPECIFIED_BIOLOGICAL,

    @SerialName("処方箋医薬品")
    PRESCRIPTION_REQUIRED,
}
