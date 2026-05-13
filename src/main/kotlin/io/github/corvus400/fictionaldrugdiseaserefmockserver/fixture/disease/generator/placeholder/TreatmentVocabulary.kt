package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object TreatmentVocabulary {
    val keys: Set<String> =
        setOf(
            "treatmentCategory",
            "acuteTreatment",
            "maintenanceTreatment",
            "secondLineTreatment",
            "adjunctTreatment",
            "specialistReferral",
            "escalatedAction",
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
            "treatmentCategory" -> listOf("${profile.domain}管理", "${profile.domain}標準治療", profile.primaryCare)
            "acuteTreatment" -> listOf(profile.acuteTreatment, "${profile.domain}急性期プロトコル", "状態安定化と初期評価")
            "maintenanceTreatment" -> listOf(profile.maintenanceTreatment, "${profile.domain}維持管理", "定期評価に基づく継続療法")
            "secondLineTreatment" -> listOf(profile.secondLineTreatment, "専門治療への切替え", "治療強度の段階的調整")
            "adjunctTreatment" -> listOf(profile.adjunctTreatment, "生活機能支援", "合併症予防指導")
            "specialistReferral" -> listOf(profile.specialistReferral, "地域基幹病院", "専門医療機関")
            "escalatedAction" -> listOf(profile.escalatedAction, "入院管理の検討", "専門チーム介入")
            else -> null
        }
    }

    private fun profileFor(chapter: Icd10Chapter): Profile =
        PROFILES[chapter] ?: error("Unknown ICD10 chapter '${chapter.chapterKey}'")

    private data class Profile(
        val domain: String,
        val primaryCare: String,
        val acuteTreatment: String,
        val maintenanceTreatment: String,
        val secondLineTreatment: String,
        val adjunctTreatment: String,
        val specialistReferral: String,
        val escalatedAction: String,
    )

    private val PROFILES: Map<Icd10Chapter, Profile> =
        mapOf(
            Icd10Chapter.CHAPTER_I to
                Profile("感染症", "抗微生物薬適正使用", "初期抗微生物薬投与", "再燃予防と感染対策", "薬剤感受性に基づく変更", "感染管理指導", "感染症科", "隔離管理の強化"),
            Icd10Chapter.CHAPTER_II to
                Profile("腫瘍性疾患", "腫瘍集学的治療", "腫瘍量評価と支持療法", "寛解維持療法", "分子標的治療の検討", "緩和ケア支援", "腫瘍内科", "集学的治療会議"),
            Icd10Chapter.CHAPTER_III to
                Profile("血液免疫疾患", "血液内科管理", "輸血または凝固補正", "血液指標フォロー", "免疫調整療法", "栄養造血支援", "血液内科", "造血管理の強化"),
            Icd10Chapter.CHAPTER_IV to
                Profile("内分泌代謝疾患", "代謝管理", "代謝異常の是正", "ホルモン補正療法", "専門薬物療法", "食事運動支援", "内分泌代謝内科", "代謝管理入院"),
            Icd10Chapter.CHAPTER_V to
                Profile("精神行動疾患", "心理社会的治療", "危機介入", "心理教育と服薬支援", "専門精神療法", "家族支援", "精神科", "安全確保のための介入"),
            Icd10Chapter.CHAPTER_VI to
                Profile("神経疾患", "神経機能管理", "急性神経症状への対応", "神経リハビリ継続", "神経専門薬の調整", "リハビリテーション", "神経内科", "神経集中管理"),
            Icd10Chapter.CHAPTER_VII to
                Profile("眼科疾患", "眼科治療", "視機能保護処置", "眼科定期管理", "手術または局所治療", "視覚支援", "眼科", "緊急眼科処置"),
            Icd10Chapter.CHAPTER_VIII to
                Profile("耳鼻平衡疾患", "耳鼻咽喉科治療", "めまい急性期管理", "聴平衡リハビリ", "内耳専門治療", "聴覚支援", "耳鼻咽喉科", "転倒予防を含む入院管理"),
            Icd10Chapter.CHAPTER_IX to
                Profile("循環器疾患", "循環器標準治療", "血行動態安定化", "心血管リスク管理", "高度循環器治療", "心臓リハビリ", "循環器内科", "集中循環管理"),
            Icd10Chapter.CHAPTER_X to
                Profile("呼吸器疾患", "呼吸器標準治療", "酸素化改善療法", "吸入または呼吸管理", "高度呼吸管理", "呼吸リハビリ", "呼吸器内科", "呼吸不全管理"),
            Icd10Chapter.CHAPTER_XI to
                Profile("消化器疾患", "消化器治療", "消化管症状の急性期管理", "栄養消化管理", "内視鏡治療の検討", "栄養療法", "消化器内科", "消化管出血対応"),
            Icd10Chapter.CHAPTER_XII to
                Profile("皮膚疾患", "皮膚科治療", "急性皮膚症状の鎮静化", "外用維持療法", "光線または免疫調整療法", "スキンケア指導", "皮膚科", "広範皮膚病変管理"),
            Icd10Chapter.CHAPTER_XIII to
                Profile("筋骨格疾患", "運動器治療", "疼痛と炎症の初期管理", "運動器機能維持", "専門リハビリ治療", "理学療法", "整形外科", "機能障害への集中的介入"),
            Icd10Chapter.CHAPTER_XIV to
                Profile("腎尿路疾患", "腎尿路管理", "水電解質補正", "腎機能保護療法", "腎代替療法の検討", "水分塩分指導", "腎臓内科", "急性腎機能悪化対応"),
            Icd10Chapter.CHAPTER_XV to
                Profile("妊娠関連疾患", "産科管理", "母体胎児安定化", "妊婦健診強化", "高次産科管理", "母体生活指導", "産婦人科", "母体胎児集中管理"),
            Icd10Chapter.CHAPTER_XVI to
                Profile("新生児疾患", "新生児管理", "体温呼吸循環の安定化", "発育フォロー", "新生児集中治療", "哺乳支援", "新生児科", "NICU 管理"),
            Icd10Chapter.CHAPTER_XVII to
                Profile("先天異常疾患", "先天異常管理", "合併症初期評価", "発達支援継続", "専門外科または遺伝医療", "家族支援", "小児科", "多職種カンファレンス"),
            Icd10Chapter.CHAPTER_XVIII to
                Profile("症状所見分類疾患", "症候別管理", "症状安定化", "経過観察", "追加診断後の治療変更", "生活記録支援", "総合診療科", "診断目的入院"),
            Icd10Chapter.CHAPTER_XIX to
                Profile("外傷損傷疾患", "外傷治療", "損傷部位の初期処置", "機能回復管理", "手術的治療の検討", "外傷リハビリ", "救急科", "外傷集中管理"),
            Icd10Chapter.CHAPTER_XX to
                Profile("外因関連状態", "外因リスク管理", "曝露後初期評価", "再曝露予防支援", "専門的安全介入", "環境調整", "救急科", "安全確保措置"),
            Icd10Chapter.CHAPTER_XXI to
                Profile("保健サービス関連状態", "予防保健管理", "健康課題の初期整理", "継続保健指導", "専門相談への連携", "セルフケア支援", "総合診療科", "支援資源の緊急調整"),
            Icd10Chapter.CHAPTER_XXII to
                Profile("特殊目的分類状態", "暫定分類管理", "分類確認の初期評価", "分類更新フォロー", "追加専門評価", "情報整理支援", "総合診療科", "分類再評価入院"),
        )
}
