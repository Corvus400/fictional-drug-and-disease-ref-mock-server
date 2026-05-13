package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseMedicalVocabulary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SymptomSeedBucketsTest {
    @Test
    fun `ICD chapter XV symptom bucket has eight pregnancy seed entries`() {
        val bucket = SymptomSeedBucketRepository.get(chapter = Icd10Chapter.CHAPTER_XV)

        assertEquals(expected = 8, actual = bucket.size)
        assertTrue(actual = "妊娠悪阻" in bucket)
        assertTrue(actual = "胎動減少" in bucket)
    }

    @Test
    fun `symptom buckets cover all ICD10 chapters`() {
        assertEquals(
            expected = Icd10Chapter.entries.toSet(),
            actual = SymptomSeedBuckets.byChapter.keys,
        )
    }

    @Test
    fun `symptom buckets do not contain duplicate entries within each ICD10 chapter`() {
        SymptomSeedBuckets.byChapter.forEach { (chapter, bucket) ->
            assertEquals(
                expected = bucket.size,
                actual = bucket.toSet().size,
                message = "${chapter.chapterKey} bucket must not contain duplicate entries",
            )
        }
    }

    @Test
    fun `symptom buckets do not start with forbidden kana`() {
        val forbiddenStarts = setOf('ン', 'ー', 'ッ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ', 'ョ', 'ヮ', 'ヵ', 'ヶ')

        val violations =
            SymptomSeedBuckets.byChapter.flatMap { (chapter, bucket) ->
                bucket.filter { entry -> entry.first() in forbiddenStarts }
                    .map { entry -> "${chapter.chapterKey}:$entry" }
            }

        assertTrue(actual = violations.isEmpty(), message = "forbidden starts: $violations")
    }

    @Test
    fun `drug symptom placeholder with DrugContext returns coined kana rather than raw mapped chapter entry`() {
        val rawBucket = SymptomSeedBucketRepository.get(chapter = Icd10Chapter.CHAPTER_IX)
        val coined =
            MedicalVocabularyDictionary.resolve(
                key = "symptom",
                seed = 42L,
                context = BucketContextKey.DrugContext(atcInitial = 'C'),
            )

        assertFalse(actual = coined in rawBucket)
    }

    @Test
    fun `disease mainSymptom placeholder with DiseaseContext returns coined kana rather than raw chapter entry`() {
        val rawBucket = SymptomSeedBucketRepository.get(chapter = Icd10Chapter.CHAPTER_XV)
        val coined =
            DiseaseMedicalVocabulary.resolve(
                key = "mainSymptom",
                seed = 42L,
                context = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_XV),
            )

        assertFalse(actual = coined in rawBucket)
    }

    @Test
    fun `symptom placeholder is deterministic for the same seed and disease context`() {
        val first =
            DiseaseMedicalVocabulary.resolve(
                key = "mainSymptom",
                seed = 42L,
                context = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_XV),
            )
        val second =
            DiseaseMedicalVocabulary.resolve(
                key = "mainSymptom",
                seed = 42L,
                context = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_XV),
            )

        assertEquals(expected = first, actual = second)
    }
}
