package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixturesTestSupport.buildFreshGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugListFixturesSummaryTest {
    @Test
    fun `default scenario items first entry has exactly 10 fields matching DrugSummary shape`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val fixtures = DrugListFixtures(drugs = drugs)

        val response = fixtures.getByScenario(scenario = "default")
        val encoded = AppJson.encodeToString(response)
        val rootObject = Json.parseToJsonElement(string = encoded).jsonObject
        val firstItem = rootObject["items"]?.jsonArray?.firstOrNull()?.jsonObject

        assertEquals(
            expected = SummaryShapeSnapshot(
                hasFirstItem = true,
                keys = setOf(
                    "id",
                    "brand_name",
                    "generic_name",
                    "therapeutic_category_name",
                    "regulatory_class",
                    "dosage_form",
                    "brand_name_kana",
                    "atc_code",
                    "revised_at",
                    "image_url",
                ),
            ),
            actual = SummaryShapeSnapshot(
                hasFirstItem = firstItem != null,
                keys = firstItem?.keys,
            ),
            message = "items[0] must expose exactly 10 DrugSummary snake_case fields",
        )
    }

    private data class SummaryShapeSnapshot(
        val hasFirstItem: Boolean,
        val keys: Set<String>?,
    )
}
