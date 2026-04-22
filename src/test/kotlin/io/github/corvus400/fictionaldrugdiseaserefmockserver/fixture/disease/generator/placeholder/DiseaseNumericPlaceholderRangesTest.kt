package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DiseaseNumericPlaceholderRangesTest {
    @Test
    fun `resolve returns a non-blank value matching the expected pattern for every category-D key`() {
        EXPECTED_PATTERNS.forEach { (key, pattern) ->
            val seed = stableHash(id = "disease_0001:$key", slot = 0, index = 0)
            val value = DiseaseNumericPlaceholderRanges.resolve(key, seed)
            assertTrue(
                value.isNotBlank(),
                "resolve('$key', $seed) returned blank",
            )
            assertTrue(
                pattern.matches(value),
                "resolve('$key', $seed) returned '$value' which does not match /${pattern.pattern}/",
            )
        }
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
        val error =
            runCatching {
                DiseaseNumericPlaceholderRanges.resolve("unknownKey", seed = 0)
            }.exceptionOrNull()
        assertNotNull(
            error,
            "resolve must throw for an unknown category-D key so gaps fail fast",
        )
        assertTrue(
            error.message.orEmpty().contains("unknownKey"),
            "error message must mention the offending key; got: ${error.message}",
        )
    }

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
