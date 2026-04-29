package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.Exam
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.PharmaTreatment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.sampleDisease
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseSearchServiceAdditionalFilterTest {
    @Test
    fun `applyAdditionalFilters with symptomKeyword='X' returns only items whose symptoms mainSymptoms contain X`() {
        val items =
            listOf(
                diseaseWithMainSymptoms(id = "disease_0001", mainSymptoms = listOf("X症候群")),
                diseaseWithMainSymptoms(id = "disease_0002", mainSymptoms = listOf("無関係な症状")),
                diseaseWithMainSymptoms(
                    id = "disease_0003",
                    mainSymptoms = listOf("別症状", "重度のX反応"),
                ),
                diseaseWithMainSymptoms(id = "disease_0004", mainSymptoms = listOf("頭痛")),
            )
        val result =
            DiseaseSearchService.applyAdditionalFilters(
                items = items,
                symptomKeyword = "X",
            )
        assertEquals(listOf("disease_0001", "disease_0003"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters with onsetPatterns=ACUTE keeps only items whose onsetPattern == ACUTE`() {
        val items =
            listOf(
                diseaseWithOnsetPattern(id = "disease_0001", onsetPattern = OnsetPattern.ACUTE),
                diseaseWithOnsetPattern(id = "disease_0002", onsetPattern = OnsetPattern.CHRONIC),
                diseaseWithOnsetPattern(id = "disease_0003", onsetPattern = OnsetPattern.ACUTE),
                diseaseWithOnsetPattern(id = "disease_0004", onsetPattern = null),
            )
        val result =
            DiseaseSearchService.applyAdditionalFilters(
                items = items,
                onsetPatterns = listOf(OnsetPattern.ACUTE),
            )
        assertEquals(listOf("disease_0001", "disease_0003"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters with onsetPatterns=ACUTE,CHRONIC keeps items with onsetPattern ACUTE or CHRONIC`() {
        val items =
            listOf(
                diseaseWithOnsetPattern(id = "disease_0001", onsetPattern = OnsetPattern.ACUTE),
                diseaseWithOnsetPattern(id = "disease_0002", onsetPattern = OnsetPattern.CHRONIC),
                diseaseWithOnsetPattern(id = "disease_0003", onsetPattern = OnsetPattern.SUBACUTE),
                diseaseWithOnsetPattern(id = "disease_0004", onsetPattern = OnsetPattern.INTERMITTENT),
                diseaseWithOnsetPattern(id = "disease_0005", onsetPattern = OnsetPattern.CHRONIC),
                diseaseWithOnsetPattern(id = "disease_0006", onsetPattern = null),
            )
        val result =
            DiseaseSearchService.applyAdditionalFilters(
                items = items,
                onsetPatterns = listOf(OnsetPattern.ACUTE, OnsetPattern.CHRONIC),
            )
        assertEquals(listOf("disease_0001", "disease_0002", "disease_0005"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters with examCategories=IMAGING keeps only items whose requiredExams contain IMAGING`() {
        val items =
            listOf(
                diseaseWithExamCategories(
                    id = "disease_0001",
                    categories = listOf(ExamCategory.IMAGING),
                ),
                diseaseWithExamCategories(
                    id = "disease_0002",
                    categories = listOf(ExamCategory.BLOOD_TEST),
                ),
                diseaseWithExamCategories(
                    id = "disease_0003",
                    categories = listOf(ExamCategory.PHYSIOLOGICAL, ExamCategory.IMAGING),
                ),
                diseaseWithExamCategories(
                    id = "disease_0004",
                    categories = listOf(ExamCategory.INTERVIEW),
                ),
            )
        val result =
            DiseaseSearchService.applyAdditionalFilters(
                items = items,
                examCategories = listOf(ExamCategory.IMAGING),
            )
        assertEquals(listOf("disease_0001", "disease_0003"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters with examCategories=IMAGING,BLOOD_TEST applies OR to requiredExams`() {
        val items =
            listOf(
                diseaseWithExamCategories(
                    id = "disease_0001",
                    categories = listOf(ExamCategory.IMAGING),
                ),
                diseaseWithExamCategories(
                    id = "disease_0002",
                    categories = listOf(ExamCategory.BLOOD_TEST),
                ),
                diseaseWithExamCategories(
                    id = "disease_0003",
                    categories = listOf(ExamCategory.PHYSIOLOGICAL, ExamCategory.IMAGING),
                ),
                diseaseWithExamCategories(
                    id = "disease_0004",
                    categories = listOf(ExamCategory.INTERVIEW),
                ),
                diseaseWithExamCategories(
                    id = "disease_0005",
                    categories = listOf(ExamCategory.PATHOLOGY),
                ),
            )
        val result =
            DiseaseSearchService.applyAdditionalFilters(
                items = items,
                examCategories = listOf(ExamCategory.IMAGING, ExamCategory.BLOOD_TEST),
            )
        assertEquals(listOf("disease_0001", "disease_0002", "disease_0003"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters with hasPharmacologicalTreatment=true keeps only pharmacological treatments`() {
        val items =
            listOf(
                diseaseWithPharmacologicalTreatment(id = "disease_0001"),
                diseaseWithoutPharmacologicalTreatment(id = "disease_0002"),
                diseaseWithPharmacologicalTreatment(id = "disease_0003"),
            )
        val result =
            DiseaseSearchService.applyAdditionalFilters(
                items = items,
                hasPharmacologicalTreatment = true,
            )
        assertEquals(listOf("disease_0001", "disease_0003"), result.map { it.id })
    }

    @Test
    fun `applyAdditionalFilters with hasPharmacologicalTreatment=false keeps only empty pharmacological treatments`() {
        val items =
            listOf(
                diseaseWithPharmacologicalTreatment(id = "disease_0001"),
                diseaseWithoutPharmacologicalTreatment(id = "disease_0002"),
                diseaseWithPharmacologicalTreatment(id = "disease_0003"),
                diseaseWithoutPharmacologicalTreatment(id = "disease_0004"),
            )
        val result =
            DiseaseSearchService.applyAdditionalFilters(
                items = items,
                hasPharmacologicalTreatment = false,
            )
        assertEquals(listOf("disease_0002", "disease_0004"), result.map { it.id })
    }

    private fun diseaseWithMainSymptoms(
        id: String,
        mainSymptoms: List<String>,
    ): Disease = sampleDisease(id = id).copy(symptoms = SymptomInfo(mainSymptoms = mainSymptoms))

    private fun diseaseWithOnsetPattern(
        id: String,
        onsetPattern: OnsetPattern?,
    ): Disease =
        sampleDisease(id = id).copy(
            symptoms = SymptomInfo(mainSymptoms = listOf("頭痛"), onsetPattern = onsetPattern),
        )

    private fun diseaseWithExamCategories(
        id: String,
        categories: List<ExamCategory>,
    ): Disease =
        sampleDisease(id = id).copy(
            requiredExams =
            categories.mapIndexed { index, category ->
                Exam(
                    name = "検査$index",
                    category = category,
                    typicalFinding = "所見$index",
                )
            },
        )

    private fun diseaseWithPharmacologicalTreatment(id: String): Disease =
        sampleDisease(id = id).copy(
            treatments = TreatmentInfo(
                pharmacological = listOf(
                    PharmaTreatment(
                        drugCategory = "抗炎症薬",
                        drugIds = listOf("drug_0001"),
                        indication = "炎症抑制",
                        notes = "症状に応じて調整",
                    ),
                ),
            ),
        )

    private fun diseaseWithoutPharmacologicalTreatment(id: String): Disease =
        sampleDisease(id = id).copy(treatments = TreatmentInfo())
}
