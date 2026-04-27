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

    @SerialName("向精神薬第1種")
    PSYCHOTROPIC_1,

    @SerialName("向精神薬第2種")
    PSYCHOTROPIC_2,

    @SerialName("向精神薬第3種")
    PSYCHOTROPIC_3,

    @SerialName("麻薬")
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
