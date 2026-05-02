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

        assertEquals("マジョインシショウコウグン", disease0079.nameKana)
        assertTrue(disease0079.nameEnglish?.contains("Witch Factor") == true)
        assertTrue(disease0079.synonyms.any { synonym -> synonym.contains("魔女化症") })
        assertTrue(disease0079.summary.contains("魔女因子"))
        assertEquals(false, disease0079.infectious)
        assertEquals(Chronicity.CHRONIC, disease0079.chronicity)
        assertTrue(disease0079.symptoms.mainSymptoms.size >= 3)
        assertTrue(disease0079.requiredExams.isNotEmpty())
        assertTrue(disease0079.prognosis?.contains("不可逆") == true)
        assertTrue(disease0079.summary.endsWith("(架空)"))
        assertTrue(DiseaseFixtureValidator.validate(diseases = diseases).isEmpty())
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

        assertEquals(0, sexRatio?.maleRatio)
        assertEquals(1, sexRatio?.femaleRatio)
        assertTrue(sexRatio?.note?.contains("少女") == true)
    }

    @Test
    fun `witch factor syndrome should list stress and trauma as risk factors`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val riskFactors = disease0079.epidemiology?.riskFactors.orEmpty()

        assertTrue(riskFactors.any { it.contains("ストレス") })
        assertTrue(riskFactors.any { it.contains("トラウマ") })
    }

    @Test
    fun `witch factor syndrome should manifest at least five witch-specific main symptoms`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val mainSymptoms = disease0079.symptoms.mainSymptoms

        assertTrue(mainSymptoms.size >= 5)
        assertTrue(mainSymptoms.any { it.contains("殺人衝動") })
        assertTrue(mainSymptoms.any { it.contains("爪") })
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

        assertEquals("身体魔女化所見観察", imagingExam.name)
        assertEquals("爪", imagingExam.typicalFinding.substringBefore("の異常伸長"))
        assertTrue(imagingExam.typicalFinding.contains("皮膚亀裂"))
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

        assertEquals(2, disease0079.differentialDiagnoses.size)
        assertTrue(differentialText.contains("原初の魔女"))
        assertTrue(differentialText.contains("なれはて"))
    }

    @Test
    fun `witch factor syndrome complications should include full witch transformation and witch husk`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val complicationsText = disease0079.complications.joinToString()

        assertTrue(complicationsText.contains("完全魔女化"))
        assertTrue(complicationsText.contains("なれはて化"))
    }

    @Test
    fun `witch factor syndrome treatment should offer three non pharmacological strategies`() {
        val disease0079 = generateDiseases().first { it.id == "disease_0079" }
        val headings = disease0079.treatments.nonPharmacological.map { it.heading }

        assertEquals(listOf("ストレス軽減・精神安定", "魔女因子除去仮説", "封じ込め管理"), headings)
    }

    @Test
    fun `insomnia disease should be overridden`() {
        val insomnia = generateDiseases().first { it.id == "disease_0022" }

        assertEquals("不眠症", insomnia.name)
    }

    @Test
    fun `insomnia disease should reflect insomnia clinical details`() {
        val insomnia = generateDiseases().first { it.id == "disease_0022" }

        assertEquals("フミンショウ", insomnia.nameKana)
        assertEquals("Insomnia (fictional)", insomnia.nameEnglish)
        assertTrue(insomnia.summary.contains("睡眠"))
        assertTrue(insomnia.symptoms.mainSymptoms.size >= 2)
        assertTrue(insomnia.requiredExams.isNotEmpty())
        assertEquals(Chronicity.CHRONIC, insomnia.chronicity)
        assertEquals(false, insomnia.infectious)
        assertTrue(insomnia.summary.endsWith("(架空)"))
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
