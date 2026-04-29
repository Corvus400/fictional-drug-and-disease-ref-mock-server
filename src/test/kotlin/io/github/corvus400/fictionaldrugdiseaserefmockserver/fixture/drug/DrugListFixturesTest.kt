package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixturesTestSupport.buildFreshGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.toSummary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DrugSearchService
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DrugSortKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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

        val expectedPageSize = DrugListFixtures.DEFAULT_PAGE_SIZE
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
                pageSize = DrugListFixtures.DEFAULT_PAGE_SIZE,
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

        val firstPageSize = DrugListFixtures.DEFAULT_PAGE_SIZE.coerceAtMost(drugs.size)
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

        assertEquals(
            expected = drugs.size,
            actual = fixtures.allDrugsById.size,
            message = "allDrugsById must index every drug once",
        )
        val firstDrug = drugs.first()
        assertEquals(
            expected = firstDrug,
            actual = fixtures.allDrugsById[firstDrug.id],
            message = "allDrugsById[drug.id] must return the matching Drug",
        )
    }

    @Test
    fun `init fails fast when DrugFixtureValidator reports violations`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val corrupted = drugs.first().copy(contraindications = emptyList())
        val withCorrupted = listOf(corrupted) + drugs.drop(n = 1)

        val failure = assertFailsWith<IllegalArgumentException>(
            message = "init must reject drugs with validator violations at startup",
        ) {
            DrugListFixtures(drugs = withCorrupted)
        }
        assertTrue(
            actual = failure.message?.contains("contraindications size >= 1 required") == true,
            message = "failure message must surface the underlying violation; got ${failure.message}",
        )
    }
}
