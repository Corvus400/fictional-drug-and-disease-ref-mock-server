package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.ForbiddenNames
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand

object AdverseReactionByFreqSeedBuckets {
    val byAtcInitialAndFrequency: Map<Char, Map<FrequencyBand, List<String>>> =
        AdverseReactionSeedBuckets.byAtcInitial.mapValues { (_, seeds) ->
            val safeSeeds = seeds.filterNot { entry -> ForbiddenNames.contains(name = entry) }
            mapOf(
                FrequencyBand.OVER_5_PERCENT to safeSeeds.take(6),
                FrequencyBand.BETWEEN_1_AND_5_PERCENT to safeSeeds.drop(1).take(6).ifEmpty { safeSeeds.take(6) },
                FrequencyBand.UNDER_1_PERCENT to safeSlice(entries = safeSeeds, drop = 2),
                FrequencyBand.UNKNOWN to safeSlice(entries = safeSeeds, drop = 3),
            )
        }

    private fun safeSlice(entries: List<String>, drop: Int): List<String> =
        entries.drop(drop).take(6).takeIf { slice -> slice.size >= MIN_BUCKET_SIZE } ?: entries.take(6)

    private const val MIN_BUCKET_SIZE: Int = 5
}

object AdverseReactionByFreqSeedBucketRepository {
    fun get(
        atcInitial: Char,
        frequency: FrequencyBand,
    ): List<String> =
        AdverseReactionByFreqSeedBuckets.byAtcInitialAndFrequency[atcInitial.uppercaseChar()]
            ?.get(frequency)
            ?: error("Unknown adverse reaction frequency bucket: atcInitial=$atcInitial, frequency=$frequency")
}

object SeverityGradeSeedBuckets {
    val byChapter: Map<Icd10Chapter, List<String>> =
        Icd10Chapter.entries.associateWith { chapter ->
            when (chapter) {
                Icd10Chapter.CHAPTER_II -> listOf("StageI", "StageII", "StageIII", "StageIV", "末期")
                Icd10Chapter.CHAPTER_IX -> listOf("NYHA1", "NYHA2", "NYHA3", "NYHA4", "末期心不全")
                Icd10Chapter.CHAPTER_X -> listOf("GOLD1", "GOLD2", "GOLD3", "GOLD4", "末期")
                else -> listOf("軽症", "中等症", "重症", "重篤", "経過観察")
            }
        }
}

object SeverityGradeSeedBucketRepository {
    fun get(chapter: Icd10Chapter): List<String> = SeverityGradeSeedBuckets.byChapter.getValue(chapter)
}

object TreatmentPharmaSeedBuckets {
    val byChapter: Map<Icd10Chapter, List<String>> =
        Icd10Chapter.entries.associateWith { chapter ->
            when (chapter) {
                Icd10Chapter.CHAPTER_I -> listOf("抗生物質", "抗ウイルス薬", "抗結核薬", "抗真菌薬", "抗寄生虫薬", "ワクチン")
                Icd10Chapter.CHAPTER_II -> listOf("抗腫瘍薬", "分子標的薬", "免疫療法薬", "支持療法薬", "細胞傷害薬", "緩和治療薬")
                Icd10Chapter.CHAPTER_V -> listOf("抗うつ薬", "抗不安薬", "睡眠薬", "抗精神病薬", "気分安定薬", "認知療法薬")
                else -> listOf("対症療法薬", "維持療法薬", "補助療法薬", "急性期治療薬", "慢性期治療薬", "予防療法薬")
            }
        }
}

object TreatmentPharmaSeedBucketRepository {
    fun get(chapter: Icd10Chapter): List<String> = TreatmentPharmaSeedBuckets.byChapter.getValue(chapter)
}

object TreatmentNonPharmaSeedBuckets {
    val byChapter: Map<Icd10Chapter, List<String>> =
        Icd10Chapter.entries.associateWith { chapter ->
            when (chapter) {
                Icd10Chapter.CHAPTER_II -> listOf("手術療法", "放射線療法", "緩和ケア", "栄養管理", "心理社会的支援", "在宅ケア移行")
                Icd10Chapter.CHAPTER_V -> listOf("認知行動療法", "精神療法", "作業療法", "心理教育", "家族支援", "環境調整")
                Icd10Chapter.CHAPTER_X -> listOf("禁煙指導", "呼吸リハビリ", "酸素療法管理", "環境調整", "吸入手技指導", "在宅管理")
                else -> listOf("生活指導", "運動療法", "食事療法", "経過観察", "患者教育", "多職種連携")
            }
        }
}

