package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.toSummary
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
    fun `default scenario wraps provided drugs in DrugListResponse envelope as summaries`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val fixtures = DrugListFixtures(drugs = drugs)

        assertEquals(
            expected = DrugListResponse(items = drugs.map { drug -> drug.toSummary() }),
            actual = fixtures.getByScenario(scenario = "default"),
            message = "default scenario must wrap drugs as summaries in DrugListResponse items",
        )
    }

    @Test
    fun `empty scenario always returns envelope with zero items regardless of drugs input`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val fixtures = DrugListFixtures(drugs = drugs)

        assertEquals(
            expected = DrugListResponse(items = emptyList()),
            actual = fixtures.getByScenario(scenario = "empty"),
            message = "empty scenario must return envelope with 0 items",
        )
    }

    @Test
    fun `describeFixture reports items size for catalog display`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val fixtures = DrugListFixtures(drugs = drugs)

        assertEquals(
            expected = "items=${drugs.size}",
            actual = fixtures.describeFixture(fixture = fixtures.getByScenario(scenario = "default")),
            message = "describeFixture must report the item count",
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

    private companion object {
        fun buildFreshGenerator(): DrugGenerator {
            val adapter = FixmergeNameAdapter()
            return DrugGenerator(
                adapter = adapter,
                placeholderDictionary = buildTestDictionary(adapter = adapter),
            )
        }

        fun buildTestDictionary(adapter: FixmergeNameAdapter): DrugPlaceholderDictionary =
            DrugPlaceholderDictionary(
                nameAdapter = adapter,
                diseaseProvider = DiseaseFixtureProvider(all = testDiseaseFixtures()),
            )

        fun testDiseaseFixtures(): List<Disease> =
            listOf(
                makeTestDisease(id = "disease_0000", name = "架空疾患甲"),
                makeTestDisease(id = "disease_0001", name = "架空疾患乙"),
            )

        fun makeTestDisease(
            id: String,
            name: String,
        ): Disease =
            Disease(
                id = id,
                name = name,
                nameKana = "カクウシッカン",
                icd10Chapter = Icd10Chapter.CHAPTER_X,
                medicalDepartment = listOf(MedicalDepartment.INTERNAL_MEDICINE),
                chronicity = Chronicity.CHRONIC,
                infectious = false,
                summary = "テスト用の架空疾患です。",
                etiology = "テスト用の病因です。",
                symptoms = SymptomInfo(mainSymptoms = listOf("テスト症状")),
                diagnosticCriteria = DiagnosticCriteriaInfo(required = listOf("テスト診断基準")),
                treatments = TreatmentInfo(),
                revisedAt = "2026-01-01",
            )
    }
}
