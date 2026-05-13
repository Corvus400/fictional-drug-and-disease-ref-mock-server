package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

object AdverseReactionSeedBuckets {
    val byAtcInitial: Map<Char, List<String>> =
        mapOf(
            'A' to listOf("偽膜性大腸炎", "麻痺性イレウス", "薬剤性肝障害", "急性膵炎", "重度の便秘", "電解質異常", "低マグネシウム血症", "ビタミンB12欠乏"),
            'B' to listOf("出血傾向", "血小板減少", "重度貧血", "血栓塞栓症", "過敏症反応", "顆粒球減少症", "骨髄抑制", "皮下出血"),
            'C' to listOf("徐脈", "起立性低血圧", "浮腫", "電解質異常", "房室ブロック", "空咳", "光線過敏症", "高カリウム血症"),
            'D' to listOf("接触皮膚炎", "重度発疹", "光線過敏症", "全身性皮疹", "蕁麻疹", "皮膚萎縮", "色素沈着", "末梢神経障害"),
            'G' to listOf("性機能障害", "月経異常", "排尿障害", "浮腫", "体液貯留", "血栓塞栓症", "乳房痛", "体重増加"),
            'H' to listOf("副腎不全", "高血糖", "骨粗鬆症", "体液貯留", "高血圧", "易感染性", "筋力低下", "精神症状"),
            'J' to listOf("偽膜性大腸炎", "薬剤性肝障害", "発疹", "低血糖", "好中球減少", "薬剤熱", "クロストリジウム性下痢", "アナフィラキシー"),
            'L' to listOf("骨髄抑制", "脱毛", "口内炎", "悪心嘔吐", "末梢神経障害", "発熱性好中球減少", "手足症候群", "間質性肺障害"),
            'M' to listOf("消化性潰瘍", "腎機能障害", "高血圧", "浮腫", "出血傾向", "肝機能障害", "過敏症反応", "喘息発作"),
            'N' to listOf("眠気", "起立性めまい", "錐体外路症状", "口渇", "便秘", "起立性低血圧", "認知機能低下", "体重増加"),
            'P' to listOf("神経毒性", "肝障害", "過敏症反応", "視覚障害", "心電図異常", "溶血性貧血", "発熱反応", "消化器症状"),
            'R' to listOf("動悸", "振戦", "低カリウム血症", "高血糖", "不眠", "嗄声", "口腔咽頭カンジダ症", "局所刺激感"),
            'S' to listOf("局所刺激感", "結膜充血", "涙液分泌異常", "一過性霧視", "角膜炎", "接触皮膚炎", "色素沈着", "過敏症反応"),
            'V' to listOf("過敏症反応", "局所反応", "注射部位反応", "発熱", "倦怠感", "頭痛", "筋肉痛", "関節痛"),
        )
}

object AdverseReactionSeedBucketRepository {
    fun get(atcInitial: Char): List<String> =
        AdverseReactionSeedBuckets.byAtcInitial[atcInitial.uppercaseChar()]
            ?: error("Unknown ATC initial '$atcInitial'")
}
