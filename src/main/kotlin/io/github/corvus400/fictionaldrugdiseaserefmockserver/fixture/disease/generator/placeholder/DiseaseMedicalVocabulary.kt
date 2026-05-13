package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextChapters
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketSeedCoiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.SymptomSeedBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot

private const val MIN_ENTRY_COUNT = 3

private data class DiseaseNamedVocabulary(
    val key: String,
    val entries: List<String>,
) {
    init {
        require(entries.size >= MIN_ENTRY_COUNT) {
            "DiseaseNamedVocabulary '$key' must contain at least $MIN_ENTRY_COUNT entries " +
                "so resolve() can yield a meaningful variety; got ${entries.size}."
        }
        require(entries.all { it.isNotBlank() }) {
            "DiseaseNamedVocabulary '$key' contains a blank entry; every vocabulary entry must be non-blank."
        }
    }
}

private infix fun String.vocabulary(entries: List<String>): Pair<String, DiseaseNamedVocabulary> =
    this to DiseaseNamedVocabulary(key = this, entries = entries)

object DiseaseMedicalVocabulary {
    fun resolve(
        key: String,
        seed: Long,
        context: BucketContextKey = BucketContextKey.Global,
    ): String {
        val chapter = BucketContextChapters.pickChapter(context = context, seed = seed)
        if (chapter != null) {
            EtiologyVocabulary.resolveOrNull(key = key, chapter = chapter, seed = seed)?.let { return it }
            DiagnosticVocabulary.resolveOrNull(key = key, chapter = chapter, seed = seed)?.let { return it }
            TreatmentVocabulary.resolveOrNull(key = key, chapter = chapter, seed = seed)?.let { return it }
        }
        if (key in SYMPTOM_KEYS) {
            if (chapter != null) {
                return BucketSeedCoiner.coin(
                    bucket = SymptomSeedBucketRepository.get(chapter = chapter),
                    seed = seed,
                    slot = NameSlot.DISEASE_SYMPTOM,
                )
            }
        }
        val vocabulary =
            VOCABULARY[key]
                ?: error(
                    "Unknown category-A placeholder key '$key'. " +
                        "DiseaseMedicalVocabulary covers only the 38 Disease category-A keys. " +
                        "Other categories (B_SELF_REFERENCE / D_NUMERIC_RANGE) are resolved " +
                        "by DiseasePlaceholderDictionary.",
                )
        return ValueRangeGenerator.pickOne(seed = seed, candidates = vocabulary.entries)
    }

    private val SYMPTOM_KEYS =
        setOf(
            "additionalSymptom",
            "associatedSymptom",
            "initialSymptom",
            "mainSymptom",
            "progressedSymptom",
        )

