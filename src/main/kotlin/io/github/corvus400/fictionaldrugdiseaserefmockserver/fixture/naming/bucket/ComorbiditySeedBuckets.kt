package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object ComorbiditySeedBuckets {
    val byChapter: Map<Icd10Chapter, List<String>> =
        mapOf(
            Icd10Chapter.CHAPTER_I to listOf("慢性肝炎", "糖尿病", "慢性腎臓病", "慢性閉塞性肺疾患", "関節リウマチ", "心不全"),
            Icd10Chapter.CHAPTER_II to listOf("糖尿病", "高血圧症", "慢性腎臓病", "慢性肝炎", "虚血性心疾患", "慢性閉塞性肺疾患"),
            Icd10Chapter.CHAPTER_III to listOf("慢性肝炎", "慢性腎臓病", "心不全", "糖尿病", "関節リウマチ", "自己免疫疾患"),
            Icd10Chapter.CHAPTER_IV to listOf("高血圧症", "脂質異常症", "慢性腎臓病", "虚血性心疾患", "脳血管障害", "肥満症"),
            Icd10Chapter.CHAPTER_V to listOf("高血圧症", "糖尿病", "脂質異常症", "慢性疼痛", "てんかん", "認知症"),
            Icd10Chapter.CHAPTER_VI to listOf("高血圧症", "糖尿病", "慢性腎臓病", "虚血性心疾患", "脂質異常症", "不整脈"),
            Icd10Chapter.CHAPTER_VII to listOf("糖尿病", "高血圧症", "動脈硬化症", "慢性腎臓病", "関節リウマチ", "自己免疫疾患"),
            Icd10Chapter.CHAPTER_VIII to listOf("高血圧症", "糖尿病", "動脈硬化症", "めまい関連疾患", "自己免疫疾患", "慢性中耳炎"),
            Icd10Chapter.CHAPTER_IX to listOf("糖尿病", "脂質異常症", "慢性腎臓病", "慢性閉塞性肺疾患", "心房細動", "脳血管障害"),
            Icd10Chapter.CHAPTER_X to listOf("喘息", "慢性閉塞性肺疾患", "心不全", "アレルギー性鼻炎", "糖尿病", "高血圧症"),
            Icd10Chapter.CHAPTER_XI to listOf("慢性肝炎", "糖尿病", "過敏性腸症候群", "機能性消化不良", "胆石症", "膵炎"),
            Icd10Chapter.CHAPTER_XII to listOf("アトピー性皮膚炎", "関節リウマチ", "糖尿病", "自己免疫疾患", "アレルギー疾患", "慢性肝炎"),
            Icd10Chapter.CHAPTER_XIII to listOf("骨粗鬆症", "関節リウマチ", "糖尿病", "高血圧症", "慢性腎臓病", "痛風"),
            Icd10Chapter.CHAPTER_XIV to listOf("糖尿病", "高血圧症", "脂質異常症", "心不全", "虚血性心疾患", "慢性肝炎"),
            Icd10Chapter.CHAPTER_XV to listOf("妊娠糖尿病", "妊娠高血圧症候群", "甲状腺機能異常", "妊娠貧血", "切迫早産", "妊娠悪阻"),
            Icd10Chapter.CHAPTER_XVI to listOf("出生時低体重", "早産", "新生児黄疸", "新生児呼吸窮迫", "新生児低血糖", "新生児感染症"),
            Icd10Chapter.CHAPTER_XVII to listOf("心奇形", "染色体異常", "神経管閉鎖障害", "口唇口蓋裂", "多発奇形", "単一遺伝子疾患"),
            Icd10Chapter.CHAPTER_XVIII to listOf("慢性疲労状態", "不明熱", "体重減少状態", "リンパ節腫脹状態", "浮腫状態", "失神既往"),
            Icd10Chapter.CHAPTER_XIX to listOf("多発外傷既往", "慢性疼痛", "創部感染既往", "神経損傷既往", "心的外傷後ストレス", "後遺障害状態"),
            Icd10Chapter.CHAPTER_XX to listOf("外傷後ストレス状態", "不安障害", "うつ状態", "不眠症", "慢性疼痛", "アルコール使用障害"),
            Icd10Chapter.CHAPTER_XXI to listOf("高血圧症", "糖尿病", "脂質異常症", "慢性腎臓病", "慢性閉塞性肺疾患", "関節リウマチ"),
            Icd10Chapter.CHAPTER_XXII to listOf("病原体特定不能感染状態", "不明熱状態", "経過観察必要状態", "暫定診断状態", "慢性疾患併存", "多臓器障害状態"),
        )
}

object ComorbiditySeedBucketRepository {
    fun get(chapter: Icd10Chapter): List<String> =
        ComorbiditySeedBuckets.byChapter[chapter]
            ?: error("Unknown ICD10 chapter '${chapter.chapterKey}'")
}
