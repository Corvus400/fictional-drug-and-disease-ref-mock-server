package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseEnumSerializationTest {
    @Test
    fun `Icd10Chapter CHAPTER_I serializes to JSON string chapter_i`() {
        val json = AppJson.encodeToString(Icd10Chapter.CHAPTER_I)
        assertEquals("\"chapter_i\"", json)
    }

    @Test
    fun `MedicalDepartment INTERNAL_MEDICINE serializes to JSON string internal_medicine`() {
        val json = AppJson.encodeToString(MedicalDepartment.INTERNAL_MEDICINE)
        assertEquals("\"internal_medicine\"", json)
    }

    @Test
    fun `Chronicity ACUTE serializes to JSON string acute`() {
        val json = AppJson.encodeToString(Chronicity.ACUTE)
        assertEquals("\"acute\"", json)
    }

    @Test
    fun `ExamCategory BLOOD_TEST serializes to JSON string blood_test`() {
        val json = AppJson.encodeToString(ExamCategory.BLOOD_TEST)
        assertEquals("\"blood_test\"", json)
    }

    @Test
    fun `OnsetPattern ACUTE serializes to JSON string acute`() {
        val json = AppJson.encodeToString(OnsetPattern.ACUTE)
        assertEquals("\"acute\"", json)
    }

    @Test
    fun `PrevalenceUnit PER_POPULATION serializes to JSON string per_population`() {
        val json = AppJson.encodeToString(PrevalenceUnit.PER_POPULATION)
        assertEquals("\"per_population\"", json)
    }
}
