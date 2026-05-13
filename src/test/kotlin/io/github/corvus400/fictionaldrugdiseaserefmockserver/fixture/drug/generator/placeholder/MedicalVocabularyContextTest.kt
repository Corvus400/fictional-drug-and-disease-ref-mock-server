package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import kotlin.test.Test
import kotlin.test.assertEquals

class MedicalVocabularyContextTest {
    @Test
    fun `resolve with DrugContext preserves non bucket medical vocabulary output`() {
        val seed = 42L

        assertEquals(
            expected = MedicalVocabularyDictionary.resolve(key = "action", seed = seed),
            actual = MedicalVocabularyDictionary.resolve(
                key = "action",
                seed = seed,
                context = BucketContextKey.DrugContext(atcInitial = 'C'),
            ),
        )
    }
}
