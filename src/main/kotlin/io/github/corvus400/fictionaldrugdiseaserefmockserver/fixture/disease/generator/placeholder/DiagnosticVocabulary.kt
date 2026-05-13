package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object DiagnosticVocabulary {
    val keys: Set<String> =
        setOf("clinicalFinding", "primaryFinding", "supportingFinding", "diagnosticTest", "differentialCondition")

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
            "clinicalFinding" -> listOf(
                "${profile.findingFocus}所見",
                "${profile.domain}に整合する身体所見",
                "${profile.organ}機能の変化",
            )
            "primaryFinding" -> listOf(
                "${profile.primaryTest}の主要異常",
                "${profile.findingFocus}を示す中核所見",
                "${profile.domain}に特徴的な検査異常",
            )
            "supportingFinding" -> listOf(
                "${profile.supportingTest}の補助所見",
                "${profile.organ}関連指標の軽度異常",
                "${profile.domain}評価スコアの上昇",
            )
            "diagnosticTest" -> listOf(
                profile.primaryTest,
                profile.supportingTest,
                "${profile.domain}評価パネル",
            )
            "differentialCondition" -> listOf(
                profile.differentialCondition,
                "${profile.domain}に類似する別疾患",
                "薬剤性または外因性の類似病態",
            )
            else -> null
        }
    }

    private fun profileFor(chapter: Icd10Chapter): Profile =
        PROFILES[chapter] ?: error("Unknown ICD10 chapter '${chapter.chapterKey}'")

    private data class Profile(
        val domain: String,
        val organ: String,
        val findingFocus: String,
        val primaryTest: String,
        val supportingTest: String,
        val differentialCondition: String,
    )

    private val PROFILES: Map<Icd10Chapter, Profile> =
        mapOf(
            Icd10Chapter.CHAPTER_I to Profile("感染症", "免疫応答", "発熱炎症", "病原体検査", "炎症反応検査", "非感染性炎症"),
            Icd10Chapter.CHAPTER_II to Profile("腫瘍性疾患", "腫瘍組織", "腫瘤形成", "病理組織検査", "腫瘍マーカー検査", "良性腫瘍"),
            Icd10Chapter.CHAPTER_III to Profile("血液免疫疾患", "血液", "血球異常", "血算検査", "凝固機能検査", "二次性血液異常"),
            Icd10Chapter.CHAPTER_IV to Profile("内分泌代謝疾患", "代謝調節", "ホルモン異常", "内分泌負荷検査", "代謝指標検査", "薬剤性代謝異常"),
            Icd10Chapter.CHAPTER_V to Profile("精神行動疾患", "認知情動", "行動変化", "構造化面接", "心理評価尺度", "神経疾患に伴う行動変化"),
            Icd10Chapter.CHAPTER_VI to Profile("神経疾患", "神経", "神経脱落", "神経画像検査", "神経生理検査", "代謝性神経症状"),
            Icd10Chapter.CHAPTER_VII to Profile("眼科疾患", "眼", "視機能低下", "眼底検査", "視野検査", "神経性視覚障害"),
            Icd10Chapter.CHAPTER_VIII to Profile("耳鼻平衡疾患", "内耳", "聴平衡機能低下", "聴力検査", "前庭機能検査", "中枢性めまい"),
            Icd10Chapter.CHAPTER_IX to Profile("循環器疾患", "心血管", "血行動態異常", "心電図検査", "心エコー検査", "呼吸器由来の胸部症状"),
            Icd10Chapter.CHAPTER_X to Profile("呼吸器疾患", "呼吸器", "換気障害", "呼吸機能検査", "胸部画像検査", "循環器由来の息切れ"),
            Icd10Chapter.CHAPTER_XI to Profile("消化器疾患", "消化器", "消化管機能異常", "内視鏡検査", "腹部画像検査", "泌尿器由来の腹部症状"),
            Icd10Chapter.CHAPTER_XII to Profile("皮膚疾患", "皮膚", "皮膚バリア異常", "皮膚生検", "皮膚画像評価", "全身疾患に伴う皮疹"),
            Icd10Chapter.CHAPTER_XIII to Profile("筋骨格疾患", "運動器", "関節機能低下", "関節画像検査", "炎症マーカー検査", "神経由来の運動障害"),
            Icd10Chapter.CHAPTER_XIV to Profile("腎尿路疾患", "腎尿路", "排泄機能低下", "尿検査", "腎機能検査", "消化器由来の腹部症状"),
            Icd10Chapter.CHAPTER_XV to Profile("妊娠関連疾患", "母体胎盤", "胎盤機能異常", "産科超音波検査", "母体血圧検査", "非妊娠性内科疾患"),
            Icd10Chapter.CHAPTER_XVI to Profile("新生児疾患", "新生児", "出生後適応異常", "新生児診察", "血液ガス検査", "先天異常"),
            Icd10Chapter.CHAPTER_XVII to Profile("先天異常疾患", "発生形成", "形態形成異常", "遺伝学的検査", "形態評価検査", "周産期適応障害"),
            Icd10Chapter.CHAPTER_XVIII to Profile("症状所見分類疾患", "全身所見", "原因未確定所見", "総合臨床評価", "追加スクリーニング検査", "確定分類済み疾患"),
            Icd10Chapter.CHAPTER_XIX to Profile("外傷損傷疾患", "損傷組織", "急性損傷所見", "損傷部位画像検査", "神経血管評価", "非外傷性急性症状"),
            Icd10Chapter.CHAPTER_XX to Profile("外因関連状態", "外因評価", "環境要因関連所見", "曝露状況評価", "安全リスク評価", "内因性疾患"),
            Icd10Chapter.CHAPTER_XXI to Profile("保健サービス関連状態", "健康管理", "予防管理不足", "健康評価面接", "生活習慣評価", "急性疾患"),
            Icd10Chapter.CHAPTER_XXII to Profile("特殊目的分類状態", "分類評価", "暫定分類所見", "分類確認検査", "追加分類評価", "通常分類済み疾患"),
        )
}
