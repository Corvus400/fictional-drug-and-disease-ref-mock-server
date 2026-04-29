package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.DiseaseListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.toSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DiseaseListFixturesTest {
    @Test
    fun `constructor succeeds with empty diseases list because validator reports no violations`() {
        val fixtures = DiseaseListFixtures(diseases = emptyList())

        assertEquals(
            expected = setOf("default", "empty"),
            actual = fixtures.scenarios.keys,
            message = "scenarios must expose default and empty keys",
        )
    }

    @Test
    fun `default scenario exposes first page DEFAULT_PAGE_SIZE summaries with pagination envelope`() {
        val diseases = buildFreshGenerator().generate(blueprints = DiseaseBlueprintFactory.build())

        val fixtures = DiseaseListFixtures(diseases = diseases)

        val expectedPageSize = DiseaseListFixtures.DEFAULT_PAGE_SIZE
        val expectedItems = diseases.map { it.toSummary() }.take(n = expectedPageSize)
        val expectedTotalPages = (diseases.size + expectedPageSize - 1) / expectedPageSize
        assertEquals(
            expected = DiseaseListResponse(
                items = expectedItems,
                page = 1,
                pageSize = expectedPageSize,
                totalPages = expectedTotalPages,
                totalCount = diseases.size,
            ),
            actual = fixtures.getByScenario(scenario = "default"),
            message = "default scenario must expose first page with DEFAULT_PAGE_SIZE summaries",
        )
    }

    @Test
    fun `empty scenario always returns envelope with zero items regardless of diseases input`() {
        val diseases = buildFreshGenerator().generate(blueprints = DiseaseBlueprintFactory.build())

        val fixtures = DiseaseListFixtures(diseases = diseases)

        assertEquals(
            expected = DiseaseListResponse(
                items = emptyList(),
                page = 1,
                pageSize = DiseaseListFixtures.DEFAULT_PAGE_SIZE,
                totalPages = 0,
                totalCount = 0,
            ),
            actual = fixtures.getByScenario(scenario = "empty"),
            message = "empty scenario must return envelope with 0 items",
        )
    }

    @Test
    fun `describeFixture reports items size of total for catalog display`() {
        val diseases = buildFreshGenerator().generate(blueprints = DiseaseBlueprintFactory.build())
        val fixtures = DiseaseListFixtures(diseases = diseases)

        val firstPageSize = DiseaseListFixtures.DEFAULT_PAGE_SIZE.coerceAtMost(diseases.size)
        assertEquals(
            expected = "items=$firstPageSize of ${diseases.size}",
            actual = fixtures.describeFixture(fixture = fixtures.getByScenario(scenario = "default")),
            message = "describeFixture must report the first-page item count out of totalCount",
        )
    }

    @Test
    fun `init fails fast when DiseaseFixtureValidator reports violations`() {
        val diseases = buildFreshGenerator().generate(blueprints = DiseaseBlueprintFactory.build())
        val corrupted = diseases.first().copy(symptoms = SymptomInfo(mainSymptoms = emptyList()))
        val withCorrupted = listOf(corrupted) + diseases.drop(n = 1)

        val failure = assertFailsWith<IllegalArgumentException>(
            message = "init must reject diseases with validator violations at startup",
        ) {
            DiseaseListFixtures(diseases = withCorrupted)
        }
        assertTrue(
            actual = failure.message?.contains("mainSymptoms must have at least 1 entry") == true,
            message = "failure message must surface the underlying violation; got ${failure.message}",
        )
    }

    private companion object {
        fun buildFreshGenerator(): DiseaseGenerator {
            val adapter = FixmergeNameAdapter()
            return DiseaseGenerator(
                adapter = adapter,
                placeholderDictionary = DiseasePlaceholderDictionary(),
            )
        }
    }
}