object TreatmentNonPharmaSeedBucketRepository {
    fun get(chapter: Icd10Chapter): List<String> = TreatmentNonPharmaSeedBuckets.byChapter.getValue(chapter)
}

object GradingSystemSeedBuckets {
    val byChapter: Map<Icd10Chapter, List<String>> =
        Icd10Chapter.entries.associateWith { chapter ->
            when (chapter) {
                Icd10Chapter.CHAPTER_II -> listOf("TNM病期分類", "AJCC病期分類", "組織学的悪性度分類", "予後リスク分類", "治療反応性分類")
                Icd10Chapter.CHAPTER_IX -> listOf("NYHA心機能分類", "心房細動分類", "高血圧重症度分類", "虚血性心疾患分類", "弁膜症重症度分類")
                else -> listOf("重症度分類", "臨床分類", "活動性分類", "経過観察分類", "治療反応性分類")
            }
        }
}

object GradingSystemSeedBucketRepository {
    fun get(chapter: Icd10Chapter): List<String> = GradingSystemSeedBuckets.byChapter.getValue(chapter)
}

object JournalSeedBuckets {
    val all: List<String> =
        listOf("日本薬理学会誌", "日本臨床薬理学会誌", "薬学雑誌", "日本医療薬学会雑誌", "臨床医薬", "医療薬学")
}

object ExamFindingSeedBuckets {
    val typicalByChapter: Map<Icd10Chapter, List<String>> =
        Icd10Chapter.entries.associateWith { chapter ->
            when (chapter) {
                Icd10Chapter.CHAPTER_IX -> listOf("心電図異常", "心エコー異常", "不整脈所見", "心拡大所見", "弁膜症所見", "虚血性変化")
                Icd10Chapter.CHAPTER_X -> listOf("胸部X線異常陰影", "肺機能低下所見", "喀痰検査異常", "胸膜所見異常", "呼気NO上昇", "気道過敏性亢進")
                else -> listOf("検査異常所見", "画像所見", "血液検査所見", "臨床所見", "経過観察所見", "問診所見")
            }
        }
    val rangeByChapter: Map<Icd10Chapter, List<String>> =
        Icd10Chapter.entries.associateWith {
            listOf("基準範囲内", "軽度上昇", "中等度上昇", "重度上昇", "経過観察域", "要再検査域")
        }
}

object ExamFindingSeedBucketRepository {
    fun typical(chapter: Icd10Chapter): List<String> = ExamFindingSeedBuckets.typicalByChapter.getValue(chapter)

    fun range(chapter: Icd10Chapter): List<String> = ExamFindingSeedBuckets.rangeByChapter.getValue(chapter)
}

object ClinicalSeedBucketRegistry {
    val all: Map<String, List<String>> =
        buildMap {
            AdverseReactionByFreqSeedBuckets.byAtcInitialAndFrequency.forEach { (atc, byFrequency) ->
                byFrequency.forEach { (frequency, entries) -> put("adverseByFreq:$atc:$frequency", entries) }
            }
            SeverityGradeSeedBuckets.byChapter.forEach { (chapter, entries) -> put("severity:$chapter", entries) }
            TreatmentPharmaSeedBuckets.byChapter.forEach { (chapter, entries) ->
                put("treatmentPharma:$chapter", entries)
            }
            TreatmentNonPharmaSeedBuckets.byChapter.forEach { (chapter, entries) ->
                put("treatmentNonPharma:$chapter", entries)
            }
            GradingSystemSeedBuckets.byChapter.forEach { (chapter, entries) -> put("gradingSystem:$chapter", entries) }
            put("author", AuthorSeedBuckets.all)
            put("titleStem", TitleSeedBuckets.all)
            put("journal", JournalSeedBuckets.all)
            ExamFindingSeedBuckets.typicalByChapter.forEach { (chapter, entries) ->
                put("examTypical:$chapter", entries)
            }
            ExamFindingSeedBuckets.rangeByChapter.forEach { (chapter, entries) -> put("examRange:$chapter", entries) }
        }
}
