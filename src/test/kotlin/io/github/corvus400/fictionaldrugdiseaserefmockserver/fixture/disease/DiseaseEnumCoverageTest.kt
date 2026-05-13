package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.PrevalenceUnit
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `requiredExams primary が icd10Chapter ベースで決定論的に割り当てられる`() {
        val violations: List<String> = diseases.mapNotNull { disease ->
            val expected = expectedPrimaryExamCategoryFor(chapter = disease.icd10Chapter)
            val actual = disease.requiredExams.first().category
            if (actual != expected) {
                "${disease.id} (chapter=${disease.icd10Chapter}): expected=$expected actual=$actual"
            } else {
                null
            }
        }
        assertTrue(actual = violations.isEmpty(), message = "primary 不一致 ${violations.size} 件: $violations")
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

        val violations = buildList {
            if (ch15Diseases.isEmpty()) {
                add("CHAPTER_XV の疾患が存在しません")
            }
            ch15Diseases.forEach { disease ->
                val sexRatio = disease.epidemiology?.sexRatio
                if (sexRatio == null) {
                    add("${disease.id}: CHAPTER_XV は sexRatio 非 null 必須")
                } else {
                    if (sexRatio.maleRatio != 0) {
                        add("${disease.id}: maleRatio は 0 (got ${sexRatio.maleRatio})")
                    }
                    if (sexRatio.femaleRatio < 1) {
                        add("${disease.id}: femaleRatio は 1 以上 (got ${sexRatio.femaleRatio})")
                    }
                }
            }
        }

        assertTrue(actual = violations.isEmpty(), message = "CHAPTER_XV sexRatio violations: $violations")
    }

    private companion object {
        fun buildFreshGenerator(): DiseaseGenerator =
            DiseaseGenerator(
                adapter = FixmergeNameAdapter(),
                placeholderDictionary = DiseasePlaceholderDictionary(),
            )

        fun expectedPrimaryExamCategoryFor(chapter: Icd10Chapter): ExamCategory = when (chapter) {
            Icd10Chapter.CHAPTER_I -> ExamCategory.BLOOD_TEST
            Icd10Chapter.CHAPTER_II -> ExamCategory.PATHOLOGY
            Icd10Chapter.CHAPTER_III -> ExamCategory.BLOOD_TEST
            Icd10Chapter.CHAPTER_IV -> ExamCategory.BLOOD_TEST
            Icd10Chapter.CHAPTER_V -> ExamCategory.INTERVIEW
            Icd10Chapter.CHAPTER_VI -> ExamCategory.PHYSIOLOGICAL
            Icd10Chapter.CHAPTER_VII -> ExamCategory.PHYSIOLOGICAL
            Icd10Chapter.CHAPTER_VIII -> ExamCategory.PHYSIOLOGICAL
            Icd10Chapter.CHAPTER_IX -> ExamCategory.IMAGING
            Icd10Chapter.CHAPTER_X -> ExamCategory.IMAGING
            Icd10Chapter.CHAPTER_XI -> ExamCategory.IMAGING
            Icd10Chapter.CHAPTER_XII -> ExamCategory.PATHOLOGY
            Icd10Chapter.CHAPTER_XIII -> ExamCategory.IMAGING
            Icd10Chapter.CHAPTER_XIV -> ExamCategory.IMAGING
            Icd10Chapter.CHAPTER_XV -> ExamCategory.IMAGING
            Icd10Chapter.CHAPTER_XVI -> ExamCategory.IMAGING
            Icd10Chapter.CHAPTER_XVII -> ExamCategory.IMAGING
            Icd10Chapter.CHAPTER_XVIII -> ExamCategory.INTERVIEW
            Icd10Chapter.CHAPTER_XIX -> ExamCategory.IMAGING
            Icd10Chapter.CHAPTER_XX -> ExamCategory.INTERVIEW
            Icd10Chapter.CHAPTER_XXI -> ExamCategory.INTERVIEW
            Icd10Chapter.CHAPTER_XXII -> ExamCategory.INTERVIEW
        }
    }
}
