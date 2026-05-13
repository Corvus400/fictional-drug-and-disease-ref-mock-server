package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TreatmentVocabularyTest {
    @Test
    fun `treatment vocabulary covers every treatment key for every ICD10 chapter`() {
        val firstMissing =
            TreatmentVocabulary.keys
                .flatMap { key ->
                    Icd10Chapter.entries.mapNotNull { chapter ->
                        val entries = TreatmentVocabulary.entriesFor(key = key, chapter = chapter)
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
    fun `chapter V specialist referral resolves to psychiatric domain`() {
        val values =
            (0L until 20L).map { seed ->
                DiseaseMedicalVocabulary.resolve(
                    key = "specialistReferral",
                    seed = seed,
                    context = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_V),
                )
            }

        assertTrue(actual = "精神科" in values)
    }

    @Test
    fun `disease context resolves treatment keys from chapter vocabulary`() {
        TreatmentVocabulary.keys.forEach { key ->
            val entries = TreatmentVocabulary.entriesFor(key = key, chapter = Icd10Chapter.CHAPTER_X) ?: emptyList()
            val resolved =
                DiseaseMedicalVocabulary.resolve(
                    key = key,
                    seed = 42L,
                    context = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_X),
                )

            assertTrue(actual = resolved in entries, message = "$key resolved outside chapter bucket: $resolved")
        }
    }

    private companion object {
        const val MIN_EXPECTED_ENTRIES: Int = 3
    }
}
