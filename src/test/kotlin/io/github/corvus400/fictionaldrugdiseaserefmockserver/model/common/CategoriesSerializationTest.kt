package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoriesSerializationTest {
    @Test
    fun `AtcEntry serializes with keys code, label`() {
        val entry = AtcEntry(code = "A10BA02", label = "メトホルミン")
        val json = AppJson.encodeToString(entry)
        assertEquals("""{"code":"A10BA02","label":"メトホルミン"}""", json)
    }

    @Test
    fun `TherapeuticCategoryEntry serializes with keys id, label`() {
        val entry = TherapeuticCategoryEntry(id = "diabetes", label = "糖尿病用薬")
        val json = AppJson.encodeToString(entry)
        assertEquals("""{"id":"diabetes","label":"糖尿病用薬"}""", json)
    }

    @Test
    fun `Icd10ChapterEntry serializes with keys roman, code, label`() {
        val entry = Icd10ChapterEntry(roman = "I", code = "A00-B99", label = "感染症及び寄生虫症")
        val json = AppJson.encodeToString(entry)
        assertEquals(
            """{"roman":"I","code":"A00-B99","label":"感染症及び寄生虫症"}""",
            json,
        )
    }

    @Test
    fun `CategoriesResponse serializes to JSON with 7 top-level snake_case keys`() {
        val response = CategoriesResponse(
            atc = emptyList(),
            therapeuticCategories = emptyList(),
            routeOfAdministration = emptyList(),
            dosageForm = emptyList(),
            regulatoryClass = emptyList(),
            icd10Chapters = emptyList(),
            medicalDepartments = emptyList(),
        )
        val json = AppJson.encodeToString(response)
        assertEquals(
            """{"atc":[],"therapeutic_categories":[],"route_of_administration":[],""" +
                """"dosage_form":[],"regulatory_class":[],"icd10_chapters":[],""" +
                """"medical_departments":[]}""",
            json,
        )
    }
}
