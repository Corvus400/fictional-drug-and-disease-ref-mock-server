package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class DoseUnit {
    /** ミリグラム */
    @SerialName("mg")
    MG,

    /** グラム */
    @SerialName("g")
    G,

    /** マイクログラム */
    @SerialName("microgram")
    MICROGRAM,

    /** ミリリットル */
    @SerialName("ml")
    ML,

    /** リットル */
    @SerialName("l")
    L,

    /** 国際単位 */
    @SerialName("iu")
    IU,

    /** ミリ当量 */
    @SerialName("meq")
    MEQ,

    /** モル */
    @SerialName("mol")
    MOL,

    /** ミリモル */
    @SerialName("mmol")
    MMOL,

    /** パーセント */
    @SerialName("percent")
    PERCENT,
    ;

    /**
     * `dose.unit` ネストフィールドで用いる英語 snake_case 表記 (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しい単位を追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)
}
