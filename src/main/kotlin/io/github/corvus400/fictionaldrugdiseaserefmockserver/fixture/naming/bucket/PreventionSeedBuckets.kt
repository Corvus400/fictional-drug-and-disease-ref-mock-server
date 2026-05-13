package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

object PreventionSeedBuckets {
    val byChapter: Map<Icd10Chapter, List<String>> =
        mapOf(
            Icd10Chapter.CHAPTER_I to
                listOf("手指衛生の徹底", "適切な換気", "接触機会の低減", "予防接種の検討", "食品衛生管理", "媒介生物対策"),
            Icd10Chapter.CHAPTER_II to
                listOf("禁煙支援", "発癌曝露の低減", "定期がん検診", "体重管理", "飲酒量の調整", "家族歴に応じた相談"),
            Icd10Chapter.CHAPTER_III to
                listOf("栄養状態の評価", "鉄摂取の最適化", "出血リスク管理", "薬剤確認", "定期血液検査", "基礎疾患管理"),
            Icd10Chapter.CHAPTER_IV to
                listOf("体重管理", "運動習慣の維持", "食事内容の調整", "血糖確認", "内分泌検査", "服薬継続支援"),
            Icd10Chapter.CHAPTER_V to
                listOf("睡眠リズムの維持", "心理的支援", "ストレス対処", "孤立予防", "物質使用の相談", "早期相談窓口の利用"),
            Icd10Chapter.CHAPTER_VI to
                listOf("血圧管理", "糖代謝管理", "睡眠評価", "転倒予防", "神経症状の早期相談", "服薬確認"),
            Icd10Chapter.CHAPTER_VII to
                listOf("眼科定期検査", "紫外線対策", "眼圧確認", "糖代謝管理", "眼外傷予防", "視力変化の早期相談"),
            Icd10Chapter.CHAPTER_VIII to
                listOf("騒音曝露の低減", "聴力検査", "耳毒性薬剤の確認", "転倒予防", "平衡機能相談", "耳症状の早期受診"),
            Icd10Chapter.CHAPTER_IX to
                listOf("血圧管理", "禁煙支援", "脂質管理", "運動習慣の維持", "食塩摂取の調整", "心血管検診"),
            Icd10Chapter.CHAPTER_X to
                listOf("禁煙支援", "大気汚染曝露の低減", "粉じん対策", "室内環境調整", "呼吸機能検査", "アレルゲン管理"),
            Icd10Chapter.CHAPTER_XI to
                listOf("食事内容の調整", "飲酒量の調整", "薬剤確認", "便通管理", "内視鏡検査の検討", "腹部症状の早期相談"),
            Icd10Chapter.CHAPTER_XII to
                listOf("保湿ケア", "紫外線対策", "刺激物回避", "皮膚観察", "アレルゲン管理", "外用薬の適正使用"),
            Icd10Chapter.CHAPTER_XIII to
                listOf("姿勢調整", "筋力維持", "体重管理", "関節負荷の低減", "転倒予防", "リハビリ相談"),
            Icd10Chapter.CHAPTER_XIV to
                listOf("血圧管理", "糖代謝管理", "水分管理", "尿検査", "薬剤確認", "排尿症状の早期相談"),
            Icd10Chapter.CHAPTER_XV to
                listOf("妊婦健診の継続", "血圧確認", "栄養相談", "胎動確認", "基礎疾患管理", "産科相談の早期化"),
            Icd10Chapter.CHAPTER_XVI to
                listOf("周産期管理", "哺乳支援", "体温管理", "新生児健診", "栄養評価", "家族へのケア教育"),
            Icd10Chapter.CHAPTER_XVII to
                listOf("遺伝相談", "胎児評価", "母体代謝管理", "薬剤確認", "発達フォロー", "家族歴確認"),
            Icd10Chapter.CHAPTER_XVIII to
                listOf("経過観察", "追加検査の実施", "服薬確認", "生活記録", "症状変化の早期相談", "基礎疾患整理"),
            Icd10Chapter.CHAPTER_XIX to
                listOf("転倒予防", "安全装備の使用", "作業環境調整", "骨脆弱性評価", "救急相談体制", "外傷後フォロー"),
            Icd10Chapter.CHAPTER_XX to
                listOf("交通安全対策", "作業安全教育", "防災準備", "暴力被害相談", "環境リスク確認", "安全装備の使用"),
            Icd10Chapter.CHAPTER_XXI to
                listOf("定期健診の受診", "予防接種計画", "服薬相談", "生活習慣相談", "社会的支援の利用", "健康記録の管理"),
            Icd10Chapter.CHAPTER_XXII to
                listOf("追加情報の収集", "分類更新の確認", "経過観察", "専門相談", "検査結果の再確認", "暫定管理計画"),
        )

    fun preventionFor(chapter: Icd10Chapter): List<String> =
        byChapter[chapter] ?: error("Unknown ICD10 chapter '${chapter.chapterKey}'")

    fun infectionExclusiveItems(): Set<String> =
        preventionFor(chapter = Icd10Chapter.CHAPTER_I).toSet() -
            byChapter
                .filterKeys { chapter -> chapter != Icd10Chapter.CHAPTER_I }
                .values
                .flatten()
                .toSet()
}
