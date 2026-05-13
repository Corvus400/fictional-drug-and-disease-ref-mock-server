package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PrognosisVocabularyTest {
    @Test
    fun `prognosis vocabulary covers every prognosis key for every ICD10 chapter`() {
        val firstMissing =
            PrognosisVocabulary.keys
                .flatMap { key ->
                    Icd10Chapter.entries.mapNotNull { chapter ->
                        val entries = PrognosisVocabulary.entriesFor(key = key, chapter = chapter)
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
    fun `non chapter I progressed complication does not resolve to severe infection`() {
        val firstViolation =
            Icd10Chapter.entries
                .filterNot { chapter -> chapter == Icd10Chapter.CHAPTER_I }
                .mapNotNull { chapter ->
                    val values = (0L until 20L).map { seed ->
                        DiseaseMedicalVocabulary.resolve(
                            key = "progressedComplication",
                            seed = seed,
                            context = BucketContextKey.DiseaseContext(chapter = chapter),
                        )
                    }
                    values.firstOrNull { value -> value == "重症感染症" }?.let { "${chapter.chapterKey}:$it" }
                }
                .firstOrNull()

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `disease context resolves prognosis keys from chapter vocabulary`() {
        PrognosisVocabulary.keys.forEach { key ->
            val entries = PrognosisVocabulary.entriesFor(key = key, chapter = Icd10Chapter.CHAPTER_IX) ?: emptyList()
            val resolved =
                DiseaseMedicalVocabulary.resolve(
                    key = key,
                    seed = 42L,
                    context = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_IX),
                )

            assertTrue(actual = resolved in entries, message = "$key resolved outside chapter bucket: $resolved")
        }
    }

    private companion object {
        const val MIN_EXPECTED_ENTRIES: Int = 3
    }
}
