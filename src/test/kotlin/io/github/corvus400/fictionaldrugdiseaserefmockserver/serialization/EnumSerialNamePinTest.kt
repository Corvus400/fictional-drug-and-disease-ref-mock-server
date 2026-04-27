package io.github.corvus400.fictionaldrugdiseaserefmockserver.serialization

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
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
    fun `ExamCategory encodes to literal english snake_case`() {
        assertEquals("\"blood_test\"", AppJson.encodeToString(ExamCategory.BLOOD_TEST))
        assertEquals("\"imaging\"", AppJson.encodeToString(ExamCategory.IMAGING))
        assertEquals("\"physiological\"", AppJson.encodeToString(ExamCategory.PHYSIOLOGICAL))
        assertEquals("\"pathology\"", AppJson.encodeToString(ExamCategory.PATHOLOGY))
        assertEquals("\"interview\"", AppJson.encodeToString(ExamCategory.INTERVIEW))
    }

    @Test
    fun `ExamCategory decodes from literal english snake_case`() {
        assertEquals(
            ExamCategory.BLOOD_TEST,
            AppJson.decodeFromString<ExamCategory>("\"blood_test\""),
        )
        assertEquals(
            ExamCategory.IMAGING,
            AppJson.decodeFromString<ExamCategory>("\"imaging\""),
        )
        assertEquals(
            ExamCategory.PHYSIOLOGICAL,
            AppJson.decodeFromString<ExamCategory>("\"physiological\""),
        )
        assertEquals(
            ExamCategory.PATHOLOGY,
            AppJson.decodeFromString<ExamCategory>("\"pathology\""),
        )
        assertEquals(
            ExamCategory.INTERVIEW,
            AppJson.decodeFromString<ExamCategory>("\"interview\""),
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
        assertEquals("\"chapter_x\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_X))
        assertEquals("\"chapter_xi\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XI))
        assertEquals("\"chapter_xii\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XII))
        assertEquals("\"chapter_xiii\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XIII))
        assertEquals("\"chapter_xiv\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XIV))
        assertEquals("\"chapter_xv\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XV))
        assertEquals("\"chapter_xvi\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XVI))
        assertEquals("\"chapter_xvii\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XVII))
        assertEquals("\"chapter_xviii\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XVIII))
        assertEquals("\"chapter_xix\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XIX))
        assertEquals("\"chapter_xx\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XX))
        assertEquals("\"chapter_xxi\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XXI))
        assertEquals("\"chapter_xxii\"", AppJson.encodeToString(Icd10Chapter.CHAPTER_XXII))
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
        assertEquals(
            Icd10Chapter.CHAPTER_X,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_x\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XI,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xi\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XII,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xii\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XIII,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xiii\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XIV,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xiv\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XV,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xv\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XVI,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xvi\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XVII,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xvii\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XVIII,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xviii\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XIX,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xix\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XX,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xx\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XXI,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xxi\""),
        )
        assertEquals(
            Icd10Chapter.CHAPTER_XXII,
            AppJson.decodeFromString<Icd10Chapter>("\"chapter_xxii\""),
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

    @Test
    fun `OnsetPattern encodes to literal english snake_case`() {
        assertEquals("\"acute\"", AppJson.encodeToString(OnsetPattern.ACUTE))
        assertEquals("\"subacute\"", AppJson.encodeToString(OnsetPattern.SUBACUTE))
        assertEquals("\"chronic\"", AppJson.encodeToString(OnsetPattern.CHRONIC))
    }

    @Test
    fun `OnsetPattern decodes from literal english snake_case`() {
        assertEquals(
            OnsetPattern.ACUTE,
            AppJson.decodeFromString<OnsetPattern>("\"acute\""),
        )
        assertEquals(
            OnsetPattern.SUBACUTE,
            AppJson.decodeFromString<OnsetPattern>("\"subacute\""),
        )
        assertEquals(
            OnsetPattern.CHRONIC,
            AppJson.decodeFromString<OnsetPattern>("\"chronic\""),
        )
    }
}
