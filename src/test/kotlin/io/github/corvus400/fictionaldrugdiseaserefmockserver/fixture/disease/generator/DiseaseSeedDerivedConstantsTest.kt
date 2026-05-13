package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiseaseSeedDerivedConstantsTest {
    @Test
    fun `synonym differential and complication counts vary across generated diseases`() {
        val diseases = generateDiseases()

        assertTrue(
            actual = diseases.map { disease -> disease.synonyms.size }.distinct().size >= MIN_COUNT_DISTINCT_SIZE,
            message = "synonym count must not be fixed",
        )
        assertTrue(
            actual =
            diseases.map { disease -> disease.differentialDiagnoses.size }.distinct().size >= MIN_COUNT_DISTINCT_SIZE,
            message = "differential diagnosis count must not be fixed",
        )
        assertTrue(
            actual = diseases.map { disease -> disease.complications.size }.distinct().size >= MIN_COUNT_DISTINCT_SIZE,
            message = "complication count must not be fixed",
        )
        assertEquals(
            expected = null,
            actual = diseases.firstOrNull { disease ->
                disease.synonyms.size !in COUNT_RANGE ||
                    disease.differentialDiagnoses.size !in COUNT_RANGE ||
                    disease.complications.size !in COUNT_RANGE
            },
            message = "generated list counts must stay inside the configured range",
        )
    }

    @Test
    fun `epidemiology onset age spans vary while keeping ordered age ranges`() {
        val ranges = generateDiseases().mapNotNull { disease -> disease.epidemiology?.onsetAgeRange }
        val spans = ranges.mapNotNull { range ->
            val min = range.minAgeYears
            val max = range.maxAgeYears
            if (min != null && max != null) max - min else null
        }

        assertTrue(
            actual = spans.distinct().size >= MIN_ONSET_SPAN_DISTINCT_SIZE,
            message = "onset age span must not be a single fixed value: ${spans.distinct()}",
        )
        assertEquals(
            expected = null,
            actual = spans.firstOrNull { span -> span !in ONSET_SPAN_RANGE },
            message = "onset age span must stay in the configured range",
        )
    }

    private fun generateDiseases(): List<Disease> =
        DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        ).generate(blueprints = DiseaseBlueprintFactory.build())
            .filterNot { disease -> disease.id in DISEASE_FINAL_OVERRIDES.keys }

    private companion object {
        val COUNT_RANGE: IntRange = 1..3
        val ONSET_SPAN_RANGE: IntRange = 5..20
        const val MIN_COUNT_DISTINCT_SIZE: Int = 2
        const val MIN_ONSET_SPAN_DISTINCT_SIZE: Int = 2
    }
}
