package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object SymptomSeedBuckets {
    val byChapter: Map<Icd10Chapter, List<String>> =
        mapOf(
            Icd10Chapter.CHAPTER_I to
                listOf("発熱", "悪寒", "リンパ節腫脹", "倦怠感", "体重減少", "発汗", "筋肉痛", "関節痛"),
            Icd10Chapter.CHAPTER_II to
                listOf("体重減少", "腫瘤触知", "出血", "疼痛", "貧血", "食欲不振", "夜間発汗", "リンパ節腫脹"),
            Icd10Chapter.CHAPTER_III to
                listOf("出血傾向", "皮下出血", "蒼白", "動悸", "易疲労感", "発熱", "黄疸", "リンパ節腫脹"),
            Icd10Chapter.CHAPTER_IV to
                listOf("多飲多尿", "体重変動", "易疲労感", "倦怠感", "食欲変化", "振戦", "発汗異常", "寒冷不耐"),
            Icd10Chapter.CHAPTER_V to
                listOf("不安", "抑うつ気分", "不眠", "集中力低下", "意欲低下", "興奮", "幻覚", "強迫思考"),
            Icd10Chapter.CHAPTER_VI to
                listOf("頭痛", "痙攣", "麻痺", "失調", "振戦", "感覚障害", "構音障害", "意識障害"),
            Icd10Chapter.CHAPTER_VII to
                listOf("視力低下", "眼痛", "充血", "流涙", "霧視", "複視", "羞明", "眼瞼下垂"),
            Icd10Chapter.CHAPTER_VIII to
                listOf("難聴", "耳鳴", "めまい", "耳痛", "耳閉感", "平衡障害", "自発眼振", "眼振"),
            Icd10Chapter.CHAPTER_IX to
                listOf("胸痛", "動悸", "息切れ", "浮腫", "起座呼吸", "失神", "易疲労感", "下肢冷感"),
            Icd10Chapter.CHAPTER_X to
                listOf("咳嗽", "喀痰", "喘鳴", "呼吸困難", "胸痛", "血痰", "嗄声", "チアノーゼ"),
            Icd10Chapter.CHAPTER_XI to
                listOf("腹痛", "悪心", "嘔吐", "下痢", "便秘", "食欲不振", "黄疸", "吐血"),
            Icd10Chapter.CHAPTER_XII to
                listOf("皮疹", "掻痒", "紅斑", "水疱", "落屑", "色素沈着", "脱毛", "皮膚潰瘍"),
            Icd10Chapter.CHAPTER_XIII to
                listOf("関節痛", "筋肉痛", "腫脹", "可動域制限", "朝のこわばり", "変形", "筋力低下", "腰背部痛"),
            Icd10Chapter.CHAPTER_XIV to
                listOf("排尿障害", "血尿", "蛋白尿", "浮腫", "腰背部痛", "残尿感", "頻尿", "尿失禁"),
            Icd10Chapter.CHAPTER_XV to
                listOf("妊娠悪阻", "出血", "子宮収縮", "浮腫", "高血圧", "蛋白尿", "体重増加異常", "胎動減少"),
            Icd10Chapter.CHAPTER_XVI to
                listOf("哺乳不良", "啼泣異常", "黄疸", "呼吸障害", "体温異常", "痙攣", "嘔吐", "体重増加不良"),
            Icd10Chapter.CHAPTER_XVII to
                listOf("形態異常", "哺乳不良", "発達遅延", "筋緊張異常", "啼泣異常", "心雑音", "呼吸障害", "体重増加不良"),
            Icd10Chapter.CHAPTER_XVIII to
                listOf("発熱", "頭痛", "浮腫", "リンパ節腫脹", "失神", "倦怠感", "体重減少", "発汗異常"),
            Icd10Chapter.CHAPTER_XIX to
                listOf("創傷痛", "腫脹", "機能障害", "出血", "変形", "神経症状", "意識障害", "呼吸障害"),
            Icd10Chapter.CHAPTER_XX to
                listOf("外傷後疼痛", "外傷後不安", "不眠", "フラッシュバック", "集中力低下", "過覚醒", "回避行動", "過敏反応"),
            Icd10Chapter.CHAPTER_XXI to
                listOf("検診希望", "予防接種希望", "服薬指導希望", "健康相談希望", "リハビリ希望", "経過観察希望", "家族歴相談希望", "妊娠相談希望"),
            Icd10Chapter.CHAPTER_XXII to
                listOf("病原体特定不能発熱", "不明熱", "経過観察必要状態", "検査保留状態", "暫定診断状態", "鑑別中状態", "観察期間中", "経時変化観察中"),
        )
}

object SymptomSeedBucketRepository {
    fun get(chapter: Icd10Chapter): List<String> =
        SymptomSeedBuckets.byChapter[chapter]
            ?: error("Unknown ICD10 chapter '${chapter.chapterKey}'")
}
