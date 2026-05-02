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
