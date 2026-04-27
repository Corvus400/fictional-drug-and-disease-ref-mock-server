package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Icd10Chapter {
    /** 感染症および寄生虫症 */
    @SerialName("chapter_i")
    CHAPTER_I,

    /** 新生物 */
    @SerialName("chapter_ii")
    CHAPTER_II,

    /** 血液および造血器の疾患ならびに免疫機構の障害 */
    @SerialName("chapter_iii")
    CHAPTER_III,

    @SerialName("内分泌、栄養および代謝疾患")
    CHAPTER_IV,

    @SerialName("精神および行動の障害")
    CHAPTER_V,

    @SerialName("神経系の疾患")
    CHAPTER_VI,

    @SerialName("眼および付属器の疾患")
    CHAPTER_VII,

    @SerialName("耳および乳様突起の疾患")
    CHAPTER_VIII,

    @SerialName("循環器系の疾患")
    CHAPTER_IX,

    @SerialName("呼吸器系の疾患")
    CHAPTER_X,

    @SerialName("消化器系の疾患")
    CHAPTER_XI,

    @SerialName("皮膚および皮下組織の疾患")
    CHAPTER_XII,

    @SerialName("筋骨格系および結合組織の疾患")
    CHAPTER_XIII,

    @SerialName("腎尿路生殖器系の疾患")
    CHAPTER_XIV,

    @SerialName("妊娠、分娩および産褥")
    CHAPTER_XV,

    @SerialName("周産期に発生した病態")
    CHAPTER_XVI,

    @SerialName("先天奇形、変形および染色体異常")
    CHAPTER_XVII,

    @SerialName("症状、徴候および異常臨床所見・異常検査所見で他に分類されないもの")
    CHAPTER_XVIII,

    @SerialName("損傷、中毒およびその他の外因の影響")
    CHAPTER_XIX,

    @SerialName("傷病および死亡の外因")
    CHAPTER_XX,

    @SerialName("健康状態に影響を及ぼす要因および保健サービスの利用")
    CHAPTER_XXI,

    @SerialName("特殊目的用コード")
    CHAPTER_XXII,
    ;

    /**
     * `/diseases?icd10_chapter=<key>` クエリフィルタで用いるローマ数字キー (`I`, `II`, ..., `XXII`)。
     * 列挙子名 (`CHAPTER_XXX`) から接頭辞を落とすだけなので、新しい章を追加したときに同期漏れが起きない。
     */
    val chapterKey: String
        get() = name.removePrefix(prefix = "CHAPTER_")

    companion object {
        /**
         * `chapterKey` (ローマ数字) から列挙子を逆引きする。未定義キーは `null`。
         */
        fun fromChapterKey(key: String): Icd10Chapter? = entries.firstOrNull { it.chapterKey == key }
    }
}
