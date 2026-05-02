package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DiseaseListFixturesSummaryTest {
    @Test
    fun `default scenario items(0) has exactly 8 fields and matches DiseaseSummary shape`() {
        val diseases = DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        ).generate(blueprints = DiseaseBlueprintFactory.build())

        val fixtures = DiseaseListFixtures(diseases = diseases)
        val response = fixtures.getByScenario(scenario = "default")

        val envelope = Json.parseToJsonElement(AppJson.encodeToString(response)).jsonObject
        val items = envelope["items"]?.jsonArray
        assertNotNull(items, "envelope must expose items array")
        val firstItem = items.firstOrNull()?.jsonObject
        assertNotNull(firstItem, "default scenario must have at least one item")

        assertEquals(
            expected = 8,
            actual = firstItem.size,
            message = "items[0] must expose exactly 8 fields (DiseaseSummary shape), got keys=${firstItem.keys}",
        )
        assertEquals(
            expected = setOf(
                "id",
                "name",
                "icd10_chapter",
                "medical_department",
                "chronicity",
                "infectious",
                "name_kana",
                "revised_at",
            ),
            actual = firstItem.keys,
            message = "items[0] keys must match DiseaseSummary snake_case shape",
        )
    }
}
