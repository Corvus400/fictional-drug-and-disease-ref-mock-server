package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlin.test.Test
import kotlin.test.assertEquals

class BucketContextKeyTest {
    @Test
    fun `DrugContext stores ATC initial`() {
        assertEquals(
            expected = 'L',
            actual = BucketContextKey.DrugContext(atcInitial = 'L').atcInitial,
        )
    }

    @Test
    fun `DiseaseContext stores ICD-10 chapter`() {
        assertEquals(
            expected = Icd10Chapter.CHAPTER_IX,
            actual = BucketContextKey.DiseaseContext(chapter = Icd10Chapter.CHAPTER_IX).chapter,
        )
    }
}
