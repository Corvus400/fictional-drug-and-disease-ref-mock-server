package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.PrevalenceUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DiseaseEnumCoverageTest {
    private val generator: DiseaseGenerator = buildFreshGenerator()
    private val diseases: List<Disease> =
        DiseaseBlueprintFactory.build().map { blueprint -> generator.generate(blueprint = blueprint) }

    @Test
    fun `MedicalDepartment 全 16 値が primary department に出現する`() {
        val primaries: Set<MedicalDepartment> = diseases.map { it.medicalDepartment.first() }.toSet()
        assertEquals(expected = MedicalDepartment.entries.toSet(), actual = primaries)
    }

    @Test
    fun `PrevalenceUnit 全 3 値が出現する`() {
        val used: Set<PrevalenceUnit> = diseases
            .mapNotNull { it.epidemiology?.prevalence?.unit }
            .toSet()
        assertEquals(expected = PrevalenceUnit.entries.toSet(), actual = used)
    }

    @Test
    fun `CHAPTER_XV 妊娠分娩は sexRatio が女性優位 maleRatio 0 femaleRatio 1 以上`() {
        val ch15Diseases: List<Disease> = diseases.filter { it.icd10Chapter == Icd10Chapter.CHAPTER_XV }
        assertTrue(actual = ch15Diseases.isNotEmpty(), message = "CHAPTER_XV の疾患が存在しません")
        ch15Diseases.forEach { disease ->
            val sexRatio = disease.epidemiology?.sexRatio
            assertNotNull(actual = sexRatio, message = "${disease.id}: CHAPTER_XV は sexRatio 非 null 必須")
            assertEquals(expected = 0, actual = sexRatio.maleRatio, message = "${disease.id}: maleRatio は 0")
            assertTrue(
                actual = sexRatio.femaleRatio >= 1,
                message = "${disease.id}: femaleRatio は 1 以上 (got ${sexRatio.femaleRatio})",
            )
        }
    }

    private companion object {
        fun buildFreshGenerator(): DiseaseGenerator =
            DiseaseGenerator(
                adapter = FixmergeNameAdapter(),
                placeholderDictionary = DiseasePlaceholderDictionary(),
            )
    }
}
