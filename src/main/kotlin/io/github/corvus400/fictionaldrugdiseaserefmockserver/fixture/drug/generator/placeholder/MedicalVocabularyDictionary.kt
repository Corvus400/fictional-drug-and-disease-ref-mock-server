package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator

object MedicalVocabularyDictionary {
    fun resolve(
        key: String,
        seed: Long,
    ): String {
        val entries =
            VOCABULARY[key]
                ?: error(
                    "Unknown category-A placeholder key '$key'. " +
                        "MedicalVocabularyDictionary covers only the 29 category-A keys. " +
                        "Other categories (B/C/D) are resolved by DrugPlaceholderDictionary.",
                )
        return ValueRangeGenerator.pickOne(seed = seed, candidates = entries)
    }

    private val VOCABULARY: Map<String, List<String>> =
        mapOf(
            "action" to
                listOf(
                    "阻害",
                    "活性化",
                    "遮断",
                    "部分作動",
                    "拮抗",
                    "アロステリック調節",
                    "脱感作",
                    "増強",
                ),
            "adverseReaction" to
                listOf(
                    "肝機能障害", "間質性肺炎", "無顆粒球症", "Stevens-Johnson 症候群", "血小板減少",
                    "横紋筋融解症", "急性腎障害", "アナフィラキシー", "QT 延長", "低血糖",
                ),
            "ageGroup" to
                listOf("小児", "成人", "高齢者", "乳児", "幼児", "学童", "青年"),
            "comorbidity" to
                listOf(
                    "糖尿病", "高血圧症", "慢性腎臓病", "心不全", "肝硬変", "気管支喘息",
                    "虚血性心疾患", "脂質異常症", "慢性閉塞性肺疾患",
                ),
            "countermeasure" to
                listOf(
                    "対症療法",
                    "輸液管理",
                    "投与中止",
                    "減量",
                    "対応薬投与",
                    "血液透析",
                    "経過観察",
                    "症状モニタリング",
                ),
            "drugCategory" to
                listOf(
                    "NSAIDs", "SSRI", "SNRI", "β 遮断薬", "CYP3A4 阻害薬", "CYP2D6 阻害薬",
                    "スタチン系", "ACE 阻害薬", "ARB", "カルシウム拮抗薬", "PPI", "H2 ブロッカー",
                    "ループ利尿薬", "サイアザイド系利尿薬", "NOAC クラス", "マクロライド系抗菌薬",
                    "キノロン系抗菌薬", "SGLT2 阻害薬", "DPP-4 阻害薬",
                ),
            "effect" to
                listOf(
                    "抗炎症作用", "鎮痛作用", "血糖降下作用", "降圧作用", "抗菌作用", "抗腫瘍作用",
                    "解熱作用", "抗不安作用", "抗凝固作用",
                ),
            "endpoint" to
                listOf("主要評価項目", "副次評価項目", "安全性評価項目", "薬物動態評価項目", "探索的評価項目"),
            "enzyme" to
                listOf("CYP3A4", "CYP2D6", "CYP2C9", "CYP1A2", "UGT1A1", "CYP2C19", "CYP2E1", "CYP2B6"),
            "exam" to
                listOf(
                    "血液検査", "肝機能検査", "腎機能検査", "心電図検査", "尿検査", "胸部 X 線",
                    "超音波検査", "CT 検査", "MRI 検査",
                ),
            "excretionRoute" to
                listOf("腎排泄", "胆汁排泄", "糞便排泄", "呼気排泄", "乳汁排泄"),
            "frequencyBand" to
                listOf("高頻度", "中等度頻度", "低頻度", "稀", "頻度不明"),
            "hepaticLevel" to
                listOf("軽度肝機能障害", "中等度肝機能障害", "重度肝機能障害"),
            "insuranceRule" to
                listOf(
                    "保医発 0301 第 1 号準拠",
                    "保険適用基準 A 群",
                    "特定保険医療材料区分",
                    "長期投与制限対象外",
                    "高額療養費制度対象",
                ),
            "ionizationForm" to
                listOf("分子", "イオン", "解離", "非解離", "部分解離"),
            "labResult" to
                listOf(
                    "AST", "ALT", "γ-GTP", "クレアチニン値", "eGFR", "ヘモグロビン値",
                    "血小板数", "CRP", "血糖値", "HbA1c",
                ),
            "mechanism" to
                listOf(
                    "受容体結合",
                    "酵素阻害",
                    "イオンチャネル修飾",
                    "膜安定化",
                    "シグナル伝達阻害",
                    "代謝経路阻害",
                    "競合的拮抗",
                ),
            "modelName" to
                listOf("炎症モデル", "腫瘍モデル", "高血圧モデル", "糖尿病モデル", "疼痛モデル", "虚血再灌流モデル"),
            "modelType" to
                listOf(
                    "1 コンパートメント",
                    "2 コンパートメント",
                    "非線形",
                    "生理学的薬物動態 (PBPK)",
                    "母集団薬物動態",
                ),
            "pathway" to
                listOf(
                    "シグナル伝達経路",
                    "代謝経路",
                    "分泌経路",
                    "吸収経路",
                    "神経伝達経路",
                    "免疫応答経路",
                ),
            "populationCategory" to
                listOf("妊婦", "授乳婦", "小児", "高齢者", "新生児", "透析患者", "肝機能障害患者"),
            "postMarketingPlan" to
                listOf("使用成績調査", "特定使用成績調査", "市販後臨床試験", "全例調査", "製造販売後データベース調査"),
            "renalLevel" to
                listOf("軽度腎機能障害", "中等度腎機能障害", "重度腎機能障害", "末期腎不全"),
            "route" to
                listOf("経口", "静注", "筋注", "皮下注", "点滴静注", "経鼻", "経皮", "直腸内"),
            "solventPolarity" to
                listOf("エタノール等の有機", "水性", "極性有機", "非極性有機", "アセトン等中極性"),
            "studyDuration" to
                listOf("12 週間", "24 週間", "52 週間", "104 週間", "2 年間", "5 年間"),
            "symptom" to
                listOf(
                    "頭痛", "嘔気", "倦怠感", "発疹", "めまい", "下痢", "腹痛", "食欲不振",
                    "不眠", "動悸", "口渇", "しびれ",
                ),
            "tissueType" to
                listOf("肝細胞", "腎皮質細胞", "心筋細胞", "血管内皮細胞", "神経細胞", "骨格筋細胞"),
            "trainingProgram" to
                listOf("医薬品安全性情報提供", "適正使用研修", "副作用対応研修", "処方監査研修", "臨床試験責任者研修"),
        )
}
