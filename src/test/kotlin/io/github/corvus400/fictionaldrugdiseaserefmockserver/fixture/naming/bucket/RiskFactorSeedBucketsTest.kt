package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RiskFactorSeedBucketsTest {
    @Test
    fun `risk factor buckets cover all ICD10 chapters`() {
        assertEquals(
            expected = Icd10Chapter.entries.toSet(),
            actual = RiskFactorSeedBuckets.byChapter.keys,
        )
    }

    @Test
    fun `risk factor buckets do not contain duplicate entries within each ICD10 chapter`() {
        RiskFactorSeedBuckets.byChapter.forEach { (chapter, bucket) ->
            assertEquals(
                expected = bucket.size,
                actual = bucket.toSet().size,
                message = "${chapter.chapterKey} bucket must not contain duplicate entries",
            )
        }
    }

    @Test
    fun `chapter I risk factor bucket contains infection route terms`() {
        val chapterIBucket = RiskFactorSeedBuckets.riskFactorsFor(chapter = Icd10Chapter.CHAPTER_I)

        assertTrue(
            actual = chapterIBucket.any { riskFactor ->
                RiskFactorSeedBuckets.INFECTION_KEYWORDS.any { keyword -> keyword in riskFactor }
            },
        )
    }

    @Test
    fun `chapter I exclusive risk factors are not reused by other chapters`() {
        val chapterIOnlyFactors = RiskFactorSeedBuckets.infectionExclusiveFactors()
        val firstViolation =
            RiskFactorSeedBuckets.byChapter
                .filterKeys { chapter -> chapter != Icd10Chapter.CHAPTER_I }
                .flatMap { (chapter, bucket) ->
                    bucket
                        .filter { riskFactor -> riskFactor in chapterIOnlyFactors }
                        .map { riskFactor -> "${chapter.chapterKey}:$riskFactor" }
                }
                .firstOrNull()

        assertEquals(expected = null, actual = firstViolation)
    }
}
