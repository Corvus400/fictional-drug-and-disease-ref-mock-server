package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object EtiologyVocabulary {
    val keys: Set<String> =
        setOf("etiologyCategory", "organSystem", "mainFeature", "chronicity", "onsetPattern", "ageGroup")

    fun resolveOrNull(
        key: String,
        chapter: Icd10Chapter,
        seed: Long,
    ): String? {
        val candidates = entriesFor(key = key, chapter = chapter) ?: return null
        return ValueRangeGenerator.pickOne(seed = seed, candidates = candidates)
    }

    fun entriesFor(
        key: String,
        chapter: Icd10Chapter,
    ): List<String>? =
        when (key) {
            "etiologyCategory" -> byChapter(chapter = chapter).etiologyCategory
            "organSystem" -> byChapter(chapter = chapter).organSystem
            "mainFeature" -> byChapter(chapter = chapter).mainFeature
            "chronicity" -> byChapter(chapter = chapter).chronicity
            "onsetPattern" -> byChapter(chapter = chapter).onsetPattern
            "ageGroup" -> byChapter(chapter = chapter).ageGroup
            else -> null
        }

    private fun byChapter(chapter: Icd10Chapter): Terms =
        BY_CHAPTER[chapter] ?: error("Unknown ICD10 chapter '${chapter.chapterKey}'")

    private data class Terms(
        val etiologyCategory: List<String>,
        val organSystem: List<String>,
        val mainFeature: List<String>,
        val chronicity: List<String>,
        val onsetPattern: List<String>,
        val ageGroup: List<String>,
    )

    private fun terms(
        etiologyCategory: List<String>,
        organSystem: List<String>,
        mainFeature: List<String>,
        chronicity: List<String> = listOf("慢性", "急性", "亜急性"),
        onsetPattern: List<String> = listOf("緩徐発症", "急性発症", "潜行性発症"),
        ageGroup: List<String> = listOf("成人", "中高年", "高齢者"),
    ): Terms =
        Terms(
            etiologyCategory = etiologyCategory,
            organSystem = organSystem,
            mainFeature = mainFeature,
            chronicity = chronicity,
            onsetPattern = onsetPattern,
            ageGroup = ageGroup,
        )

    private val BY_CHAPTER: Map<Icd10Chapter, Terms> =
        mapOf(
            Icd10Chapter.CHAPTER_I to terms(
                etiologyCategory = listOf("感染性疾患", "病原体関連疾患", "伝播性疾患"),
                organSystem = listOf("全身感染領域", "免疫応答系", "感染防御系"),
                mainFeature = listOf("病原体曝露後の炎症反応", "伝播経路に関連する症状", "免疫応答の急性変化"),
                chronicity = listOf("急性", "亜急性", "再燃性"),
                onsetPattern = listOf("急性発症", "曝露後発症", "集団発生型発症"),
                ageGroup = listOf("小児", "成人", "高齢者"),
            ),
            Icd10Chapter.CHAPTER_II to terms(
                etiologyCategory = listOf("腫瘍性疾患", "増殖性疾患", "悪性新生物関連疾患"),
                organSystem = listOf("造血器系", "固形臓器系", "リンパ系"),
                mainFeature = listOf("異常細胞増殖", "腫瘤形成", "組織浸潤傾向"),
            ),
            Icd10Chapter.CHAPTER_III to terms(
                etiologyCategory = listOf("血液免疫疾患", "造血機能疾患", "凝固異常疾患"),
                organSystem = listOf("血液系", "骨髄系", "免疫系"),
                mainFeature = listOf("造血機能の変動", "凝固能の異常", "免疫細胞機能の変化"),
            ),
            Icd10Chapter.CHAPTER_IV to terms(
                etiologyCategory = listOf("内分泌代謝疾患", "栄養代謝疾患", "ホルモン調節疾患"),
                organSystem = listOf("内分泌系", "代謝調節系", "栄養代謝系"),
                mainFeature = listOf("ホルモン調節異常", "代謝恒常性の破綻", "栄養利用の変化"),
            ),
            Icd10Chapter.CHAPTER_V to terms(
                etiologyCategory = listOf("精神行動疾患", "神経発達関連疾患", "心理社会的疾患"),
                organSystem = listOf("精神機能系", "行動調節系", "認知情動系"),
                mainFeature = listOf("情動調節の変化", "認知行動機能の障害", "社会機能の低下"),
            ),
            Icd10Chapter.CHAPTER_VI to terms(
                etiologyCategory = listOf("神経疾患", "中枢神経疾患", "末梢神経疾患"),
                organSystem = listOf("神経系", "中枢神経系", "末梢神経系"),
                mainFeature = listOf("神経伝達の障害", "運動感覚機能の変化", "神経変性の進行"),
            ),
            Icd10Chapter.CHAPTER_VII to terms(
                etiologyCategory = listOf("眼科疾患", "視覚器疾患", "眼付属器疾患"),
                organSystem = listOf("眼球系", "視覚系", "眼付属器系"),
                mainFeature = listOf("視機能の低下", "眼圧や網膜機能の変化", "眼組織の炎症や変性"),
            ),
            Icd10Chapter.CHAPTER_VIII to terms(
                etiologyCategory = listOf("耳鼻平衡疾患", "聴覚器疾患", "前庭機能疾患"),
                organSystem = listOf("聴覚系", "前庭系", "耳鼻咽喉系"),
                mainFeature = listOf("聴覚機能の低下", "平衡機能の変化", "内耳機能の障害"),
            ),
            Icd10Chapter.CHAPTER_IX to terms(
                etiologyCategory = listOf("循環器疾患", "心血管疾患", "血管調節疾患"),
                organSystem = listOf("循環器系", "心血管系", "末梢血管系"),
                mainFeature = listOf("血行動態の変化", "心機能の低下", "血管抵抗の上昇"),
            ),
            Icd10Chapter.CHAPTER_X to terms(
                etiologyCategory = listOf("呼吸器疾患", "気道疾患", "肺実質疾患"),
                organSystem = listOf("呼吸器系", "気道系", "肺胞換気系"),
                mainFeature = listOf("換気機能の低下", "気道炎症の持続", "ガス交換能の変化"),
            ),
            Icd10Chapter.CHAPTER_XI to terms(
                etiologyCategory = listOf("消化器疾患", "肝胆膵疾患", "消化管機能疾患"),
                organSystem = listOf("消化器系", "肝胆膵系", "消化管系"),
                mainFeature = listOf("消化吸収機能の変化", "消化管運動の異常", "肝胆膵機能の障害"),
            ),
            Icd10Chapter.CHAPTER_XII to terms(
                etiologyCategory = listOf("皮膚疾患", "皮膚付属器疾患", "表皮バリア疾患"),
                organSystem = listOf("皮膚系", "表皮真皮系", "皮膚付属器系"),
                mainFeature = listOf("皮膚バリア機能の低下", "表皮炎症の持続", "皮膚構造の変化"),
            ),
            Icd10Chapter.CHAPTER_XIII to terms(
                etiologyCategory = listOf("筋骨格疾患", "結合組織疾患", "関節機能疾患"),
                organSystem = listOf("筋骨格系", "関節系", "結合組織系"),
                mainFeature = listOf("運動器機能の低下", "関節構造の変化", "結合組織の炎症"),
            ),
            Icd10Chapter.CHAPTER_XIV to terms(
                etiologyCategory = listOf("腎尿路疾患", "泌尿器疾患", "腎機能疾患"),
                organSystem = listOf("腎尿路系", "泌尿器系", "腎実質系"),
                mainFeature = listOf("排泄機能の低下", "腎機能指標の変化", "尿路機能の障害"),
            ),
            Icd10Chapter.CHAPTER_XV to terms(
                etiologyCategory = listOf("妊娠関連疾患", "産科疾患", "周産期母体疾患"),
                organSystem = listOf("妊娠産科系", "胎盤循環系", "母体周産期系"),
                mainFeature = listOf("母体循環の変化", "胎盤機能の変動", "妊娠経過の異常"),
                ageGroup = listOf("妊娠可能年齢", "妊婦", "産褥期女性"),
            ),
            Icd10Chapter.CHAPTER_XVI to terms(
                etiologyCategory = listOf("新生児疾患", "周産期疾患", "出生直後適応疾患"),
                organSystem = listOf("新生児全身系", "周産期適応系", "未熟臓器系"),
                mainFeature = listOf("出生後適応の遅れ", "未熟臓器機能の変化", "周産期ストレス反応"),
                ageGroup = listOf("新生児", "早産児", "乳児早期"),
            ),
            Icd10Chapter.CHAPTER_XVII to terms(
                etiologyCategory = listOf("先天異常疾患", "染色体関連疾患", "発生異常疾患"),
                organSystem = listOf("発生形成系", "先天構造系", "多臓器形成系"),
                mainFeature = listOf("形態形成の異常", "発達過程の偏り", "先天的構造変化"),
                ageGroup = listOf("新生児", "乳幼児", "小児"),
            ),
            Icd10Chapter.CHAPTER_XVIII to terms(
                etiologyCategory = listOf("症状所見分類疾患", "未確定病態", "臨床所見関連疾患"),
                organSystem = listOf("全身評価系", "臨床所見系", "症候評価系"),
                mainFeature = listOf("原因未確定の症候", "複数所見の併存", "診断保留の臨床像"),
            ),
            Icd10Chapter.CHAPTER_XIX to terms(
                etiologyCategory = listOf("外傷疾患", "損傷関連疾患", "中毒損傷疾患"),
                organSystem = listOf("損傷組織系", "外傷評価系", "急性侵襲系"),
                mainFeature = listOf("組織損傷の発生", "急性侵襲反応", "機能障害の急性出現"),
                onsetPattern = listOf("急性発症", "受傷後発症", "突発性発症"),
            ),
            Icd10Chapter.CHAPTER_XX to terms(
                etiologyCategory = listOf("外因関連状態", "環境要因関連状態", "事故外因関連状態"),
                organSystem = listOf("外因評価系", "環境曝露評価系", "安全リスク評価系"),
                mainFeature = listOf("外的要因による健康影響", "環境要因との関連", "事故リスクとの関連"),
                onsetPattern = listOf("外因曝露後発症", "事故後発症", "急性発症"),
            ),
            Icd10Chapter.CHAPTER_XXI to terms(
                etiologyCategory = listOf("保健サービス関連状態", "予防管理関連状態", "健康相談関連状態"),
                organSystem = listOf("保健管理系", "予防医療系", "健康支援系"),
                mainFeature = listOf("医療接点の必要性", "予防管理の不足", "継続支援の必要性"),
            ),
            Icd10Chapter.CHAPTER_XXII to terms(
                etiologyCategory = listOf("特殊目的分類状態", "暫定分類疾患", "追加分類関連状態"),
                organSystem = listOf("分類評価系", "暫定診断系", "特殊目的評価系"),
                mainFeature = listOf("分類上の暫定性", "追加評価の必要性", "通常分類外の臨床像"),
            ),
        )
}
