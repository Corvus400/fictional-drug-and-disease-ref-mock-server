package io.github.corvus400.fictionaldrugdiseaserefmockserver.serialization

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.PrevalenceUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.HepaticSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.PrecautionPopulationCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RenalSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.AppJson
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

    @Test
    fun `DosageForm encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                DosageForm.TABLET to "tablet",
                DosageForm.CAPSULE to "capsule",
                DosageForm.POWDER to "powder",
                DosageForm.GRANULE to "granule",
                DosageForm.LIQUID to "liquid",
                DosageForm.INJECTION_FORM to "injection_form",
                DosageForm.OINTMENT to "ointment",
                DosageForm.CREAM to "cream",
                DosageForm.PATCH to "patch",
                DosageForm.EYE_DROPS to "eye_drops",
                DosageForm.SUPPOSITORY to "suppository",
                DosageForm.INHALER to "inhaler",
                DosageForm.NASAL_SPRAY to "nasal_spray",
            ),
        )
    }

    @Test
    fun `DosageForm decodes from literal english snake_case`() {
        assertDecodedSerialNames<DosageForm>(
            mapOf(
                "tablet" to DosageForm.TABLET,
                "capsule" to DosageForm.CAPSULE,
                "powder" to DosageForm.POWDER,
                "granule" to DosageForm.GRANULE,
                "liquid" to DosageForm.LIQUID,
                "injection_form" to DosageForm.INJECTION_FORM,
                "ointment" to DosageForm.OINTMENT,
                "cream" to DosageForm.CREAM,
                "patch" to DosageForm.PATCH,
                "eye_drops" to DosageForm.EYE_DROPS,
                "suppository" to DosageForm.SUPPOSITORY,
                "inhaler" to DosageForm.INHALER,
                "nasal_spray" to DosageForm.NASAL_SPRAY,
            ),
        )
    }

    @Test
    fun `DoseUnit encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                DoseUnit.MG to "mg",
                DoseUnit.G to "g",
                DoseUnit.MICROGRAM to "microgram",
                DoseUnit.ML to "ml",
                DoseUnit.L to "l",
                DoseUnit.IU to "iu",
                DoseUnit.MEQ to "meq",
                DoseUnit.MOL to "mol",
                DoseUnit.MMOL to "mmol",
                DoseUnit.PERCENT to "percent",
            ),
        )
    }

    @Test
    fun `DoseUnit decodes from literal english snake_case`() {
        assertDecodedSerialNames<DoseUnit>(
            mapOf(
                "mg" to DoseUnit.MG,
                "g" to DoseUnit.G,
                "microgram" to DoseUnit.MICROGRAM,
                "ml" to DoseUnit.ML,
                "l" to DoseUnit.L,
                "iu" to DoseUnit.IU,
                "meq" to DoseUnit.MEQ,
                "mol" to DoseUnit.MOL,
                "mmol" to DoseUnit.MMOL,
                "percent" to DoseUnit.PERCENT,
            ),
        )
    }

    @Test
    fun `FrequencyBand encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                FrequencyBand.OVER_5_PERCENT to "over_5_percent",
                FrequencyBand.BETWEEN_1_AND_5_PERCENT to "between_1_and_5_percent",
                FrequencyBand.UNDER_1_PERCENT to "under_1_percent",
                FrequencyBand.UNKNOWN to "unknown",
            ),
        )
    }

    @Test
    fun `FrequencyBand decodes from literal english snake_case`() {
        assertDecodedSerialNames<FrequencyBand>(
            mapOf(
                "over_5_percent" to FrequencyBand.OVER_5_PERCENT,
                "between_1_and_5_percent" to FrequencyBand.BETWEEN_1_AND_5_PERCENT,
                "under_1_percent" to FrequencyBand.UNDER_1_PERCENT,
                "unknown" to FrequencyBand.UNKNOWN,
            ),
        )
    }

    @Test
    fun `HepaticSeverity encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                HepaticSeverity.MILD to "mild",
                HepaticSeverity.MODERATE to "moderate",
                HepaticSeverity.SEVERE to "severe",
            ),
        )
    }

    @Test
    fun `HepaticSeverity decodes from literal english snake_case`() {
        assertDecodedSerialNames<HepaticSeverity>(
            mapOf(
                "mild" to HepaticSeverity.MILD,
                "moderate" to HepaticSeverity.MODERATE,
                "severe" to HepaticSeverity.SEVERE,
            ),
        )
    }

    @Test
    fun `PrecautionPopulationCategory encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                PrecautionPopulationCategory.COMORBIDITY to "comorbidity",
                PrecautionPopulationCategory.RENAL_IMPAIRMENT to "renal_impairment",
                PrecautionPopulationCategory.HEPATIC_IMPAIRMENT to "hepatic_impairment",
                PrecautionPopulationCategory.REPRODUCTIVE_POTENTIAL to "reproductive_potential",
                PrecautionPopulationCategory.PREGNANT to "pregnant",
                PrecautionPopulationCategory.LACTATING to "lactating",
                PrecautionPopulationCategory.PEDIATRIC to "pediatric",
                PrecautionPopulationCategory.GERIATRIC to "geriatric",
            ),
        )
    }

    @Test
    fun `PrecautionPopulationCategory decodes from literal english snake_case`() {
        assertDecodedSerialNames<PrecautionPopulationCategory>(
            mapOf(
                "comorbidity" to PrecautionPopulationCategory.COMORBIDITY,
                "renal_impairment" to PrecautionPopulationCategory.RENAL_IMPAIRMENT,
                "hepatic_impairment" to PrecautionPopulationCategory.HEPATIC_IMPAIRMENT,
                "reproductive_potential" to PrecautionPopulationCategory.REPRODUCTIVE_POTENTIAL,
                "pregnant" to PrecautionPopulationCategory.PREGNANT,
                "lactating" to PrecautionPopulationCategory.LACTATING,
                "pediatric" to PrecautionPopulationCategory.PEDIATRIC,
                "geriatric" to PrecautionPopulationCategory.GERIATRIC,
            ),
        )
    }

    @Test
    fun `RegulatoryClass encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                RegulatoryClass.POISON to "poison",
                RegulatoryClass.POTENT to "potent",
                RegulatoryClass.ORDINARY to "ordinary",
                RegulatoryClass.PSYCHOTROPIC_1 to "psychotropic_1",
                RegulatoryClass.PSYCHOTROPIC_2 to "psychotropic_2",
                RegulatoryClass.PSYCHOTROPIC_3 to "psychotropic_3",
                RegulatoryClass.NARCOTIC to "narcotic",
                RegulatoryClass.STIMULANT_PRECURSOR to "stimulant_precursor",
                RegulatoryClass.BIOLOGICAL to "biological",
                RegulatoryClass.SPECIFIED_BIOLOGICAL to "specified_biological",
                RegulatoryClass.PRESCRIPTION_REQUIRED to "prescription_required",
            ),
        )
    }

    @Test
    fun `RegulatoryClass decodes from literal english snake_case`() {
        assertDecodedSerialNames<RegulatoryClass>(
            mapOf(
                "poison" to RegulatoryClass.POISON,
                "potent" to RegulatoryClass.POTENT,
                "ordinary" to RegulatoryClass.ORDINARY,
                "psychotropic_1" to RegulatoryClass.PSYCHOTROPIC_1,
                "psychotropic_2" to RegulatoryClass.PSYCHOTROPIC_2,
                "psychotropic_3" to RegulatoryClass.PSYCHOTROPIC_3,
                "narcotic" to RegulatoryClass.NARCOTIC,
                "stimulant_precursor" to RegulatoryClass.STIMULANT_PRECURSOR,
                "biological" to RegulatoryClass.BIOLOGICAL,
                "specified_biological" to RegulatoryClass.SPECIFIED_BIOLOGICAL,
                "prescription_required" to RegulatoryClass.PRESCRIPTION_REQUIRED,
            ),
        )
    }

    @Test
    fun `RenalSeverity encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                RenalSeverity.NORMAL to "normal",
                RenalSeverity.MILD to "mild",
                RenalSeverity.MODERATE to "moderate",
                RenalSeverity.SEVERE to "severe",
                RenalSeverity.END_STAGE to "end_stage",
            ),
        )
    }

    @Test
    fun `RenalSeverity decodes from literal english snake_case`() {
        assertDecodedSerialNames<RenalSeverity>(
            mapOf(
                "normal" to RenalSeverity.NORMAL,
                "mild" to RenalSeverity.MILD,
                "moderate" to RenalSeverity.MODERATE,
                "severe" to RenalSeverity.SEVERE,
                "end_stage" to RenalSeverity.END_STAGE,
            ),
        )
    }

    @Test
    fun `RouteOfAdministration encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                RouteOfAdministration.ORAL to "oral",
                RouteOfAdministration.TOPICAL to "topical",
                RouteOfAdministration.INJECTION_ROUTE to "injection_route",
                RouteOfAdministration.INHALATION to "inhalation",
                RouteOfAdministration.RECTAL to "rectal",
                RouteOfAdministration.OPHTHALMIC to "ophthalmic",
                RouteOfAdministration.NASAL to "nasal",
                RouteOfAdministration.TRANSDERMAL to "transdermal",
            ),
        )
    }

    @Test
    fun `RouteOfAdministration decodes from literal english snake_case`() {
        assertDecodedSerialNames<RouteOfAdministration>(
            mapOf(
                "oral" to RouteOfAdministration.ORAL,
                "topical" to RouteOfAdministration.TOPICAL,
                "injection_route" to RouteOfAdministration.INJECTION_ROUTE,
                "inhalation" to RouteOfAdministration.INHALATION,
                "rectal" to RouteOfAdministration.RECTAL,
                "ophthalmic" to RouteOfAdministration.OPHTHALMIC,
                "nasal" to RouteOfAdministration.NASAL,
                "transdermal" to RouteOfAdministration.TRANSDERMAL,
            ),
        )
    }

    @Test
    fun `StorageTemperature encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                StorageTemperature.ROOM_TEMPERATURE to "room_temperature",
                StorageTemperature.COLD to "cold",
                StorageTemperature.FROZEN to "frozen",
            ),
        )
    }

    @Test
    fun `StorageTemperature decodes from literal english snake_case`() {
        assertDecodedSerialNames<StorageTemperature>(
            mapOf(
                "room_temperature" to StorageTemperature.ROOM_TEMPERATURE,
                "cold" to StorageTemperature.COLD,
                "frozen" to StorageTemperature.FROZEN,
            ),
        )
    }

    // === disease enums ===
    // worktree-B (feature/serialname-en-disease) のみがこのセクションに関数追加。
    // 追加順は alphabetical: Chronicity → ExamCategory → Icd10Chapter →
    //                       MedicalDepartment → OnsetPattern → PrevalenceUnit。
    // 各 enum で encode 関数 → decode 関数 の順。

    @Test
    fun `Chronicity encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                Chronicity.ACUTE to "acute",
                Chronicity.SUBACUTE to "subacute",
                Chronicity.CHRONIC to "chronic",
                Chronicity.RELAPSING to "relapsing",
            ),
        )
    }

    @Test
    fun `Chronicity decodes from literal english snake_case`() {
        assertDecodedSerialNames<Chronicity>(
            mapOf(
                "acute" to Chronicity.ACUTE,
                "subacute" to Chronicity.SUBACUTE,
                "chronic" to Chronicity.CHRONIC,
                "relapsing" to Chronicity.RELAPSING,
            ),
        )
    }

    @Test
    fun `ExamCategory encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                ExamCategory.BLOOD_TEST to "blood_test",
                ExamCategory.IMAGING to "imaging",
                ExamCategory.PHYSIOLOGICAL to "physiological",
                ExamCategory.PATHOLOGY to "pathology",
                ExamCategory.INTERVIEW to "interview",
            ),
        )
    }

    @Test
    fun `ExamCategory decodes from literal english snake_case`() {
        assertDecodedSerialNames<ExamCategory>(
            mapOf(
                "blood_test" to ExamCategory.BLOOD_TEST,
                "imaging" to ExamCategory.IMAGING,
                "physiological" to ExamCategory.PHYSIOLOGICAL,
                "pathology" to ExamCategory.PATHOLOGY,
                "interview" to ExamCategory.INTERVIEW,
            ),
        )
    }

    @Test
    fun `Icd10Chapter encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                Icd10Chapter.CHAPTER_I to "chapter_i",
                Icd10Chapter.CHAPTER_II to "chapter_ii",
                Icd10Chapter.CHAPTER_III to "chapter_iii",
                Icd10Chapter.CHAPTER_IV to "chapter_iv",
                Icd10Chapter.CHAPTER_V to "chapter_v",
                Icd10Chapter.CHAPTER_VI to "chapter_vi",
                Icd10Chapter.CHAPTER_VII to "chapter_vii",
                Icd10Chapter.CHAPTER_VIII to "chapter_viii",
                Icd10Chapter.CHAPTER_IX to "chapter_ix",
                Icd10Chapter.CHAPTER_X to "chapter_x",
                Icd10Chapter.CHAPTER_XI to "chapter_xi",
                Icd10Chapter.CHAPTER_XII to "chapter_xii",
                Icd10Chapter.CHAPTER_XIII to "chapter_xiii",
                Icd10Chapter.CHAPTER_XIV to "chapter_xiv",
                Icd10Chapter.CHAPTER_XV to "chapter_xv",
                Icd10Chapter.CHAPTER_XVI to "chapter_xvi",
                Icd10Chapter.CHAPTER_XVII to "chapter_xvii",
                Icd10Chapter.CHAPTER_XVIII to "chapter_xviii",
                Icd10Chapter.CHAPTER_XIX to "chapter_xix",
                Icd10Chapter.CHAPTER_XX to "chapter_xx",
                Icd10Chapter.CHAPTER_XXI to "chapter_xxi",
                Icd10Chapter.CHAPTER_XXII to "chapter_xxii",
            ),
        )
    }

    @Test
    fun `Icd10Chapter decodes from literal english snake_case`() {
        assertDecodedSerialNames<Icd10Chapter>(
            mapOf(
                "chapter_i" to Icd10Chapter.CHAPTER_I,
                "chapter_ii" to Icd10Chapter.CHAPTER_II,
                "chapter_iii" to Icd10Chapter.CHAPTER_III,
                "chapter_iv" to Icd10Chapter.CHAPTER_IV,
                "chapter_v" to Icd10Chapter.CHAPTER_V,
                "chapter_vi" to Icd10Chapter.CHAPTER_VI,
                "chapter_vii" to Icd10Chapter.CHAPTER_VII,
                "chapter_viii" to Icd10Chapter.CHAPTER_VIII,
                "chapter_ix" to Icd10Chapter.CHAPTER_IX,
                "chapter_x" to Icd10Chapter.CHAPTER_X,
                "chapter_xi" to Icd10Chapter.CHAPTER_XI,
                "chapter_xii" to Icd10Chapter.CHAPTER_XII,
                "chapter_xiii" to Icd10Chapter.CHAPTER_XIII,
                "chapter_xiv" to Icd10Chapter.CHAPTER_XIV,
                "chapter_xv" to Icd10Chapter.CHAPTER_XV,
                "chapter_xvi" to Icd10Chapter.CHAPTER_XVI,
                "chapter_xvii" to Icd10Chapter.CHAPTER_XVII,
                "chapter_xviii" to Icd10Chapter.CHAPTER_XVIII,
                "chapter_xix" to Icd10Chapter.CHAPTER_XIX,
                "chapter_xx" to Icd10Chapter.CHAPTER_XX,
                "chapter_xxi" to Icd10Chapter.CHAPTER_XXI,
                "chapter_xxii" to Icd10Chapter.CHAPTER_XXII,
            ),
        )
    }

    @Test
    fun `MedicalDepartment encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                MedicalDepartment.INTERNAL_MEDICINE to "internal_medicine",
                MedicalDepartment.CARDIOLOGY to "cardiology",
                MedicalDepartment.GASTROENTEROLOGY to "gastroenterology",
                MedicalDepartment.ENDOCRINOLOGY to "endocrinology",
                MedicalDepartment.NEUROLOGY to "neurology",
                MedicalDepartment.PSYCHIATRY to "psychiatry",
                MedicalDepartment.SURGERY to "surgery",
                MedicalDepartment.ORTHOPEDICS to "orthopedics",
                MedicalDepartment.DERMATOLOGY to "dermatology",
                MedicalDepartment.OPHTHALMOLOGY to "ophthalmology",
                MedicalDepartment.OTOLARYNGOLOGY to "otolaryngology",
                MedicalDepartment.UROLOGY to "urology",
                MedicalDepartment.GYNECOLOGY to "gynecology",
                MedicalDepartment.PEDIATRICS to "pediatrics",
                MedicalDepartment.EMERGENCY to "emergency",
                MedicalDepartment.INFECTIOUS_DISEASE to "infectious_disease",
            ),
        )
    }

    @Test
    fun `MedicalDepartment decodes from literal english snake_case`() {
        assertDecodedSerialNames<MedicalDepartment>(
            mapOf(
                "internal_medicine" to MedicalDepartment.INTERNAL_MEDICINE,
                "cardiology" to MedicalDepartment.CARDIOLOGY,
                "gastroenterology" to MedicalDepartment.GASTROENTEROLOGY,
                "endocrinology" to MedicalDepartment.ENDOCRINOLOGY,
                "neurology" to MedicalDepartment.NEUROLOGY,
                "psychiatry" to MedicalDepartment.PSYCHIATRY,
                "surgery" to MedicalDepartment.SURGERY,
                "orthopedics" to MedicalDepartment.ORTHOPEDICS,
                "dermatology" to MedicalDepartment.DERMATOLOGY,
                "ophthalmology" to MedicalDepartment.OPHTHALMOLOGY,
                "otolaryngology" to MedicalDepartment.OTOLARYNGOLOGY,
                "urology" to MedicalDepartment.UROLOGY,
                "gynecology" to MedicalDepartment.GYNECOLOGY,
                "pediatrics" to MedicalDepartment.PEDIATRICS,
                "emergency" to MedicalDepartment.EMERGENCY,
                "infectious_disease" to MedicalDepartment.INFECTIOUS_DISEASE,
            ),
        )
    }

    @Test
    fun `OnsetPattern encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                OnsetPattern.ACUTE to "acute",
                OnsetPattern.SUBACUTE to "subacute",
                OnsetPattern.CHRONIC to "chronic",
                OnsetPattern.INTERMITTENT to "intermittent",
                OnsetPattern.RELAPSING to "relapsing",
            ),
        )
    }

    @Test
    fun `OnsetPattern decodes from literal english snake_case`() {
        assertDecodedSerialNames<OnsetPattern>(
            mapOf(
                "acute" to OnsetPattern.ACUTE,
                "subacute" to OnsetPattern.SUBACUTE,
                "chronic" to OnsetPattern.CHRONIC,
                "intermittent" to OnsetPattern.INTERMITTENT,
                "relapsing" to OnsetPattern.RELAPSING,
            ),
        )
    }

    @Test
    fun `PrevalenceUnit encodes to literal english snake_case`() {
        assertEncodedSerialNames(
            mapOf(
                PrevalenceUnit.PER_POPULATION to "per_population",
                PrevalenceUnit.PER_PATIENT to "per_patient",
                PrevalenceUnit.PER_BIRTH to "per_birth",
            ),
        )
    }

    @Test
    fun `PrevalenceUnit decodes from literal english snake_case`() {
        assertDecodedSerialNames<PrevalenceUnit>(
            mapOf(
                "per_population" to PrevalenceUnit.PER_POPULATION,
                "per_patient" to PrevalenceUnit.PER_PATIENT,
                "per_birth" to PrevalenceUnit.PER_BIRTH,
            ),
        )
    }

    private inline fun <reified T> assertEncodedSerialNames(expected: Map<T, String>) {
        val actual = expected.keys.associateWith { value ->
            AppJson.encodeToString(value).removeSurrounding("\"")
        }

        assertEquals(
            expected = expected,
            actual = actual,
            message = "${T::class.simpleName} encoded serial names must stay pinned",
        )
    }

    private inline fun <reified T> assertDecodedSerialNames(expected: Map<String, T>) {
        val actual = expected.keys.associateWith { serialName ->
            AppJson.decodeFromString<T>("\"$serialName\"")
        }

        assertEquals(
            expected = expected,
            actual = actual,
            message = "${T::class.simpleName} decoded serial names must stay pinned",
        )
    }
}
