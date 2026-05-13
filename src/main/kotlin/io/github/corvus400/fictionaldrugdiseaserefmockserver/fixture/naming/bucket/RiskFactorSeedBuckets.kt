package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object RiskFactorSeedBuckets {
    internal val INFECTION_KEYWORDS: Set<String> =
        setOf("接触", "飛沫", "空気", "経口", "媒介", "伝播", "曝露", "感染")

    val byChapter: Map<Icd10Chapter, List<String>> =
        mapOf(
            Icd10Chapter.CHAPTER_I to
                listOf("飛沫伝播", "空気感染への曝露", "経口感染への曝露", "媒介生物への曝露", "感染者との濃厚接触", "院内接触歴"),
            Icd10Chapter.CHAPTER_II to
                listOf("喫煙歴", "放射線曝露", "発癌物質曝露", "家族歴", "慢性炎症", "高齢"),
            Icd10Chapter.CHAPTER_III to
                listOf("鉄欠乏", "骨髄抑制", "自己免疫素因", "抗凝固薬使用", "栄養障害", "慢性出血"),
            Icd10Chapter.CHAPTER_IV to
                listOf("肥満", "運動不足", "過栄養", "内分泌素因", "ステロイド使用", "家族歴"),
            Icd10Chapter.CHAPTER_V to
                listOf("心理社会的ストレス", "睡眠不足", "孤立", "発達特性", "物質使用", "家族歴"),
            Icd10Chapter.CHAPTER_VI to
                listOf("高血圧", "糖代謝異常", "頭部外傷歴", "神経変性素因", "睡眠障害", "家族歴"),
            Icd10Chapter.CHAPTER_VII to
                listOf("眼圧上昇", "紫外線曝露", "加齢", "糖代謝異常", "眼外傷歴", "近視"),
            Icd10Chapter.CHAPTER_VIII to
                listOf("騒音曝露", "加齢", "内耳循環障害", "耳毒性薬剤使用", "頭部外傷歴", "家族歴"),
            Icd10Chapter.CHAPTER_IX to
                listOf("高血圧", "喫煙歴", "脂質異常", "糖代謝異常", "運動不足", "家族歴"),
            Icd10Chapter.CHAPTER_X to
                listOf("喫煙歴", "大気汚染曝露", "職業性粉じん曝露", "アレルギー素因", "換気不良", "加齢"),
            Icd10Chapter.CHAPTER_XI to
                listOf("高脂肪食", "飲酒習慣", "薬剤使用", "胆汁うっ滞", "ストレス", "家族歴"),
            Icd10Chapter.CHAPTER_XII to
                listOf("乾燥環境", "紫外線曝露", "アレルギー素因", "皮膚刺激物曝露", "摩擦", "家族歴"),
            Icd10Chapter.CHAPTER_XIII to
                listOf("過負荷動作", "加齢", "姿勢不良", "自己免疫素因", "肥満", "外傷歴"),
            Icd10Chapter.CHAPTER_XIV to
                listOf("高血圧", "糖代謝異常", "尿路閉塞", "薬剤使用", "脱水", "家族歴"),
            Icd10Chapter.CHAPTER_XV to
                listOf("高年妊娠", "多胎妊娠", "妊娠高血圧既往", "胎盤機能不全", "栄養不良", "基礎疾患"),
            Icd10Chapter.CHAPTER_XVI to
                listOf("早産", "低出生体重", "周産期低酸素", "母体合併症", "栄養不良", "新生児医療介入"),
            Icd10Chapter.CHAPTER_XVII to
                listOf("遺伝的素因", "染色体異常", "胎内薬剤曝露", "母体代謝異常", "家族歴", "胎児発育不全"),
            Icd10Chapter.CHAPTER_XVIII to
                listOf("原因未確定状態", "多疾患併存", "高齢", "薬剤使用", "生活習慣変化", "検査未完了"),
            Icd10Chapter.CHAPTER_XIX to
                listOf("転倒", "交通外傷", "作業中外傷", "スポーツ外傷", "骨脆弱性", "薬剤影響"),
            Icd10Chapter.CHAPTER_XX to
                listOf("危険作業環境", "交通環境曝露", "転倒リスク", "安全装備不足", "災害曝露", "暴力被害"),
            Icd10Chapter.CHAPTER_XXI to
                listOf("検診未受診", "予防接種未完了", "服薬自己中断", "健康相談不足", "社会的支援不足", "生活習慣リスク"),
            Icd10Chapter.CHAPTER_XXII to
                listOf("暫定分類状態", "追加検査待機", "分類不能所見", "情報不足", "経過観察中", "診断保留"),
        )

    fun riskFactorsFor(chapter: Icd10Chapter): List<String> =
        byChapter[chapter] ?: error("Unknown ICD10 chapter '${chapter.chapterKey}'")

    fun infectionExclusiveFactors(): Set<String> =
        riskFactorsFor(chapter = Icd10Chapter.CHAPTER_I).toSet() -
            byChapter
                .filterKeys { chapter -> chapter != Icd10Chapter.CHAPTER_I }
                .values
                .flatten()
                .toSet()
}
