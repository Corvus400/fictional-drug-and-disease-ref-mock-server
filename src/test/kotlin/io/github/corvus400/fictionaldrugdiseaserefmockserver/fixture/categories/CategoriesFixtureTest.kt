package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.categories

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixturesTestSupport.buildFreshGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CategoriesFixtureTest {
    @Test
    fun `CategoriesFixture build atc contains 14 entries (A to V ATC first letters)`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val response = CategoriesFixture(drugs = drugs).build()

        assertEquals(
            expected = 14,
            actual = response.atc.size,
            message = "atc must expose 14 ATC first-letter entries (A,B,C,D,G,H,J,L,M,N,P,R,S,V)",
        )
    }

    @Test
    fun `CategoriesFixture build icd10Chapters contains 22 entries (I to XXII roman numerals)`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val response = CategoriesFixture(drugs = drugs).build()

        assertEquals(
            expected = 22,
            actual = response.icd10Chapters.size,
            message = "icd10Chapters must expose 22 entries matching ICD-10 chapter count (I..XXII)",
        )
    }

    @Test
    fun `CategoriesFixture build reads therapeuticCategories from DrugListFixtures allDrugs`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val response = CategoriesFixture(drugs = drugs).build()

        assertTrue(
            actual = response.therapeuticCategories.isNotEmpty(),
            message = "therapeuticCategories must aggregate from the 120 fixed drugs (non-zero), " +
                "not from a per-scenario empty list",
        )
    }

    @Test
    fun `CategoriesFixture build therapeuticCategories has unique ids (no duplicates)`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val response = CategoriesFixture(drugs = drugs).build()

        val ids = response.therapeuticCategories.map { entry -> entry.id }
        assertEquals(
            expected = ids.toSet().size,
            actual = ids.size,
            message = "therapeuticCategories must be deduplicated by id (distinctBy effect)",
        )
    }
}