    private val VOCABULARY: Map<String, DiseaseNamedVocabulary> =
        mapOf(
            "acuteTreatment" vocabulary
                listOf(
                    "ステロイドパルス療法",
                    "免疫グロブリン大量療法",
                    "輸液・電解質補正",
                    "酸素療法",
                    "抗菌薬投与",
                    "血漿交換",
                ),
            "additionalSymptom" vocabulary
                listOf(
                    "関節痛",
                    "筋痛",
                    "微熱",
                    "全身倦怠感",
                    "食欲不振",
                    "体重減少",
                    "睡眠障害",
                ),
            "adjunctTreatment" vocabulary
                listOf(
                    "理学療法",
                    "栄養療法",
                    "心理療法",
                    "生活指導",
                    "リハビリテーション",
                    "疼痛コントロール",
                ),
            "ageGroup" vocabulary
                listOf(
                    "小児",
                    "成人",
                    "高齢者",
                    "中高年",
                    "若年成人",
                    "学童",
                    "青年期",
                ),
            "associatedSymptom" vocabulary
                listOf(
                    "悪心",
                    "頭痛",
                    "めまい",
                    "倦怠感",
                    "関節痛",
                    "筋痛",
                    "発疹",
                ),
            "chronicity" vocabulary
                listOf(
                    "慢性",
                    "急性",
                    "亜急性",
                    "進行性",
                    "再発性",
                    "緩徐進行性",
                ),
            "clinicalFinding" vocabulary
                listOf(
                    "肝腫大",
                    "脾腫",
                    "リンパ節腫脹",
                    "皮疹",
                    "関節腫脹",
                    "浮腫",
                    "発熱所見",
                ),
            "diagnosticTest" vocabulary
                listOf(
                    "血液生化学検査",
                    "画像検査 (MRI)",
                    "画像検査 (CT)",
                    "生検による病理診断",
                    "遺伝子検査",
                    "特異的バイオマーカー測定",
                ),
            "differentialCondition" vocabulary
                listOf(
                    "感染症",
                    "悪性腫瘍",
                    "他の自己免疫疾患",
                    "内分泌疾患",
                    "代謝性疾患",
                    "薬剤性障害",
                ),
            "escalatedAction" vocabulary
                listOf(
                    "入院加療",
                    "集中治療室での管理",
                    "専門医への緊急紹介",
                    "治療強度の引き上げ",
                    "手術的治療の検討",
                ),
            "etiologyCategory" vocabulary
                listOf(
                    "自己免疫性疾患",
                    "感染性疾患",
                    "遺伝性疾患",
                    "代謝性疾患",
                    "特発性疾患",
                    "医原性疾患",
                ),
            "favorableFactor" vocabulary
                listOf(
                    "早期診断",
                    "良好な治療反応性",
                    "基礎疾患なし",
                    "若年発症",
                    "軽症型",
                    "合併症なし",
                ),
            "favorableOutcome" vocabulary
                listOf(
                    "長期寛解",
                    "完全寛解",
                    "機能温存",
                    "日常生活の維持",
                    "症状の消失",
                    "再発なし",
                ),
            "followUpExam" vocabulary
                listOf(
                    "定期血液検査",
                    "定期画像検査",
                    "機能評価検査",
                    "生活指導外来",
                    "専門医フォロー",
                ),
            "gradingSystem" vocabulary
                listOf(
                    "本邦独自重症度分類 (架空)",
                    "国際重症度分類 (架空)",
                    "学会推奨分類 (架空)",
                    "ガイドライン準拠分類 (架空)",
                ),
            "initialSymptom" vocabulary
                listOf(
                    "発熱",
                    "倦怠感",
                    "局所的な疼痛",
                    "発疹",
                    "体重減少",
                    "関節のこわばり",
                ),
            "mainFeature" vocabulary
                listOf(
                    "進行性の臓器障害",
                    "全身性炎症反応",
                    "慢性的な経過",
                    "多臓器への罹患",
                    "再発寛解の経過",
                    "特異的バイオマーカー上昇",
                ),
            "mainSymptom" vocabulary
                listOf(
                    "持続性の疼痛",
                    "発熱",
                    "倦怠感",
                    "機能障害",
                    "腫脹",
                    "呼吸困難",
                ),
            "maintenanceTreatment" vocabulary
                listOf(
                    "低用量免疫抑制薬",
                    "低用量ステロイド",
                    "生物学的製剤",
                    "長期抗菌療法",
                    "対症療法",
                ),
            "onsetPattern" vocabulary
                listOf(
                    "緩徐発症",
                    "急性発症",
                    "亜急性発症",
                    "潜行性発症",
                    "再発性発症",
                ),
            "organSystem" vocabulary
                listOf(
                    "消化器系",
                    "循環器系",
                    "呼吸器系",
                    "神経系",
                    "内分泌系",
                    "筋骨格系",
                    "泌尿器系",
                ),
            "prevalenceLabel" vocabulary
                listOf(
                    "まれな疾患",
                    "希少疾患",
                    "一般的ではない疾患",
                    "比較的まれな疾患",
                    "国指定難病相当 (架空)",
                ),
            "primaryFinding" vocabulary
                listOf(
                    "特異的血液検査異常",
                    "特徴的な画像所見",
                    "病理組織学的異常",
                    "典型的臨床症状",
                    "機能検査の著明な異常",
                ),
            "prognosisIndicator" vocabulary
                listOf(
                    "5 年生存率",
                    "機能保持率",
                    "寛解維持率",
                    "再発率",
                    "QOL スコア",
                ),
            "prognosticFactor" vocabulary
                listOf(
                    "初期重症度",
                    "治療反応性",
                    "合併症の有無",
                    "年齢",
                    "基礎疾患の有無",
                    "バイオマーカー値",
                ),
            "progressedComplication" vocabulary
                listOf(
                    "臓器不全",
                    "重症感染症",
                    "血栓塞栓症",
                    "悪性化",
                    "廃用症候群",
                    "機能不全",
                ),
            "progressedSymptom" vocabulary
                listOf(
                    "重度機能障害",
                    "全身状態の悪化",
                    "多臓器症状",
                    "意識障害",
                    "循環動態の悪化",
                ),
            "regionalNote" vocabulary
                listOf(
                    "都市部に多い",
                    "農村部に多い",
                    "地域差が認められる",
                    "地域差はない",
                    "特定地域に集積性がある",
                ),
            "riskFactor" vocabulary
                listOf(
                    "遺伝的素因",
                    "環境因子",
                    "生活習慣",
                    "既往症",
                    "年齢",
                    "性別",
                ),
            "seasonalNote" vocabulary
                listOf(
                    "冬季に多い",
                    "夏季に多い",
                    "春季に多い",
                    "秋季に多い",
                    "季節性なし",
                ),
            "secondLineTreatment" vocabulary
                listOf(
                    "強力免疫抑制療法",
                    "生物学的製剤",
                    "血漿交換",
                    "造血幹細胞移植",
                    "代替薬への切替え",
                ),
            "severeGradeLabel" vocabulary
                listOf(
                    "高度重症域",
                    "緊急介入域",
                    "重症",
                    "最重症",
                    "高度",
                ),
            "severityIndicator" vocabulary
                listOf(
                    "臨床スコア",
                    "画像評価スコア",
                    "機能評価スコア",
                    "生化学スコア",
                    "総合重症度スコア",
                ),
            "sexDominance" vocabulary
                listOf(
                    "女性優位",
                    "男性優位",
                    "性差なし",
                    "軽度女性優位",
                    "軽度男性優位",
                ),
            "specialistReferral" vocabulary
                listOf(
                    "総合内科",
                    "該当専門科",
                    "大学病院",
                    "専門医療機関",
                    "地域基幹病院",
                ),
            "supportingFinding" vocabulary
                listOf(
                    "副次的血液検査異常",
                    "画像補助所見",
                    "遺伝子検査陽性",
                    "バイオマーカーの軽度上昇",
                    "臨床スコアの異常",
                ),
            "symptomTriggerCondition" vocabulary
                listOf(
                    "運動負荷",
                    "寒冷刺激",
                    "感染症罹患",
                    "精神的ストレス",
                    "食事摂取",
                    "日光曝露",
                ),
            "treatmentCategory" vocabulary
                listOf(
                    "薬物療法",
                    "外科的治療",
                    "放射線療法",
                    "免疫療法",
                    "支持療法",
                    "生活療法",
                ),
        )
}
