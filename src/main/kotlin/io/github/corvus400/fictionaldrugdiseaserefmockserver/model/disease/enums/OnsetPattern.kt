package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * 発症パターン区分: `SymptomInfo.onsetPattern` で用いる 5 値の enum。
 *
 * 急性 / 亜急性 / 慢性 / 間欠性 / 再発性 を区別。`@SerialName` は英語 snake_case で
 * クライアント側 DTO とシリアライズ互換。
 */
@Serializable
enum class OnsetPattern {
    /** 急性発症 */
    @SerialName("acute")
    ACUTE,

    /** 亜急性発症 */
    @SerialName("subacute")
    SUBACUTE,

    /** 慢性経過 */
    @SerialName("chronic")
    CHRONIC,

    /** 間欠性 */
    @SerialName("intermittent")
    INTERMITTENT,

    /** 再発性 */
    @SerialName("relapsing")
    RELAPSING,
    ;

    /**
     * JSON encoding 時に用いる英語 snake_case (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しい発症パターンを追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)
}
