package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.Exam
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentSection

internal val DISEASE_FINAL_OVERRIDES: Map<String, (Disease) -> Disease> =
    mapOf("disease_0079" to ::witchFactorSyndromeOverride)

private fun witchFactorSyndromeOverride(generated: Disease): Disease =
    generated.copy(
        name = "魔女因子症候群",
        nameKana = "マジョインシショウコウグン",
        nameEnglish = "Witch Factor Syndrome (fictional)",
        chronicity = Chronicity.CHRONIC,
        infectious = false,
        synonyms = listOf("魔女化症", "ウィッチファクター症候群"),
        summary = "魔女因子の活性化により不死性獲得と肉体・精神の不可逆変質を来す症候群 (架空)",
        etiology = "魔女因子の暴走的活性化と外部干渉により発症する (架空)",
        symptoms =
        SymptomInfo(
            mainSymptoms =
            listOf(
                "不死性の獲得 (架空)",
                "肉体変質 (架空)",
                "精神変容 (架空)",
            ),
            associatedSymptoms =
            listOf(
                "魔女因子反応の増強 (架空)",
                "外傷治癒の異常促進 (架空)",
            ),
            onsetPattern = OnsetPattern.CHRONIC,
        ),
        diagnosticCriteria =
        DiagnosticCriteriaInfo(
            required =
            listOf(
                "魔女因子活性化の持続を認める (架空)",
                "通常治癒過程を逸脱した再生反応を認める (架空)",
            ),
            supporting =
            listOf(
                "トレデキム曝露に対する特異的脆弱性を認める (架空)",
            ),
            notes = "現実の診断基準ではなく架空 fixture の分類である (架空)",
        ),
        requiredExams =
        listOf(
            Exam(
                name = "魔女因子問診",
                category = ExamCategory.INTERVIEW,
                typicalFinding = "不死性獲得と精神変容の経過を聴取する (架空)",
            ),
            Exam(
                name = "魔女因子血中反応",
                category = ExamCategory.BLOOD_TEST,
                typicalFinding = "魔女因子活性シグナル陽性 (架空)",
                referenceRange = "陰性 (架空)",
            ),
            Exam(
                name = "因子結晶化画像検査",
                category = ExamCategory.IMAGING,
                typicalFinding = "眼球および創部周辺の結晶化兆候 (架空)",
            ),
        ),
        differentialDiagnoses =
        listOf(
            "急性薬物中毒 (架空)",
            "不死性獲得を伴わない魔力過敏症 (架空)",
        ),
        complications =
        listOf(
            "不可逆的精神変容 (架空)",
            "外傷治癒異常 (架空)",
        ),
        treatments =
        TreatmentInfo(
            nonPharmacological =
            listOf(
                TreatmentSection(
                    heading = "隔離管理",
                    items = listOf("魔女因子反応の監視", "外部刺激の制限"),
                    description = "安全確保を優先し、治療ではなく封じ込めを目的とする (架空)",
                ),
            ),
        ),
        prognosis = "発症後の変質は不可逆で予後不良とされ、トレデキムへの脆弱性を伴う (架空)",
        prevention = listOf("魔女因子曝露の回避 (架空)", "活性化兆候の早期隔離 (架空)"),
    )
