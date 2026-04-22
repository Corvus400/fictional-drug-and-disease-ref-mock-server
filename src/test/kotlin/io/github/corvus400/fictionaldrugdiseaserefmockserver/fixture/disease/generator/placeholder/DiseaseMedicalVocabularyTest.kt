package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
        val error =
            runCatching {
                DiseaseMedicalVocabulary.resolve("unknownKey", seed = 0)
            }.exceptionOrNull()
        assertNotNull(
            error,
            "resolve must throw for an unknown category-A key so gaps fail fast",
        )
        assertTrue(
            error.message.orEmpty().contains("unknownKey"),
            "error message must mention the offending key; got: ${error.message}",
        )
    }

    private fun categoryAKeys(): List<String> =
        DiseasePlaceholderKey.values()
            .filter { it.category == DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY }
            .map { it.jsonKey }
}
