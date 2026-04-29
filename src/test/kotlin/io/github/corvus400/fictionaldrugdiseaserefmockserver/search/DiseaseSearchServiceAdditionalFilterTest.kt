package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
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
}
