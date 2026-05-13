package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixturesTestSupport.buildFreshGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.toSummary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DrugSearchService
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DrugSortKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.SearchDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugListFixturesTest {
    @Test
    fun `constructor succeeds with empty drugs list because validator reports no violations`() {
        val fixtures = DrugListFixtures(drugs = emptyList())

        assertEquals(
            expected = setOf("default", "empty"),
            actual = fixtures.scenarios.keys,
            message = "scenarios must expose default and empty keys",
        )
    }

    @Test
    fun `default scenario exposes first page DEFAULT_PAGE_SIZE summaries with pagination envelope`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val fixtures = DrugListFixtures(drugs = drugs)

        val expectedPageSize = SearchDefaults.DEFAULT_PAGE_SIZE
        val sortedDrugs = DrugSearchService.applySort(items = drugs, sort = DrugSortKey.REVISED_AT_DESC)
        val expectedItems = sortedDrugs.map { drug -> drug.toSummary() }.take(n = expectedPageSize)
        val expectedTotalPages = (drugs.size + expectedPageSize - 1) / expectedPageSize
        assertEquals(
            expected = DrugListResponse(
                items = expectedItems,
                page = 1,
                pageSize = expectedPageSize,
                totalPages = expectedTotalPages,
                totalCount = drugs.size,
            ),
            actual = fixtures.getByScenario(scenario = "default"),
            message = "default scenario must expose first page with DEFAULT_PAGE_SIZE summaries " +
                "(sorted by revised_at desc)",
        )
    }

    @Test
    fun `empty scenario always returns envelope with zero items regardless of drugs input`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val fixtures = DrugListFixtures(drugs = drugs)

        assertEquals(
            expected = DrugListResponse(
                items = emptyList(),
                page = 1,
                pageSize = SearchDefaults.DEFAULT_PAGE_SIZE,
                totalPages = 0,
                totalCount = 0,
            ),
            actual = fixtures.getByScenario(scenario = "empty"),
            message = "empty scenario must return envelope with 0 items",
        )
    }

    @Test
    fun `describeFixture reports items size of total for catalog display`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val fixtures = DrugListFixtures(drugs = drugs)

        val firstPageSize = SearchDefaults.DEFAULT_PAGE_SIZE.coerceAtMost(drugs.size)
        assertEquals(
            expected = "items=$firstPageSize of ${drugs.size}",
            actual = fixtures.describeFixture(fixture = fixtures.getByScenario(scenario = "default")),
            message = "describeFixture must report the first-page item count out of totalCount",
        )
    }

    @Test
    fun `allDrugsById resolves every drug id back to the originating Drug`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val fixtures = DrugListFixtures(drugs = drugs)

        val firstDrug = drugs.first()
        assertEquals(
            expected = AllDrugsByIdSnapshot(indexedCount = drugs.size, firstDrugMatches = true),
            actual = AllDrugsByIdSnapshot(
                indexedCount = fixtures.allDrugsById.size,
                firstDrugMatches = fixtures.allDrugsById[firstDrug.id] == firstDrug,
            ),
            message = "allDrugsById must index every drug and resolve ids to the originating Drug",
        )
    }

    @Test
    fun `init fails fast when DrugFixtureValidator reports violations`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val corrupted = drugs.first().copy(contraindications = emptyList())
        val withCorrupted = listOf(corrupted) + drugs.drop(n = 1)

        val failure = runCatching { DrugListFixtures(drugs = withCorrupted) }.exceptionOrNull()
        assertEquals(
            expected = ValidatorFailureSnapshot(
                type = IllegalArgumentException::class.simpleName,
                mentionsContraindicationsViolation = true,
            ),
            actual = ValidatorFailureSnapshot(
                type = failure?.let { it::class.simpleName },
                mentionsContraindicationsViolation =
                failure?.message?.contains("contraindications size >= 1 required") == true,
            ),
            message = "init must reject drugs with validator violations at startup",
        )
    }

    private data class AllDrugsByIdSnapshot(
        val indexedCount: Int,
        val firstDrugMatches: Boolean,
    )

    private data class ValidatorFailureSnapshot(
        val type: String?,
        val mentionsContraindicationsViolation: Boolean,
    )
}
