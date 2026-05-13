package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreventionSeedBucketsTest {
    @Test
    fun `prevention buckets cover all ICD10 chapters`() {
        assertEquals(
            expected = Icd10Chapter.entries.toSet(),
            actual = PreventionSeedBuckets.byChapter.keys,
        )
    }

    @Test
    fun `prevention buckets do not contain duplicate entries within each ICD10 chapter`() {
        PreventionSeedBuckets.byChapter.forEach { (chapter, bucket) ->
            assertEquals(
                expected = bucket.size,
                actual = bucket.toSet().size,
                message = "${chapter.chapterKey} bucket must not contain duplicate entries",
            )
        }
    }

    @Test
    fun `chapter I prevention bucket contains infection control items`() {
        val chapterIBucket = PreventionSeedBuckets.preventionFor(chapter = Icd10Chapter.CHAPTER_I)

        assertTrue(actual = "手指衛生の徹底" in chapterIBucket)
        assertTrue(actual = "適切な換気" in chapterIBucket)
    }

    @Test
    fun `chapter I exclusive prevention items are not reused by other chapters`() {
        val chapterIOnlyItems = PreventionSeedBuckets.infectionExclusiveItems()
        val firstViolation =
            PreventionSeedBuckets.byChapter
                .filterKeys { chapter -> chapter != Icd10Chapter.CHAPTER_I }
                .flatMap { (chapter, bucket) ->
                    bucket
                        .filter { item -> item in chapterIOnlyItems }
                        .map { item -> "${chapter.chapterKey}:$item" }
                }
                .firstOrNull()

        assertEquals(expected = null, actual = firstViolation)
    }
}
