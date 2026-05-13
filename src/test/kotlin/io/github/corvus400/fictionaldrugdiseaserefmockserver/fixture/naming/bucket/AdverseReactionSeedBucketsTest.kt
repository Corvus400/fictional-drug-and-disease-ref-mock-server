package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AdverseReactionSeedBucketsTest {
    @Test
    fun `ATC L adverse reaction bucket has eight oncology seed entries`() {
        val bucket = AdverseReactionSeedBucketRepository.get(atcInitial = 'L')

        assertEquals(expected = 8, actual = bucket.size)
        assertTrue(actual = "骨髄抑制" in bucket)
        assertTrue(actual = "間質性肺障害" in bucket)
    }

    @Test
    fun `adverse reaction buckets cover all ATC initials`() {
        assertEquals(
            expected = setOf('A', 'B', 'C', 'D', 'G', 'H', 'J', 'L', 'M', 'N', 'P', 'R', 'S', 'V'),
            actual = AdverseReactionSeedBuckets.byAtcInitial.keys,
        )
    }

    @Test
    fun `adverse reaction buckets do not contain duplicate entries within each ATC bucket`() {
        AdverseReactionSeedBuckets.byAtcInitial.forEach { (atcInitial, bucket) ->
            assertEquals(
                expected = bucket.size,
                actual = bucket.toSet().size,
                message = "ATC=$atcInitial bucket must not contain duplicate entries",
            )
        }
    }

    @Test
    fun `adverse reaction buckets do not start with forbidden kana`() {
        val forbiddenStarts = setOf('ン', 'ー', 'ッ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ', 'ョ', 'ヮ', 'ヵ', 'ヶ')

        val violations =
            AdverseReactionSeedBuckets.byAtcInitial.flatMap { (atcInitial, bucket) ->
                bucket.filter { entry -> entry.first() in forbiddenStarts }
                    .map { entry -> "$atcInitial:$entry" }
            }

        assertTrue(actual = violations.isEmpty(), message = "forbidden starts: $violations")
    }

    @Test
    fun `adverseReaction placeholder with DrugContext returns coined kana rather than raw bucket entry`() {
        val rawBucket = AdverseReactionSeedBucketRepository.get(atcInitial = 'L')
        val coined =
            io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder
                .MedicalVocabularyDictionary.resolve(
                    key = "adverseReaction",
                    seed = 42L,
                    context = BucketContextKey.DrugContext(atcInitial = 'L'),
                )

        assertFalse(actual = coined in rawBucket)
    }

    @Test
    fun `adverseReaction placeholder is deterministic for the same seed and ATC context`() {
        val first =
            io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder
                .MedicalVocabularyDictionary.resolve(
                    key = "adverseReaction",
                    seed = 42L,
                    context = BucketContextKey.DrugContext(atcInitial = 'L'),
                )
        val second =
            io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder
                .MedicalVocabularyDictionary.resolve(
                    key = "adverseReaction",
                    seed = 42L,
                    context = BucketContextKey.DrugContext(atcInitial = 'L'),
                )

        assertEquals(expected = first, actual = second)
    }

    @Test
    fun `adverseReaction placeholder changes by ATC context for the same seed`() {
        val oncology =
            io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder
                .MedicalVocabularyDictionary.resolve(
                    key = "adverseReaction",
                    seed = 42L,
                    context = BucketContextKey.DrugContext(atcInitial = 'L'),
                )
        val cardiovascular =
            io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder
                .MedicalVocabularyDictionary.resolve(
                    key = "adverseReaction",
                    seed = 42L,
                    context = BucketContextKey.DrugContext(atcInitial = 'C'),
                )

        assertNotEquals(illegal = oncology, actual = cardiovascular)
    }
}
