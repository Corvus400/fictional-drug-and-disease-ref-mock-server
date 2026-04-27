package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class RenalSeverity {
    /** 正常 */
    @SerialName("normal")
    NORMAL,

    /** 軽度低下 */
    @SerialName("mild")
    MILD,

    /** 中等度低下 */
    @SerialName("moderate")
    MODERATE,

    /** 重度低下 */
    @SerialName("severe")
    SEVERE,

    /** 末期 */
    @SerialName("end_stage")
    END_STAGE,
    ;

    /**
     * 腎機能障害重症度区分の英語 snake_case 表記 (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しい区分を追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)
}
