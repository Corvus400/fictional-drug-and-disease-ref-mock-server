package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.sampleDisease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.sampleDiseases
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseSearchServiceTest {
    @Test
    fun `applyKeyword with null keyword returns items unchanged (disease)`() {
        val items = sampleDiseases(n = 3)
        val result =
            DiseaseSearchService.applyKeyword(
                items = items,
                keyword = null,
                match = KeywordMatch.PARTIAL,
                target = DiseaseKeywordTarget.NAME,
            )
        assertEquals(items, result)
    }

    @Test
    fun `applyKeyword with target NAME matches name OR nameKana`() {
        val items =
            listOf(
                disease(id = "disease_0001", name = "高血圧症", nameKana = "コウケツアツショウ"),
                disease(id = "disease_0002", name = "糖尿病", nameKana = "トウニョウビョウ"),
                // カナのみヒット
                disease(id = "disease_0003", name = "別", nameKana = "コウケツ"),
            )
        val result =
            DiseaseSearchService.applyKeyword(
                items = items,
                keyword = "コウケツ",
                match = KeywordMatch.PARTIAL,
                target = DiseaseKeywordTarget.NAME,
            )
        assertEquals(setOf("disease_0001", "disease_0003"), result.map { it.id }.toSet())
    }

    @Test
    fun `applyKeyword with target NAME_ENGLISH matches nameEnglish contains`() {
        val items =
            listOf(
                disease(id = "disease_0001", nameEnglish = "Hypertension"),
                disease(id = "disease_0002", nameEnglish = "Diabetes mellitus"),
            )
        val result =
            DiseaseSearchService.applyKeyword(
                items = items,
                keyword = "Hyper",
                match = KeywordMatch.PARTIAL,
                target = DiseaseKeywordTarget.NAME_ENGLISH,
            )
        assertEquals(listOf("disease_0001"), result.map { it.id })
    }

    @Test
    fun `applyKeyword with target SYNONYMS matches any element of synonyms list`() {
        val items =
            listOf(
                disease(id = "disease_0001", synonyms = listOf("HTN", "高血圧")),
                disease(id = "disease_0002", synonyms = listOf("DM", "糖尿病")),
                disease(id = "disease_0003", synonyms = emptyList()),
            )
        val result =
            DiseaseSearchService.applyKeyword(
                items = items,
                keyword = "HTN",
                match = KeywordMatch.PARTIAL,
                target = DiseaseKeywordTarget.SYNONYMS,
            )
        assertEquals(listOf("disease_0001"), result.map { it.id })
    }

    private fun disease(
        id: String,
        name: String,
        nameKana: String,
    ): Disease = sampleDisease(id = id).copy(name = name, nameKana = nameKana)

    private fun disease(
        id: String,
        nameEnglish: String,
    ): Disease = sampleDisease(id = id).copy(nameEnglish = nameEnglish)

    private fun disease(
        id: String,
        synonyms: List<String>,
    ): Disease = sampleDisease(id = id).copy(synonyms = synonyms)
}
