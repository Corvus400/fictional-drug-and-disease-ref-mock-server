package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseListFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.sample.SampleFixtures
import kotlin.test.Test
import kotlin.test.assertTrue

class FixtureProviderConsistencyTest {
    private val allFixtureProviders: List<Pair<String, FixtureProvider<*>>> = listOf(
        "SampleFixtures" to SampleFixtures,
        "DrugListFixtures" to DrugListFixtures(drugs = emptyList()),
        "DiseaseListFixtures" to DiseaseListFixtures(diseases = emptyList()),
    )

    @Test
    fun `all FixtureProviders have matching scenarios and scenarioTitles keys`() {
        allFixtureProviders.forEach { (name, provider) ->
            val scenarioKeys = provider.scenarios.keys
            val titleKeys = provider.scenarioTitles.keys
            val missingTitles = scenarioKeys - titleKeys
            assertTrue(
                missingTitles.isEmpty(),
                "$name: scenarioTitlesに未定義のシナリオがあります: $missingTitles",
            )
        }
    }
}
