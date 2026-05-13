package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiseaseMedicalVocabularyTest {
    @Test
    fun `resolve returns a non-blank value for every category-A key`() {
        categoryAKeys().forEach { key ->
            val seed = stableHash(id = "disease_0001:$key", slot = 0, index = 0)
            val value = DiseaseMedicalVocabulary.resolve(key, seed)
            assertTrue(
                value.isNotBlank(),
                "DiseaseMedicalVocabulary.resolve('$key', $seed) returned blank; " +
                    "every category-A key must yield a non-empty vocabulary entry",
            )
        }
    }

    @Test
    fun `resolve is deterministic for the same seed`() {
        categoryAKeys().forEach { key ->
            val seed = stableHash(id = "determinism:$key", slot = 0, index = 0)
            val first = DiseaseMedicalVocabulary.resolve(key, seed)
            val second = DiseaseMedicalVocabulary.resolve(key, seed)
            assertEquals(
                first,
                second,
                "DiseaseMedicalVocabulary.resolve must be deterministic: key=$key seed=$seed",
            )
        }
    }

    @Test
    fun `resolve throws on unknown category-A key to catch dictionary gaps`() {
        val error = runCatching { DiseaseMedicalVocabulary.resolve("unknownKey", seed = 0) }.exceptionOrNull()
        assertEquals(
            expected = UnknownKeyFailureSnapshot(throws = true, mentionsKey = true),
            actual = UnknownKeyFailureSnapshot(
                throws = error != null,
                mentionsKey = error?.message.orEmpty().contains("unknownKey"),
            ),
            message = "resolve must fail fast and mention the offending category-A key",
        )
    }

    private data class UnknownKeyFailureSnapshot(
        val throws: Boolean,
        val mentionsKey: Boolean,
    )

    private fun categoryAKeys(): List<String> =
        DiseasePlaceholderKey.entries
            .filter { it.category == DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY }
            .map { it.jsonKey }
}
