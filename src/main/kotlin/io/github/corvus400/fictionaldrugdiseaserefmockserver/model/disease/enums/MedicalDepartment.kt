package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
enum class MedicalDepartment {
    /** 内科 */
    @SerialName("internal_medicine")
    INTERNAL_MEDICINE,

    @SerialName("循環器内科")
    CARDIOLOGY,

    @SerialName("消化器内科")
    GASTROENTEROLOGY,

    @SerialName("内分泌代謝科")
    ENDOCRINOLOGY,

    @SerialName("神経内科")
    NEUROLOGY,

    @SerialName("精神科")
    PSYCHIATRY,

    @SerialName("外科")
    SURGERY,

    @SerialName("整形外科")
    ORTHOPEDICS,

    @SerialName("皮膚科")
    DERMATOLOGY,

    @SerialName("眼科")
    OPHTHALMOLOGY,

    @SerialName("耳鼻咽喉科")
    OTOLARYNGOLOGY,

    @SerialName("泌尿器科")
    UROLOGY,

    @SerialName("婦人科")
    GYNECOLOGY,

    @SerialName("小児科")
    PEDIATRICS,

    @SerialName("救急科")
    EMERGENCY,

    @SerialName("感染症科")
    INFECTIOUS_DISEASE,
    ;

    /**
     * `/diseases?department=<value>` クエリフィルタで用いる日本語表記 (`@SerialName` 値)。
     * 列挙子の宣言順序が [descriptor] の要素順と一致するため、新しい診療科を追加しても同期漏れが起きない。
     */
    val serialName: String
        get() = serializer().descriptor.getElementName(index = ordinal)

    companion object {
        /**
         * 日本語表記 (`@SerialName` 値) から列挙子を逆引きする。未定義キーは `null`。
         */
        fun fromSerialName(key: String): MedicalDepartment? = entries.firstOrNull { it.serialName == key }
    }
}
