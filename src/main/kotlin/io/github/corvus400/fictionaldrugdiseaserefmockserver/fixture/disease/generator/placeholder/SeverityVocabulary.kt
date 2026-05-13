package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object SeverityVocabulary {
    val keys: Set<String> =
        setOf("gradingSystem", "severeGradeLabel", "severityIndicator")

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
            "gradingSystem" -> listOf(
                "${profile.domain}重症度分類 (架空)",
                "${profile.organ}機能重症度分類 (架空)",
                "${profile.domain}臨床ステージ分類 (架空)",
            )
            "severeGradeLabel" -> listOf("${profile.domain}重症域", "高度${profile.organ}障害域", "緊急介入域")
            "severityIndicator" -> listOf(profile.indicator, "${profile.organ}機能スコア", "総合臨床重症度スコア")
            else -> null
        }
    }

    private fun profileFor(chapter: Icd10Chapter): Profile =
        PROFILES[chapter] ?: error("Unknown ICD10 chapter '${chapter.chapterKey}'")

    private data class Profile(
        val domain: String,
        val organ: String,
        val indicator: String,
    )

    private val PROFILES: Map<Icd10Chapter, Profile> =
        mapOf(
            Icd10Chapter.CHAPTER_I to Profile("感染症", "免疫応答", "炎症反応スコア"),
            Icd10Chapter.CHAPTER_II to Profile("腫瘍性疾患", "腫瘍組織", "腫瘍進展スコア"),
            Icd10Chapter.CHAPTER_III to Profile("血液免疫疾患", "血液", "血液指標スコア"),
            Icd10Chapter.CHAPTER_IV to Profile("内分泌代謝疾患", "代謝調節", "代謝異常スコア"),
            Icd10Chapter.CHAPTER_V to Profile("精神行動疾患", "認知情動", "心理社会機能スコア"),
            Icd10Chapter.CHAPTER_VI to Profile("神経疾患", "神経", "神経機能スコア"),
            Icd10Chapter.CHAPTER_VII to Profile("眼科疾患", "眼", "視機能スコア"),
            Icd10Chapter.CHAPTER_VIII to Profile("耳鼻平衡疾患", "内耳", "聴平衡機能スコア"),
            Icd10Chapter.CHAPTER_IX to Profile("循環器疾患", "心血管", "血行動態スコア"),
            Icd10Chapter.CHAPTER_X to Profile("呼吸器疾患", "呼吸器", "呼吸機能スコア"),
            Icd10Chapter.CHAPTER_XI to Profile("消化器疾患", "消化器", "消化器症状スコア"),
            Icd10Chapter.CHAPTER_XII to Profile("皮膚疾患", "皮膚", "皮膚病変スコア"),
            Icd10Chapter.CHAPTER_XIII to Profile("筋骨格疾患", "運動器", "運動器機能スコア"),
            Icd10Chapter.CHAPTER_XIV to Profile("腎尿路疾患", "腎尿路", "腎機能スコア"),
            Icd10Chapter.CHAPTER_XV to Profile("妊娠関連疾患", "母体胎盤", "母体胎児リスクスコア"),
            Icd10Chapter.CHAPTER_XVI to Profile("新生児疾患", "新生児", "新生児適応スコア"),
            Icd10Chapter.CHAPTER_XVII to Profile("先天異常疾患", "発生形成", "発達機能スコア"),
            Icd10Chapter.CHAPTER_XVIII to Profile("症状所見分類疾患", "全身所見", "症候重症度スコア"),
            Icd10Chapter.CHAPTER_XIX to Profile("外傷損傷疾患", "損傷組織", "外傷重症度スコア"),
            Icd10Chapter.CHAPTER_XX to Profile("外因関連状態", "外因評価", "曝露影響スコア"),
            Icd10Chapter.CHAPTER_XXI to Profile("保健サービス関連状態", "健康管理", "支援必要度スコア"),
            Icd10Chapter.CHAPTER_XXII to Profile("特殊目的分類状態", "分類評価", "分類優先度スコア"),
        )
}
