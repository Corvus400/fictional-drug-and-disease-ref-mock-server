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

    @Test
    fun `applyKeyword with two tokens requires each matched by at least one target field (disease)`() {
        val items =
            listOf(
                disease(id = "disease_0001", name = "高血圧症", nameKana = "コウケツアツショウ"),
                disease(id = "disease_0002", name = "糖尿病", nameKana = "トウニョウビョウ"),
            )
        // 両トークンとも disease_0001 の name/nameKana のいずれかにヒットすべき
        val result =
            DiseaseSearchService.applyKeyword(
                items = items,
                keyword = "高血圧 コウケツ",
                match = KeywordMatch.PARTIAL,
                target = DiseaseKeywordTarget.NAME,
            )
        assertEquals(listOf("disease_0001"), result.map { it.id })
    }

    @Test
    fun `applyKeyword with two tokens does NOT require same-field AND (disease synonyms)`() {
        val items =
            listOf(
                disease(id = "disease_0001", synonyms = listOf("HTN", "高血圧")),
                // 別 synonym 要素でも OK: token 'HTN' は synonyms[0]、token '高血圧' は synonyms[1] にヒット → 1 アイテム上で OK
            )
        val result =
            DiseaseSearchService.applyKeyword(
                items = items,
                keyword = "HTN 高血圧",
                match = KeywordMatch.PARTIAL,
                target = DiseaseKeywordTarget.SYNONYMS,
            )
        assertEquals(listOf("disease_0001"), result.map { it.id })
    }

    @Test
    fun `applyKeyword with match PREFIX filters by startsWith, not contains (disease)`() {
        val items =
            listOf(
                disease(id = "disease_0001", name = "高血圧症", nameKana = "コウケツアツショウ"),
                // name に keyword を contains するが startsWith ではない / nameKana は keyword を含まない
                disease(id = "disease_0002", name = "本態性高血圧", nameKana = "ホンタイセイコウケツアツ"),
            )
        val result =
            DiseaseSearchService.applyKeyword(
                items = items,
                keyword = "高血圧",
                match = KeywordMatch.PREFIX,
                target = DiseaseKeywordTarget.NAME,
            )
        assertEquals(listOf("disease_0001"), result.map { it.id })
    }

    @Test
    fun `applyKeyword with unicode keyword on synonyms list works correctly`() {
        val items =
            listOf(
                disease(id = "disease_0001", synonyms = listOf("本態性高血圧症", "原発性高血圧")),
                disease(id = "disease_0002", synonyms = listOf("糖尿病")),
            )
        val result =
            DiseaseSearchService.applyKeyword(
                items = items,
                keyword = "高血圧",
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

    @Test
    fun `applySort returns items sorted by revisedAt descending when REVISED_AT_DESC is specified`() {
        val items =
            listOf(
                diseaseWith(id = "disease_0001", revisedAt = "2026-01-10"),
                diseaseWith(id = "disease_0002", revisedAt = "2026-03-20"),
                diseaseWith(id = "disease_0003", revisedAt = "2026-02-01"),
            )
        val sorted = DiseaseSearchService.applySort(items = items, sort = DiseaseSortKey.REVISED_AT_DESC)
        assertEquals(listOf("disease_0002", "disease_0003", "disease_0001"), sorted.map { it.id })
    }

    @Test
    fun `applySort breaks revisedAt ties by id descending`() {
        val items =
            listOf(
                diseaseWith(id = "disease_0001", revisedAt = "2026-04-23"),
                diseaseWith(id = "disease_0002", revisedAt = "2026-04-23"),
                diseaseWith(id = "disease_0003", revisedAt = "2026-04-23"),
            )
        val sorted = DiseaseSearchService.applySort(items = items, sort = DiseaseSortKey.REVISED_AT_DESC)
        assertEquals(listOf("disease_0003", "disease_0002", "disease_0001"), sorted.map { it.id })
    }

    private fun diseaseWith(
        id: String,
        revisedAt: String,
    ): Disease = sampleDisease(id = id).copy(revisedAt = revisedAt)

    @Test
    fun `applySort sorts by nameKana ascending when NAME_KANA_ASC is specified`() {
        val items =
            listOf(
                sampleDisease(id = "disease_0001").copy(nameKana = "サンブルビョウ"),
                sampleDisease(id = "disease_0002").copy(nameKana = "アイウエビョウ"),
                sampleDisease(id = "disease_0003").copy(nameKana = "カキクケビョウ"),
            )
        val sorted = DiseaseSearchService.applySort(items = items, sort = DiseaseSortKey.NAME_KANA_ASC)
        assertEquals(listOf("disease_0002", "disease_0003", "disease_0001"), sorted.map { it.id })
    }
}
