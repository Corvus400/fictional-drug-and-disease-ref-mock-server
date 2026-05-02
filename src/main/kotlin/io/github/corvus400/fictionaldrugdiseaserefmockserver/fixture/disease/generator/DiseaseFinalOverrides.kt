package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.PrevalenceUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.EpidemiologyInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.Exam
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.OnsetAgeRange
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.Prevalence
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SexDistribution
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentSection

internal val DISEASE_FINAL_OVERRIDES: Map<String, (Disease) -> Disease> =
    mapOf(
        "disease_0079" to ::witchFactorSyndromeOverride,
        "disease_0022" to ::insomniaOverride,
    )

private fun insomniaOverride(generated: Disease): Disease =
    generated.copy(
        name = "不眠症",
        nameKana = "フミンショウ",
        nameEnglish = "Insomnia (fictional)",
        chronicity = Chronicity.CHRONIC,
        infectious = false,
        synonyms = listOf("睡眠障害（不眠型）", "インソムニア"),
        summary = "入眠困難、中途覚醒、早朝覚醒又は睡眠の質低下が持続し、日中機能障害を伴う睡眠障害 (架空)",
        etiology = "心理的要因、ストレス、生活習慣、身体疾患及び睡眠環境の乱れが複合して発症する (架空)",
        symptoms =
        SymptomInfo(
            mainSymptoms =
            listOf(
                "入眠困難 (架空)",
                "中途覚醒 (架空)",
                "早朝覚醒 (架空)",
                "睡眠の質低下による日中機能障害 (架空)",
            ),
            associatedSymptoms =
            listOf(
                "日中の眠気 (架空)",
                "集中力低下 (架空)",
                "易刺激性 (架空)",
            ),
            onsetPattern = OnsetPattern.CHRONIC,
        ),
        diagnosticCriteria =
        DiagnosticCriteriaInfo(
            required =
            listOf(
                "入眠困難、中途覚醒又は早朝覚醒が週 3 晩以上みられる (架空)",
                "症状が 3 ヶ月以上持続し日中機能障害を伴う (架空)",
            ),
            supporting =
            listOf(
                "睡眠機会が十分にあるにもかかわらず睡眠困難が持続する (架空)",
                "薬物、身体疾患又は他の睡眠障害だけでは説明できない (架空)",
            ),
            notes = "DSM-5 及び ICD-10 の不眠症診断基準を参考にした架空 fixture である (架空)",
        ),
        requiredExams =
        listOf(
            Exam(
                name = "睡眠歴問診",
                category = ExamCategory.INTERVIEW,
                typicalFinding = "睡眠時間、入眠潜時、中途覚醒及び日中機能障害を聴取する (架空)",
            ),
            Exam(
                name = "睡眠質問票",
                category = ExamCategory.INTERVIEW,
                typicalFinding = "睡眠の質低下と生活への支障を評価する (架空)",
                referenceRange = "支障なし (架空)",
            ),
            Exam(
                name = "睡眠日誌",
                category = ExamCategory.PHYSIOLOGICAL,
                typicalFinding = "就床時刻、覚醒回数、睡眠効率の低下を確認する (架空)",
            ),
        ),
        differentialDiagnoses =
        listOf(
            "睡眠時無呼吸症候群 (架空)",
            "むずむず脚症候群 (架空)",
            "うつ病 (架空)",
        ),
        complications =
        listOf(
            "日中の認知機能低下 (架空)",
            "事故リスク増大 (架空)",
            "心血管疾患リスク増大 (架空)",
        ),
        treatments =
        TreatmentInfo(
            nonPharmacological =
            listOf(
                TreatmentSection(
                    heading = "睡眠衛生指導",
                    items = listOf("就床時刻の固定 (架空)", "カフェイン摂取制限 (架空)", "寝室環境の調整 (架空)"),
                    description = "睡眠を妨げる生活習慣と環境要因を整える (架空)",
                ),
                TreatmentSection(
                    heading = "認知行動療法",
                    items = listOf("刺激制御法 (架空)", "睡眠制限法 (架空)", "認知再構成 (架空)"),
                    description = "不眠を維持する認知と行動パターンを修正する (架空)",
                ),
                TreatmentSection(
                    heading = "環境調整",
                    items = listOf("光環境調整 (架空)", "騒音対策 (架空)", "就寝前スクリーン制限 (架空)"),
                    description = "睡眠の質を妨げる外的刺激を減らす (架空)",
                ),
            ),
        ),
        prognosis = "適切な睡眠衛生指導と認知行動療法で改善が見込まれるが、長期化すると慢性化する (架空)",
        prevention =
        listOf(
            "規則的な睡眠覚醒リズムの維持 (架空)",
            "就寝前の刺激物と強い光の回避 (架空)",
            "ストレス管理と適度な運動 (架空)",
        ),
        relatedDrugIds = listOf("drug_0089"),
    )

private fun witchFactorSyndromeOverride(generated: Disease): Disease =
    generated.copy(
        name = "魔女因子症候群",
        nameKana = "マジョインシショウコウグン",
        nameEnglish = "Witch Factor Syndrome (fictional)",
        medicalDepartment = listOf(MedicalDepartment.PSYCHIATRY, MedicalDepartment.DERMATOLOGY),
        chronicity = Chronicity.CHRONIC,
        infectious = false,
        synonyms = listOf("魔女化症", "ウィッチファクター症候群"),
        summary = "魔女因子の活性化により精神変容、肉体異形化、不死性獲得を経てなれはて化に至る進行性呪性疾患 (架空)",
        epidemiology =
        EpidemiologyInfo(
            prevalence =
            Prevalence(
                rate = null,
                denominator = null,
                unit = PrevalenceUnit.PER_POPULATION,
                label = "少女人口の極小比率に魔女因子高値を検出 (架空)",
            ),
            onsetAgeRange =
            OnsetAgeRange(
                minAgeYears = 15,
                maxAgeYears = null,
                label = "15 歳以降に急性魔女化リスクが急増 (架空)",
            ),
            sexRatio =
            SexDistribution(
                maleRatio = 0,
                femaleRatio = 1,
                note = "患者は全員少女 (架空)",
            ),
            riskFactors =
            listOf(
                "強いストレス (架空)",
                "精神衰弱 (架空)",
                "トラウマ刺激 (架空)",
                "悪意・不信感 (架空)",
                "処刑等の急性苦痛 (架空)",
            ),
        ),
        etiology = "大魔女が自らの存在・魂を変換して放った魔女因子が少女に潜伏し、強いストレス・トラウマ刺激・悪意の影響で活性化して魔女化が進行する (架空)",
        symptoms =
        SymptomInfo(
            mainSymptoms =
            listOf(
                "殺人衝動・凶暴化 (架空)",
                "爪の異常伸長 (架空)",
                "顔面皮膚亀裂と身体異形化 (架空)",
                "全身の激痛 (架空)",
                "不死性・再生性の獲得 (架空)",
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
        relatedDrugIds = listOf("drug_0080"),
        relatedDiseaseIds = emptyList(),
    )
