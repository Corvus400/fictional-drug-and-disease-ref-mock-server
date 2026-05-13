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
        val firstItem = envelope["items"]?.jsonArray?.firstOrNull()?.jsonObject
        assertEquals(
            expected = SummaryShapeSnapshot(
                hasFirstItem = true,
                fieldCount = 8,
                keys = setOf(
                    "id",
                    "name",
                    "icd10_chapter",
                    "medical_department",
                    "chronicity",
                    "infectious",
                    "name_kana",
                    "revised_at",
                ),
            ),
            actual = SummaryShapeSnapshot(
                hasFirstItem = firstItem != null,
                fieldCount = firstItem?.size,
                keys = firstItem?.keys,
            ),
            message = "items[0] must expose exactly 8 fields (DiseaseSummary shape), got keys=${firstItem?.keys}",
        )
    }

    private data class SummaryShapeSnapshot(
        val hasFirstItem: Boolean,
        val fieldCount: Int?,
        val keys: Set<String>?,
    )
}
