package io.github.corvus400.fictionaldrugdiseaserefmockserver.serialization

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
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
    fun `Chronicity encodes to literal english snake_case`() {
        assertEquals("\"acute\"", AppJson.encodeToString(Chronicity.ACUTE))
        assertEquals("\"subacute\"", AppJson.encodeToString(Chronicity.SUBACUTE))
        assertEquals("\"chronic\"", AppJson.encodeToString(Chronicity.CHRONIC))
        assertEquals("\"relapsing\"", AppJson.encodeToString(Chronicity.RELAPSING))
    }

    @Test
    fun `Chronicity decodes from literal english snake_case`() {
        assertEquals(
            Chronicity.ACUTE,
            AppJson.decodeFromString<Chronicity>("\"acute\""),
        )
        assertEquals(
            Chronicity.SUBACUTE,
            AppJson.decodeFromString<Chronicity>("\"subacute\""),
        )
        assertEquals(
            Chronicity.CHRONIC,
            AppJson.decodeFromString<Chronicity>("\"chronic\""),
        )
        assertEquals(
            Chronicity.RELAPSING,
            AppJson.decodeFromString<Chronicity>("\"relapsing\""),
        )
    }

    @Test
    fun `Icd10Chapter encodes to literal english snake_case`() {
        assertEquals("\"chapter_i\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_I))
        assertEquals("\"chapter_ii\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_II))
        assertEquals("\"chapter_iii\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_III))
        assertEquals("\"chapter_iv\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_IV))
        assertEquals("\"chapter_v\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_V))
        assertEquals("\"chapter_vi\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_VI))
        assertEquals("\"chapter_vii\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_VII))
        assertEquals("\"chapter_viii\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_VIII))
        assertEquals("\"chapter_ix\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_IX))
    }

    @Test
    fun `Icd10Chapter decodes from literal english snake_case`() {
        assertEquals(
            Icd10Chapter.CHAPTER_I,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_i\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_II,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_ii\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_III,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_iii\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_IV,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_iv\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_V,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_v\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_VI,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_vi\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_VII,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_vii\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_VIII,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_viii\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_IX,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_ix\""),
        )
    }

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
        assertEquals("\"pediatrics\"", AppJson.encodeToString(MedicalDepartment.PEDIATRICS))
        assertEquals("\"emergency\"", AppJson.encodeToString(MedicalDepartment.EMERGENCY))
        assertEquals("\"infectious_disease\"", AppJson.encodeToString(MedicalDepartment.INFECTIOUS_DISEASE))
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
        assertEquals(
            MedicalDepartment.PEDIATRICS,
            AppJson.decodeFromString<MedicalDepartment>("\"pediatrics\""),
        )
        assertEquals(
            MedicalDepartment.EMERGENCY,
            AppJson.decodeFromString<MedicalDepartment>("\"emergency\""),
        )
        assertEquals(
            MedicalDepartment.INFECTIOUS_DISEASE,
            AppJson.decodeFromString<MedicalDepartment>("\"infectious_disease\""),
        )
    }
}
