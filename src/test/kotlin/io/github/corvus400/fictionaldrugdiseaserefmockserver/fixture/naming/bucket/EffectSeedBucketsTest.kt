package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EffectSeedBucketsTest {
    @Test
    fun `ATC L effect bucket has six oncology seed entries`() {
        val bucket = EffectSeedBucketRepository.get(atcInitial = 'L')

        assertEquals(expected = 6, actual = bucket.size)
        assertTrue(actual = "抗腫瘍作用" in bucket)
        assertTrue(actual = "免疫抑制作用" in bucket)
    }

    @Test
    fun `effect buckets cover all ATC initials`() {
        assertEquals(
            expected = setOf('A', 'B', 'C', 'D', 'G', 'H', 'J', 'L', 'M', 'N', 'P', 'R', 'S', 'V'),
            actual = EffectSeedBuckets.byAtcInitial.keys,
        )
    }

    @Test
    fun `effect buckets do not contain duplicate entries within each ATC bucket`() {
        EffectSeedBuckets.byAtcInitial.forEach { (atcInitial, bucket) ->
            assertEquals(
                expected = bucket.size,
                actual = bucket.toSet().size,
                message = "ATC=$atcInitial bucket must not contain duplicate entries",
            )
        }
    }

    @Test
    fun `effect placeholder with DrugContext returns coined kana rather than raw bucket entry`() {
        val rawBucket = EffectSeedBucketRepository.get(atcInitial = 'L')
        val coined =
            MedicalVocabularyDictionary.resolve(
                key = "effect",
                seed = 42L,
                context = BucketContextKey.DrugContext(atcInitial = 'L'),
            )

        assertFalse(actual = coined in rawBucket)
    }
}
