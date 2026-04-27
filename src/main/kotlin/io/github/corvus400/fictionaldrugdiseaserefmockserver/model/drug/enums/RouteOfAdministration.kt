package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class RouteOfAdministration {
    /** 内服 */
    @SerialName("oral")
    ORAL,

    /** 外用 */
    @SerialName("topical")
    TOPICAL,

    /** 注射 */
    @SerialName("injection_route")
    INJECTION_ROUTE,

    /** 吸入 */
    @SerialName("inhalation")
    INHALATION,

    /** 坐剤 */
    @SerialName("rectal")
    RECTAL,

    /** 点眼 */
    @SerialName("ophthalmic")
    OPHTHALMIC,

    /** 点鼻 */
    @SerialName("nasal")
    NASAL,

    /** 貼付 */
    @SerialName("transdermal")
    TRANSDERMAL,
    ;

    /**
     * `/drugs?route=<value>` クエリフィルタで用いる英語 snake_case 表記 (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しい投与経路を追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)
}
