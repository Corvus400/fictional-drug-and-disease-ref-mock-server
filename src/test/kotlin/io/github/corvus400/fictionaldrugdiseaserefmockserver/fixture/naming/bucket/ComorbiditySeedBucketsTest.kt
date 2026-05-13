package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComorbiditySeedBucketsTest {
    @Test
    fun `ICD chapter XV comorbidity bucket has six pregnancy seed entries`() {
        val bucket = ComorbiditySeedBucketRepository.get(chapter = Icd10Chapter.CHAPTER_XV)

        assertEquals(expected = 6, actual = bucket.size)
        assertTrue(actual = "妊娠糖尿病" in bucket)
        assertTrue(actual = "妊娠悪阻" in bucket)
    }

    @Test
    fun `comorbidity buckets cover all ICD10 chapters`() {
        assertEquals(
            expected = Icd10Chapter.entries.toSet(),
            actual = ComorbiditySeedBuckets.byChapter.keys,
        )
    }

    @Test
    fun `comorbidity buckets do not contain duplicate entries within each ICD10 chapter`() {
        ComorbiditySeedBuckets.byChapter.forEach { (chapter, bucket) ->
            assertEquals(
                expected = bucket.size,
                actual = bucket.toSet().size,
                message = "${chapter.chapterKey} bucket must not contain duplicate entries",
            )
        }
    }

    @Test
    fun `drug comorbidity placeholder with DrugContext returns coined kana rather than raw mapped chapter entry`() {
        val rawBucket = ComorbiditySeedBucketRepository.get(chapter = Icd10Chapter.CHAPTER_IX)
        val coined =
            MedicalVocabularyDictionary.resolve(
                key = "comorbidity",
                seed = 42L,
                context = BucketContextKey.DrugContext(atcInitial = 'C'),
            )

        assertFalse(actual = coined in rawBucket)
    }

    @Test
    fun `comorbidity placeholder with DiseaseContext returns coined kana rather than raw chapter entry`() {
        val rawBucket = ComorbiditySeedBucketRepository.get(chapter = Icd10Chapter.CHAPTER_XV)
        val coined =
            MedicalVocabularyDictionary.resolve(
                key = "comorbidity",
                seed = 42L,
                context = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_XV),
            )

        assertFalse(actual = coined in rawBucket)
    }
}
