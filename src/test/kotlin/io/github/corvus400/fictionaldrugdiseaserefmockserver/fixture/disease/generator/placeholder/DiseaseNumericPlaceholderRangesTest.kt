package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiseaseNumericPlaceholderRangesTest {
    @Test
    fun `resolve returns a non-blank value matching the expected pattern for every category-D key`() {
        val violations = EXPECTED_PATTERNS.mapNotNull { (key, pattern) ->
            val seed = stableHash(id = "disease_0001:$key", slot = 0, index = 0)
            val value = DiseaseNumericPlaceholderRanges.resolve(key, seed)
            when {
                value.isBlank() -> "resolve('$key', $seed) returned blank"
                pattern.matches(value).not() ->
                    "resolve('$key', $seed) returned '$value' which does not match /${pattern.pattern}/"
                else -> null
            }
        }

        assertTrue(actual = violations.isEmpty(), message = "category-D formatting violations: $violations")
    }

    @Test
    fun `resolve is deterministic for the same seed`() {
        EXPECTED_PATTERNS.keys.forEach { key ->
            val seed = stableHash(id = "determinism:$key", slot = 0, index = 0)
            val first = DiseaseNumericPlaceholderRanges.resolve(key, seed)
            val second = DiseaseNumericPlaceholderRanges.resolve(key, seed)
            assertEquals(
                first,
                second,
                "DiseaseNumericPlaceholderRanges.resolve must be deterministic: key=$key seed=$seed",
            )
        }
    }

    @Test
    fun `resolve throws on unknown category-D key to catch formatter gaps`() {
        val error = runCatching { DiseaseNumericPlaceholderRanges.resolve("unknownKey", seed = 0) }.exceptionOrNull()
        assertEquals(
            expected = UnknownKeyFailureSnapshot(throws = true, mentionsKey = true),
            actual = UnknownKeyFailureSnapshot(
                throws = error != null,
                mentionsKey = error?.message.orEmpty().contains("unknownKey"),
            ),
            message = "resolve must fail fast and mention the offending category-D key",
        )
    }

    private data class UnknownKeyFailureSnapshot(
        val throws: Boolean,
        val mentionsKey: Boolean,
    )

    companion object {
        private val EXPECTED_PATTERNS: Map<String, Regex> =
            mapOf(
                "annualIncidence" to Regex("""^\d{1,3}(,\d{3})* 例$"""),
                "evaluationDuration" to Regex("""^\d+ 週間$"""),
                "gradeCount" to Regex("""^\d+$"""),
                "peakAgeYears" to Regex("""^\d+-\d+$"""),
                "prevalenceRate" to Regex("""^\d+$"""),
                "prognosisRate" to Regex("""^\d+\.\d%$"""),
                "severityThreshold" to Regex("""^\d+ 点$"""),
                "sexRatio" to Regex("""^\d+:1$"""),
                "supportingFindingCount" to Regex("""^\d+$"""),
            )
    }
}
