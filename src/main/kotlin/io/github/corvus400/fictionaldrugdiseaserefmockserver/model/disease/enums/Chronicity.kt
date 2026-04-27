package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class Chronicity {
    /** 急性 */
    @SerialName("acute")
    ACUTE,

    /** 亜急性 */
    @SerialName("subacute")
    SUBACUTE,

    /** 慢性 */
    @SerialName("chronic")
    CHRONIC,

    /** 再発性 */
    @SerialName("relapsing")
    RELAPSING,
    ;

    /**
     * `/diseases?chronicity=<value>` クエリフィルタで用いる英語 snake_case (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しい慢性度を追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)
}
