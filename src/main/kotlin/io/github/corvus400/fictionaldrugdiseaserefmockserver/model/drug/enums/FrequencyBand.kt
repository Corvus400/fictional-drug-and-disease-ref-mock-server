package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class FrequencyBand {
    /** 5% 以上 */
    @SerialName("over_5_percent")
    OVER_5_PERCENT,

    /** 1〜5% */
    @SerialName("between_1_and_5_percent")
    BETWEEN_1_AND_5_PERCENT,

    /** 1% 未満 */
    @SerialName("under_1_percent")
    UNDER_1_PERCENT,

    /** 頻度不明 */
    @SerialName("unknown")
    UNKNOWN,
    ;

    /**
     * 副作用頻度区分の英語 snake_case 表記 (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しい区分を追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)
}
