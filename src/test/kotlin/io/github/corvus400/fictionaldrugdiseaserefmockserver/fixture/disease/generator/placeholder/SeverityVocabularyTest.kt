package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SeverityVocabularyTest {
    @Test
    fun `severity vocabulary covers every severity key for every ICD10 chapter`() {
        val firstMissing =
            SeverityVocabulary.keys
                .flatMap { key ->
                    Icd10Chapter.entries.mapNotNull { chapter ->
                        val entries = SeverityVocabulary.entriesFor(key = key, chapter = chapter)
                        if (entries == null || entries.size < MIN_EXPECTED_ENTRIES || entries.any(String::isBlank)) {
                            "$key:${chapter.chapterKey}"
                        } else {
                            null
                        }
                    }
                }
                .firstOrNull()

        assertEquals(expected = null, actual = firstMissing)
    }

    @Test
    fun `disease context resolves severity keys from chapter vocabulary`() {
        SeverityVocabulary.keys.forEach { key ->
            val entries = SeverityVocabulary.entriesFor(key = key, chapter = Icd10Chapter.CHAPTER_XIII).orEmpty()
            val resolved =
                DiseaseMedicalVocabulary.resolve(
                    key = key,
                    seed = 42L,
                    context = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_XIII),
                )

            assertTrue(actual = resolved in entries, message = "$key resolved outside chapter bucket: $resolved")
        }
    }

    private companion object {
        const val MIN_EXPECTED_ENTRIES: Int = 3
    }
}
