package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class ExamCategory {
    /** 血液検査 */
    @SerialName("blood_test")
    BLOOD_TEST,

    /** 画像検査 */
    @SerialName("imaging")
    IMAGING,

    /** 生理検査 */
    @SerialName("physiological")
    PHYSIOLOGICAL,

    /** 病理検査 */
    @SerialName("pathology")
    PATHOLOGY,

    /** 問診 */
    @SerialName("interview")
    INTERVIEW,
    ;

    /**
     * JSON encoding 時に用いる英語 snake_case (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しい検査カテゴリを追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)
}
