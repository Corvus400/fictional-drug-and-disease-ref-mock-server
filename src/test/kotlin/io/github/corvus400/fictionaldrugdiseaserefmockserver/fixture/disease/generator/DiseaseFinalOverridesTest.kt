package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation.DiseaseFixtureValidator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DiseaseFinalOverridesTest {
    @Test
    fun `chapter XXII disease should be overridden by DISEASE_FINAL_OVERRIDES`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }

        assertEquals("魔女因子症候群", disease0079.name)
    }

    @Test
    fun `chapter XXII disease should reflect witch factor syndrome details`() {
        val diseases = generateDiseases()
        val disease0079 = diseases.first { it.id == "disease_0079" }

        val violations = listOfNotNull(
            "nameKana must be マジョインシショウコウグン but was ${disease0079.nameKana}"
                .takeUnless { disease0079.nameKana == "マジョインシショウコウグン" },
            "nameEnglish must contain Witch Factor".takeUnless {
                disease0079.nameEnglish?.contains("Witch Factor") == true
            },
            "synonyms must contain 魔女化症"
                .takeUnless { disease0079.synonyms.any { synonym -> synonym.contains("魔女化症") } },
            "summary must contain 魔女因子".takeUnless { disease0079.summary.contains("魔女因子") },
            "infectious must be false".takeUnless { !disease0079.infectious },
            "chronicity must be CHRONIC but was ${disease0079.chronicity}"
                .takeUnless { disease0079.chronicity == Chronicity.CHRONIC },
            "mainSymptoms size must be >= 3 but was ${disease0079.symptoms.mainSymptoms.size}"
                .takeUnless { disease0079.symptoms.mainSymptoms.size >= 3 },
            "requiredExams must be non-empty".takeUnless { disease0079.requiredExams.isNotEmpty() },
            "prognosis must contain 不可逆".takeUnless { disease0079.prognosis?.contains("不可逆") == true },
            "summary must end with (架空)".takeUnless { disease0079.summary.endsWith("(架空)") },
            "DiseaseFixtureValidator must pass but got ${DiseaseFixtureValidator.validate(diseases = diseases)}"
                .takeUnless { DiseaseFixtureValidator.validate(diseases = diseases).isEmpty() },
        )

        assertTrue(
            actual = violations.isEmpty(),
            message = "disease_0079 narrative violations: $violations",
        )
    }

    @Test
    fun `non chapter XXII disease should not be affected by witch factor override`() {
        val disease0078 = generateDiseases().first { it.id == "disease_0078" }

        assertNotEquals("魔女因子症候群", disease0078.name)
    }

    @Test
    fun `witch factor syndrome disease should reference drug_0080 only`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }

        assertEquals(listOf("drug_0080"), disease0079.relatedDrugIds)
    }

    @Test
    fun `witch factor syndrome should have no related diseases`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }

        assertEquals(emptyList(), disease0079.relatedDiseaseIds)
    }

    @Test
    fun `witch factor syndrome should be assigned to psychiatry and dermatology`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }

        assertEquals(
            listOf(MedicalDepartment.PSYCHIATRY, MedicalDepartment.DERMATOLOGY),
            disease0079.medicalDepartment,
        )
    }

    @Test
    fun `witch factor syndrome should onset at 15 years or later`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }

        assertEquals(15, disease0079.epidemiology?.onsetAgeRange?.minAgeYears)
    }

    @Test
    fun `witch factor syndrome should affect only female patients`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val sexRatio = disease0079.epidemiology?.sexRatio
        val actual = mapOf(
            "maleRatio" to sexRatio?.maleRatio,
            "femaleRatio" to sexRatio?.femaleRatio,
            "noteContains少女" to (sexRatio?.note?.contains("少女") == true),
        )

        assertEquals(
            expected = mapOf("maleRatio" to 0, "femaleRatio" to 1, "noteContains少女" to true),
            actual = actual,
        )
    }

    @Test
    fun `witch factor syndrome should list stress and trauma as risk factors`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val riskFactors = disease0079.epidemiology?.riskFactors.orEmpty()
        val actual = mapOf(
            "contains stress" to riskFactors.any { it.contains("ストレス") },
            "contains trauma" to riskFactors.any { it.contains("トラウマ") },
        )

        assertEquals(
            expected = actual.keys.associateWith { true },
            actual = actual,
        )
    }

    @Test
    fun `witch factor syndrome should manifest at least five witch-specific main symptoms`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val mainSymptoms = disease0079.symptoms.mainSymptoms
        val actual = mapOf(
            "sizeAtLeast5" to (mainSymptoms.size >= 5),
            "contains殺人衝動" to mainSymptoms.any { it.contains("殺人衝動") },
            "contains爪" to mainSymptoms.any { it.contains("爪") },
        )

        assertEquals(
            expected = actual.keys.associateWith { true },
            actual = actual,
        )
    }

    @Test
    fun `witch factor syndrome should be classified as subacute onset`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }

        assertEquals(OnsetPattern.SUBACUTE, disease0079.symptoms.onsetPattern)
    }

    @Test
    fun `witch factor syndrome associated symptoms should include witch husk transformation`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }

        assertTrue(disease0079.symptoms.associatedSymptoms.any { it.contains("なれはて") })
    }

    @Test
    fun `witch factor syndrome required criteria should reference screening and witch factor positivity`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val requiredText = disease0079.diagnosticCriteria.required.joinToString()

        assertTrue(requiredText.contains("全国検査"))
        assertTrue(requiredText.contains("魔女因子高値"))
    }

    @Test
    fun `witch factor syndrome imaging exam should observe physical witch transformation findings`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val imagingExam = disease0079.requiredExams[2]
        val actual = mapOf(
            "name" to imagingExam.name,
            "typicalFindingPrefix" to imagingExam.typicalFinding.substringBefore("の異常伸長"),
            "typicalFindingContainsSkinCrack" to imagingExam.typicalFinding.contains("皮膚亀裂"),
        )

        assertEquals(
            expected = mapOf(
                "name" to "身体魔女化所見観察",
                "typicalFindingPrefix" to "爪",
                "typicalFindingContainsSkinCrack" to true,
            ),
            actual = actual,
        )
    }

    @Test
    fun `witch factor syndrome should grade five progression stages ending in witch husk`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val gradeLabels = disease0079.severityGrading?.grades.orEmpty().map { it.label }

        assertEquals(listOf("潜伏・保因", "活性化", "魔女化進行", "完全魔女化", "なれはて"), gradeLabels)
    }

    @Test
    fun `witch factor syndrome should be differentiated from primal witch and witch husk endpoint`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val differentialText = disease0079.differentialDiagnoses.joinToString()
        val actual = mapOf(
            "size" to disease0079.differentialDiagnoses.size,
            "containsPrimalWitch" to differentialText.contains("原初の魔女"),
            "containsWitchHusk" to differentialText.contains("なれはて"),
        )

        assertEquals(
            expected = mapOf("size" to 2, "containsPrimalWitch" to true, "containsWitchHusk" to true),
            actual = actual,
        )
    }

    @Test
    fun `witch factor syndrome complications should include full witch transformation and witch husk`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val complicationsText = disease0079.complications.joinToString()
        val actual = mapOf(
            "containsFullWitchTransformation" to complicationsText.contains("完全魔女化"),
            "containsWitchHusk" to complicationsText.contains("なれはて化"),
        )

        assertEquals(
            expected = actual.keys.associateWith { true },
            actual = actual,
        )
    }

    @Test
    fun `witch factor syndrome treatment should offer three non pharmacological strategies`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val headings = disease0079.treatments.nonPharmacological.map { it.heading }

        assertEquals(listOf("ストレス軽減・精神安定", "魔女因子除去仮説", "封じ込め管理"), headings)
    }

    @Test
    fun `witch factor syndrome prognosis should describe irreversibility tempered by witch factor depletion`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val prognosis = disease0079.prognosis.orEmpty()
        val actual = mapOf(
            "containsIrreversible" to prognosis.contains("不可逆"),
            "containsGreatWitchManifestation" to prognosis.contains("大魔女顕現"),
            "containsWitchFactorDepletion" to prognosis.contains("魔女因子喪失"),
        )

        assertEquals(
            expected = actual.keys.associateWith { true },
            actual = actual,
        )
    }

    @Test
    fun `witch factor syndrome prevention should target stress avoidance and pre 15 mental stability`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val preventionText = disease0079.prevention.joinToString()
        val actual = mapOf(
            "containsStress" to preventionText.contains("ストレス"),
            "containsAge15" to preventionText.contains("15 歳"),
        )

        assertEquals(
            expected = actual.keys.associateWith { true },
            actual = actual,
        )
    }

    @Test
    fun `insomnia disease should be overridden`() {
        val insomnia = generateDiseases().first { it.id == "disease_0022" }

        assertEquals("不眠症", insomnia.name)
    }

    @Test
    fun `insomnia disease should reflect insomnia clinical details`() {
        val insomnia = generateDiseases().first { it.id == "disease_0022" }
        val violations = listOfNotNull(
            "nameKana must be フミンショウ but was ${insomnia.nameKana}"
                .takeUnless { insomnia.nameKana == "フミンショウ" },
            "nameEnglish must be Insomnia (fictional) but was ${insomnia.nameEnglish}"
                .takeUnless { insomnia.nameEnglish == "Insomnia (fictional)" },
            "summary must contain 睡眠".takeUnless { insomnia.summary.contains("睡眠") },
            "mainSymptoms size must be >= 2 but was ${insomnia.symptoms.mainSymptoms.size}"
                .takeUnless { insomnia.symptoms.mainSymptoms.size >= 2 },
            "requiredExams must be non-empty".takeUnless { insomnia.requiredExams.isNotEmpty() },
            "chronicity must be CHRONIC but was ${insomnia.chronicity}"
                .takeUnless { insomnia.chronicity == Chronicity.CHRONIC },
            "infectious must be false".takeUnless { !insomnia.infectious },
            "summary must end with (架空)".takeUnless { insomnia.summary.endsWith("(架空)") },
        )

        assertTrue(
            actual = violations.isEmpty(),
            message = "disease_0022 narrative violations: $violations",
        )
    }

    @Test
    fun `insomnia disease should pass DiseaseFixtureValidator`() {
        val diseases = generateDiseases()

        assertTrue(DiseaseFixtureValidator.validate(diseases = diseases).isEmpty())
    }

    @Test
    fun `insomnia disease should reference drug_0089`() {
        val insomnia = generateDiseases().first { it.id == "disease_0022" }

        assertEquals(listOf("drug_0089"), insomnia.relatedDrugIds)
    }

    private fun generateDiseases(): List<Disease> =
        DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        ).generate(blueprints = DiseaseBlueprintFactory.build())
}
