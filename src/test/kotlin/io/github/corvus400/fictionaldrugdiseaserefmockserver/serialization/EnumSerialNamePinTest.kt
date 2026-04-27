package io.github.corvus400.fictionaldrugdiseaserefmockserver.serialization

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class EnumSerialNamePinTest {
    @Test
    fun placeholderEnsuresFileCompiles() {
        assertEquals(0, 0)
    }

    // === drug enums ===
    // worktree-A (feature/serialname-en-drug) のみがこのセクションに関数追加。
    // 追加順は alphabetical: DosageForm → DoseUnit → FrequencyBand → HepaticSeverity →
    //                       PrecautionPopulationCategory → RegulatoryClass → RenalSeverity →
    //                       RouteOfAdministration → StorageTemperature。
    // 各 enum で encode 関数 → decode 関数 の順。

    // === disease enums ===
    // worktree-B (feature/serialname-en-disease) のみがこのセクションに関数追加。
    // 追加順は alphabetical: Chronicity → ExamCategory → Icd10Chapter →
    //                       MedicalDepartment → OnsetPattern → PrevalenceUnit。
    // 各 enum で encode 関数 → decode 関数 の順。

    @Test
    fun `MedicalDepartment encodes to literal english snake_case`() {
        assertEquals("\"internal_medicine\"", AppJson.encodeToString(MedicalDepartment.INTERNAL_MEDICINE))
        assertEquals("\"cardiology\"", AppJson.encodeToString(MedicalDepartment.CARDIOLOGY))
        assertEquals("\"gastroenterology\"", AppJson.encodeToString(MedicalDepartment.GASTROENTEROLOGY))
        assertEquals("\"endocrinology\"", AppJson.encodeToString(MedicalDepartment.ENDOCRINOLOGY))
        assertEquals("\"neurology\"", AppJson.encodeToString(MedicalDepartment.NEUROLOGY))
        assertEquals("\"psychiatry\"", AppJson.encodeToString(MedicalDepartment.PSYCHIATRY))
        assertEquals("\"surgery\"", AppJson.encodeToString(MedicalDepartment.SURGERY))
        assertEquals("\"orthopedics\"", AppJson.encodeToString(MedicalDepartment.ORTHOPEDICS))
        assertEquals("\"dermatology\"", AppJson.encodeToString(MedicalDepartment.DERMATOLOGY))
        assertEquals("\"ophthalmology\"", AppJson.encodeToString(MedicalDepartment.OPHTHALMOLOGY))
        assertEquals("\"otolaryngology\"", AppJson.encodeToString(MedicalDepartment.OTOLARYNGOLOGY))
        assertEquals("\"urology\"", AppJson.encodeToString(MedicalDepartment.UROLOGY))
        assertEquals("\"gynecology\"", AppJson.encodeToString(MedicalDepartment.GYNECOLOGY))
    }

    @Test
    fun `MedicalDepartment decodes from literal english snake_case`() {
        assertEquals(
            MedicalDepartment.INTERNAL_MEDICINE,
            AppJson.decodeFromString<MedicalDepartment>("\"internal_medicine\""),
        )
        assertEquals(
            MedicalDepartment.CARDIOLOGY,
            AppJson.decodeFromString<MedicalDepartment>("\"cardiology\""),
        )
        assertEquals(
            MedicalDepartment.GASTROENTEROLOGY,
            AppJson.decodeFromString<MedicalDepartment>("\"gastroenterology\""),
        )
        assertEquals(
            MedicalDepartment.ENDOCRINOLOGY,
            AppJson.decodeFromString<MedicalDepartment>("\"endocrinology\""),
        )
        assertEquals(
            MedicalDepartment.NEUROLOGY,
            AppJson.decodeFromString<MedicalDepartment>("\"neurology\""),
        )
        assertEquals(
            MedicalDepartment.PSYCHIATRY,
            AppJson.decodeFromString<MedicalDepartment>("\"psychiatry\""),
        )
        assertEquals(
            MedicalDepartment.SURGERY,
            AppJson.decodeFromString<MedicalDepartment>("\"surgery\""),
        )
        assertEquals(
            MedicalDepartment.ORTHOPEDICS,
            AppJson.decodeFromString<MedicalDepartment>("\"orthopedics\""),
        )
        assertEquals(
            MedicalDepartment.DERMATOLOGY,
            AppJson.decodeFromString<MedicalDepartment>("\"dermatology\""),
        )
        assertEquals(
            MedicalDepartment.OPHTHALMOLOGY,
            AppJson.decodeFromString<MedicalDepartment>("\"ophthalmology\""),
        )
        assertEquals(
            MedicalDepartment.OTOLARYNGOLOGY,
            AppJson.decodeFromString<MedicalDepartment>("\"otolaryngology\""),
        )
        assertEquals(
            MedicalDepartment.UROLOGY,
            AppJson.decodeFromString<MedicalDepartment>("\"urology\""),
        )
        assertEquals(
            MedicalDepartment.GYNECOLOGY,
            AppJson.decodeFromString<MedicalDepartment>("\"gynecology\""),
        )
    }
}
