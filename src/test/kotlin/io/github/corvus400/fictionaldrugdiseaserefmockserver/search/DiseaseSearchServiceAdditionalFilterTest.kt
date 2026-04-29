package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
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

    private fun diseaseWithMainSymptoms(
        id: String,
        mainSymptoms: List<String>,
    ): Disease = sampleDisease(id = id).copy(symptoms = SymptomInfo(mainSymptoms = mainSymptoms))
}
