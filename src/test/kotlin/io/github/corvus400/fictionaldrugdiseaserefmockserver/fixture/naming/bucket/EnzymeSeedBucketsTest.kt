package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EnzymeSeedBucketsTest {
    @Test
    fun `enzyme global bucket has ten seed entries`() {
        val bucket = EnzymeSeedBucketRepository.get()

        assertEquals(expected = 10, actual = bucket.size)
        assertTrue(actual = "CYP3A4" in bucket)
        assertTrue(actual = "NAT2" in bucket)
    }

    @Test
    fun `enzyme global bucket does not contain duplicate entries`() {
        val bucket = EnzymeSeedBucketRepository.get()

        assertEquals(expected = bucket.size, actual = bucket.toSet().size)
    }

    @Test
    fun `enzyme placeholder returns coined kana rather than raw bucket entry`() {
        val rawBucket = EnzymeSeedBucketRepository.get()
        val coined =
            MedicalVocabularyDictionary.resolve(
                key = "enzyme",
                seed = 42L,
                context = BucketContextKey.Global,
            )

        assertFalse(actual = coined in rawBucket)
    }
}
