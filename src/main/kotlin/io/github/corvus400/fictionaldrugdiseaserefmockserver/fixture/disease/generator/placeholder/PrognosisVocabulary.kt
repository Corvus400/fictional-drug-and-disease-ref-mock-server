package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object PrognosisVocabulary {
    val keys: Set<String> =
        setOf(
            "prognosticFactor",
            "progressedComplication",
            "prognosisIndicator",
            "favorableFactor",
            "favorableOutcome",
            "followUpExam",
        )

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
    ): List<String>? {
        val profile = profileFor(chapter = chapter)
        return when (key) {
            "prognosticFactor" -> listOf("${profile.domain}の初期重症度", profile.keyFactor, "治療開始までの期間")
            "progressedComplication" -> listOf(profile.complication, "${profile.organ}機能不全", "生活機能低下")
            "prognosisIndicator" -> listOf(profile.indicator, "${profile.domain}再燃率", "機能維持率")
            "favorableFactor" -> listOf(profile.favorableFactor, "早期診断", "治療継続性が良好")
            "favorableOutcome" -> listOf(profile.favorableOutcome, "日常生活の維持", "再燃リスクの低下")
            "followUpExam" -> listOf(profile.followUpExam, "${profile.domain}定期評価", "合併症スクリーニング")
            else -> null
        }
    }

    private fun profileFor(chapter: Icd10Chapter): Profile =
        PROFILES[chapter] ?: error("Unknown ICD10 chapter '${chapter.chapterKey}'")

    private data class Profile(
        val domain: String,
        val organ: String,
        val keyFactor: String,
        val complication: String,
        val indicator: String,
        val favorableFactor: String,
        val favorableOutcome: String,
        val followUpExam: String,
    )

    private val PROFILES: Map<Icd10Chapter, Profile> =
        mapOf(
            Icd10Chapter.CHAPTER_I to
                Profile("感染症", "免疫応答", "病原体量の早期低下", "全身炎症の遷延", "解熱維持率", "早期の適正治療", "感染制御", "炎症反応検査"),
            Icd10Chapter.CHAPTER_II to
                Profile("腫瘍性疾患", "腫瘍組織", "病期", "臓器浸潤", "寛解維持率", "限局病変", "長期寛解", "画像検査"),
            Icd10Chapter.CHAPTER_III to
                Profile("血液免疫疾患", "血液", "血球数の安定性", "出血または血栓", "血液指標安定率", "造血能の回復", "出血予防", "血算検査"),
            Icd10Chapter.CHAPTER_IV to
                Profile("内分泌代謝疾患", "代謝調節", "代謝指標の改善", "代謝性合併症", "代謝コントロール率", "生活習慣介入への反応", "代謝安定", "代謝指標検査"),
            Icd10Chapter.CHAPTER_V to
                Profile("精神行動疾患", "認知情動", "社会機能の保全", "社会機能低下", "症状安定率", "支援体制の確保", "社会復帰", "心理評価尺度"),
            Icd10Chapter.CHAPTER_VI to
                Profile("神経疾患", "神経", "神経脱落症状の程度", "運動機能低下", "神経機能保持率", "リハビリ継続", "機能温存", "神経画像検査"),
            Icd10Chapter.CHAPTER_VII to
                Profile("眼科疾患", "眼", "視機能保持", "視力低下", "視機能保持率", "早期眼科介入", "視機能温存", "眼底検査"),
            Icd10Chapter.CHAPTER_VIII to
                Profile("耳鼻平衡疾患", "内耳", "聴平衡機能の残存", "転倒または聴力低下", "聴平衡機能保持率", "早期リハビリ", "転倒予防", "聴力検査"),
            Icd10Chapter.CHAPTER_IX to
                Profile("循環器疾患", "心血管", "血行動態の安定性", "心不全増悪", "心血管イベント回避率", "危険因子管理", "心機能維持", "心エコー検査"),
            Icd10Chapter.CHAPTER_X to
                Profile("呼吸器疾患", "呼吸器", "酸素化の安定性", "呼吸不全", "呼吸機能保持率", "禁煙と環境調整", "呼吸機能維持", "呼吸機能検査"),
            Icd10Chapter.CHAPTER_XI to
                Profile("消化器疾患", "消化器", "栄養状態の維持", "消化管出血", "消化器症状寛解率", "栄養管理", "摂食機能維持", "内視鏡検査"),
            Icd10Chapter.CHAPTER_XII to
                Profile("皮膚疾患", "皮膚", "皮膚バリア回復", "広範皮膚病変", "皮膚症状寛解率", "スキンケア継続", "皮膚機能維持", "皮膚観察"),
            Icd10Chapter.CHAPTER_XIII to
                Profile("筋骨格疾患", "運動器", "可動域の保持", "関節変形", "運動機能保持率", "運動器リハビリ", "移動能力維持", "関節画像検査"),
            Icd10Chapter.CHAPTER_XIV to
                Profile("腎尿路疾患", "腎尿路", "腎機能の保全", "腎機能悪化", "腎機能保持率", "血圧水分管理", "排泄機能維持", "腎機能検査"),
            Icd10Chapter.CHAPTER_XV to
                Profile("妊娠関連疾患", "母体胎盤", "母体胎児状態の安定", "胎盤機能不全", "妊娠継続安定率", "健診継続", "母体胎児安定", "産科超音波検査"),
            Icd10Chapter.CHAPTER_XVI to
                Profile("新生児疾患", "新生児", "出生後適応の進行", "発育不全", "発育安定率", "早期栄養支援", "発育促進", "新生児健診"),
            Icd10Chapter.CHAPTER_XVII to
                Profile("先天異常疾患", "発生形成", "合併症の少なさ", "発達遅延", "発達支援継続率", "早期発達支援", "生活機能獲得", "発達評価"),
            Icd10Chapter.CHAPTER_XVIII to
                Profile("症状所見分類疾患", "全身所見", "原因同定の進展", "診断遅延", "症状安定率", "継続観察", "診断確定", "追加スクリーニング検査"),
            Icd10Chapter.CHAPTER_XIX to
                Profile("外傷損傷疾患", "損傷組織", "初期処置までの時間", "後遺機能障害", "機能回復率", "早期外傷対応", "損傷部位の機能回復", "損傷部位画像検査"),
            Icd10Chapter.CHAPTER_XX to
                Profile("外因関連状態", "外因評価", "再曝露回避", "再曝露による悪化", "再曝露回避率", "安全環境の確保", "健康影響の軽減", "曝露状況評価"),
            Icd10Chapter.CHAPTER_XXI to
                Profile("保健サービス関連状態", "健康管理", "支援資源への接続", "受診中断", "支援継続率", "継続相談", "健康行動の定着", "健康評価面接"),
            Icd10Chapter.CHAPTER_XXII to
                Profile("特殊目的分類状態", "分類評価", "分類情報の更新", "分類遅延", "分類更新完了率", "追加情報の充足", "適切な分類更新", "分類確認検査"),
        )
}
