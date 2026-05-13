package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DrugCategorySeedBucketsTest {
    @Test
    fun `ATC L drug category bucket has eight oncology seed entries`() {
        val bucket = DrugCategorySeedBucketRepository.get(atcInitial = 'L')

        assertEquals(expected = 8, actual = bucket.size)
        assertTrue(actual = "アルキル化剤系" in bucket)
        assertTrue(actual = "免疫チェックポイント阻害剤系" in bucket)
    }

    @Test
    fun `drug category buckets cover all ATC initials`() {
        assertEquals(
            expected = setOf('A', 'B', 'C', 'D', 'G', 'H', 'J', 'L', 'M', 'N', 'P', 'R', 'S', 'V'),
            actual = DrugCategorySeedBuckets.byAtcInitial.keys,
        )
    }

    @Test
    fun `drug category buckets do not contain duplicate entries within each ATC bucket`() {
        DrugCategorySeedBuckets.byAtcInitial.forEach { (atcInitial, bucket) ->
            assertEquals(
                expected = bucket.size,
                actual = bucket.toSet().size,
                message = "ATC=$atcInitial bucket must not contain duplicate entries",
            )
        }
    }

    @Test
    fun `drugCategory placeholder with DrugContext returns coined kana rather than raw bucket entry`() {
        val rawBucket = DrugCategorySeedBucketRepository.get(atcInitial = 'L')
        val coined =
            MedicalVocabularyDictionary.resolve(
                key = "drugCategory",
                seed = 42L,
                context = BucketContextKey.DrugContext(atcInitial = 'L'),
            )

        assertFalse(actual = coined in rawBucket)
    }
}
