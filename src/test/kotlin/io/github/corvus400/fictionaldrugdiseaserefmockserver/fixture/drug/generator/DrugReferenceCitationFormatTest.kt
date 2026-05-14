package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.ClinicalSeedBucketRegistry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugReferenceCitationFormatTest {
    @Test
    fun `citation follows Vancouver format`() {
        val firstViolation =
            generateDrugs().asSequence()
                .flatMap { drug -> drug.references.asSequence().map { reference -> drug.id to reference.citation } }
                .firstOrNull { (_, citation) -> !VANCOUVER_CITATION.matches(citation) }
                ?.let { (drugId, citation) -> CitationFormatViolation(drugId = drugId, citation = citation) }

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `title suffix is one of expected title suffixes`() {
        val firstViolation =
            generateDrugs().asSequence()
                .flatMap { drug -> drug.references.asSequence().map { reference -> drug.id to reference.citation } }
                .mapNotNull { (drugId, citation) ->
                    val title = VANCOUVER_CITATION.matchEntire(citation)?.groupValues?.get(TITLE_GROUP_INDEX)
                    title?.takeUnless { parsedTitle ->
                        EXPECTED_TITLE_SUFFIXES.any { suffix -> parsedTitle.endsWith(suffix) }
                    }?.let { parsedTitle ->
                        TitleSuffixViolation(drugId = drugId, title = parsedTitle)
                    }
                }
                .firstOrNull()

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `author and title stem do not collide statically`() {
        val collision =
            ClinicalSeedBucketRegistry.all.getValue("author").toSet()
                .intersect(ClinicalSeedBucketRegistry.all.getValue("titleStem").toSet())

        assertEquals(expected = emptySet(), actual = collision)
    }

    private fun generateDrugs(): List<Drug> {
        val adapter = FixmergeNameAdapter()
        val diseasePlaceholderDictionary = DiseasePlaceholderDictionary()
        val diseases =
            DiseaseGenerator(adapter = adapter, placeholderDictionary = diseasePlaceholderDictionary)
                .generate(blueprints = DiseaseBlueprintFactory.build())
        return DrugGenerator(
            adapter = adapter,
            placeholderDictionary = DrugPlaceholderDictionary(nameAdapter = adapter, diseases = diseases),
            diseases = diseases,
        ).generate(blueprints = DrugBlueprintFactory.build())
    }

    private data class CitationFormatViolation(
        val drugId: String,
        val citation: String,
    )

    private data class TitleSuffixViolation(
        val drugId: String,
        val title: String,
    )

    private companion object {
        val VANCOUVER_CITATION: Regex =
            Regex("""^[^.]+\. ([^.]+)\. [^,]+, [0-9]+, [0-9]+-[0-9]+\. \(架空\)$""")
        val EXPECTED_TITLE_SUFFIXES: Set<String> =
            setOf("研究", "報告", "検討", "評価", "解析", "調査", "試験", "観察", "比較", "検証")
        const val TITLE_GROUP_INDEX: Int = 1
    }
}
