package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

object EffectSeedBuckets {
    val byAtcInitial: Map<Char, List<String>> =
        mapOf(
            'A' to listOf("胃酸分泌抑制作用", "消化管運動調整作用", "制吐作用", "血糖降下作用", "整腸作用", "食欲調整作用"),
            'B' to listOf("抗凝固作用", "血小板凝集抑制作用", "線溶促進作用", "造血促進作用", "血液粘度低下作用", "鉄補充作用"),
            'C' to listOf("血圧降下作用", "心拍数調整作用", "利尿作用", "強心作用", "抗不整脈作用", "末梢血管拡張作用"),
            'D' to listOf("角質溶解作用", "抗炎症作用", "抗真菌作用", "抗掻痒作用", "皮膚保護作用", "紫外線防護作用"),
            'G' to listOf("ホルモン補充作用", "排卵抑制作用", "排尿改善作用", "子宮収縮抑制作用", "性機能改善作用", "月経調整作用"),
            'H' to listOf("ホルモン補充作用", "内分泌調整作用", "代謝促進作用", "成長促進作用", "副腎皮質ホルモン補充作用", "甲状腺機能調整作用"),
            'J' to listOf("抗菌作用", "抗ウイルス作用", "抗結核作用", "抗真菌作用", "殺菌作用", "静菌作用"),
            'L' to listOf("抗腫瘍作用", "免疫調整作用", "細胞増殖抑制作用", "アポトーシス誘導作用", "血管新生抑制作用", "免疫抑制作用"),
            'M' to listOf("抗炎症作用", "鎮痛作用", "筋弛緩作用", "関節保護作用", "骨形成促進作用", "尿酸排泄促進作用"),
            'N' to listOf("抗うつ作用", "抗不安作用", "鎮静作用", "抗痙攣作用", "抗パーキンソン作用", "鎮痛作用"),
            'P' to listOf("抗原虫作用", "抗蠕虫作用", "抗マラリア作用", "抗トキソプラズマ作用", "殺虫作用", "駆虫作用"),
            'R' to listOf("気管支拡張作用", "抗炎症作用", "抗アレルギー作用", "鎮咳作用", "去痰作用", "粘液調整作用"),
            'S' to listOf("眼圧降下作用", "散瞳作用", "縮瞳作用", "抗炎症作用", "涙液分泌促進作用", "充血改善作用"),
            'V' to listOf("解毒作用", "造影作用", "中和作用", "免疫賦活作用", "検査支援作用", "補助診断作用"),
        )
}

object EffectSeedBucketRepository {
    fun get(atcInitial: Char): List<String> =
        EffectSeedBuckets.byAtcInitial[atcInitial.uppercaseChar()]
            ?: error("Unknown ATC initial '$atcInitial'")
}
