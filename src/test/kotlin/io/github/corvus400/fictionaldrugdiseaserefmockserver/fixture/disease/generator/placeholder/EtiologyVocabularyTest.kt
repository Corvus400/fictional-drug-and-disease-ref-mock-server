package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EtiologyVocabularyTest {
    @Test
    fun `etiology vocabulary covers every overview key for every ICD10 chapter`() {
        val firstMissing =
            EtiologyVocabulary.keys
                .flatMap { key ->
                    Icd10Chapter.entries.mapNotNull { chapter ->
                        val entries = EtiologyVocabulary.entriesFor(key = key, chapter = chapter)
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
    fun `non chapter I etiology categories do not resolve to infection category`() {
        val firstViolation =
            Icd10Chapter.entries
                .filterNot { chapter -> chapter == Icd10Chapter.CHAPTER_I }
                .mapNotNull { chapter ->
                    val values = (0L until 20L).map { seed ->
                        DiseaseMedicalVocabulary.resolve(
                            key = "etiologyCategory",
                            seed = seed,
                            context = BucketContextKey.DiseaseContext(chapter = chapter),
                        )
                    }
                    values.firstOrNull { value -> value == "感染性疾患" }?.let { "${chapter.chapterKey}:$it" }
                }
                .firstOrNull()

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `disease context resolves overview keys from chapter vocabulary`() {
        EtiologyVocabulary.keys.forEach { key ->
            val entries = EtiologyVocabulary.entriesFor(key = key, chapter = Icd10Chapter.CHAPTER_X).orEmpty()
            val resolved =
                DiseaseMedicalVocabulary.resolve(
                    key = key,
                    seed = 42L,
                    context = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_X),
                )

            assertNotNull(actual = entries)
            assertTrue(actual = resolved in entries, message = "$key resolved outside chapter bucket: $resolved")
        }
    }

    @Test
    fun `global context keeps legacy vocabulary fallback`() {
        val resolved =
            DiseaseMedicalVocabulary.resolve(
                key = "etiologyCategory",
                seed = 42L,
                context = BucketContextKey.Global,
            )

        assertFalse(
            actual =
            resolved in EtiologyVocabulary.entriesFor("etiologyCategory", Icd10Chapter.CHAPTER_X).orEmpty()
        )
    }

    private companion object {
        const val MIN_EXPECTED_ENTRIES: Int = 3
    }
}
