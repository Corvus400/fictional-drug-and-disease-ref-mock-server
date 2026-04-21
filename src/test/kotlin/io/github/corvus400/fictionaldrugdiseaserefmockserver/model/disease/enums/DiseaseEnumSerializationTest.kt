package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums

import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseEnumSerializationTest {
    @Test
    fun `Icd10Chapter CHAPTER_I serializes to JSON string 感染症および寄生虫症`() {
        val json = AppJson.encodeToString(Icd10Chapter.CHAPTER_I)
        assertEquals("\"感染症および寄生虫症\"", json)
    }

    @Test
    fun `MedicalDepartment INTERNAL_MEDICINE serializes to JSON string 内科`() {
        val json = AppJson.encodeToString(MedicalDepartment.INTERNAL_MEDICINE)
        assertEquals("\"内科\"", json)
    }

    @Test
    fun `Chronicity ACUTE serializes to JSON string 急性`() {
        val json = AppJson.encodeToString(Chronicity.ACUTE)
        assertEquals("\"急性\"", json)
    }

    @Test
    fun `ExamCategory BLOOD_TEST serializes to JSON string 血液検査`() {
        val json = AppJson.encodeToString(ExamCategory.BLOOD_TEST)
        assertEquals("\"血液検査\"", json)
    }

    @Test
    fun `OnsetPattern ACUTE serializes to JSON string 急性発症`() {
        val json = AppJson.encodeToString(OnsetPattern.ACUTE)
        assertEquals("\"急性発症\"", json)
    }

    @Test
    fun `PrevalenceUnit PER_POPULATION serializes to JSON string 人口対`() {
        val json = AppJson.encodeToString(PrevalenceUnit.PER_POPULATION)
        assertEquals("\"人口対\"", json)
    }
}
