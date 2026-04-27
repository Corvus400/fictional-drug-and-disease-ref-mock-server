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

    /** 内分泌、栄養および代謝疾患 */
    @SerialName("chapter_iv")
    CHAPTER_IV,

    /** 精神および行動の障害 */
    @SerialName("chapter_v")
    CHAPTER_V,

    /** 神経系の疾患 */
    @SerialName("chapter_vi")
    CHAPTER_VI,

    /** 眼および付属器の疾患 */
    @SerialName("chapter_vii")
    CHAPTER_VII,

    /** 耳および乳様突起の疾患 */
    @SerialName("chapter_viii")
    CHAPTER_VIII,

    /** 循環器系の疾患 */
    @SerialName("chapter_ix")
    CHAPTER_IX,

    /** 呼吸器系の疾患 */
    @SerialName("chapter_x")
    CHAPTER_X,

    /** 消化器系の疾患 */
    @SerialName("chapter_xi")
    CHAPTER_XI,

    /** 皮膚および皮下組織の疾患 */
    @SerialName("chapter_xii")
    CHAPTER_XII,

    /** 筋骨格系および結合組織の疾患 */
    @SerialName("chapter_xiii")
    CHAPTER_XIII,

    /** 腎尿路生殖器系の疾患 */
    @SerialName("chapter_xiv")
    CHAPTER_XIV,

    /** 妊娠、分娩および産褥 */
    @SerialName("chapter_xv")
    CHAPTER_XV,

    /** 周産期に発生した病態 */
    @SerialName("chapter_xvi")
    CHAPTER_XVI,

    /** 先天奇形、変形および染色体異常 */
    @SerialName("chapter_xvii")
    CHAPTER_XVII,

    /** 症状、徴候および異常臨床所見・異常検査所見で他に分類されないもの */
    @SerialName("chapter_xviii")
    CHAPTER_XVIII,

    /** 損傷、中毒およびその他の外因の影響 */
    @SerialName("chapter_xix")
    CHAPTER_XIX,

    /** 傷病および死亡の外因 */
    @SerialName("chapter_xx")
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
