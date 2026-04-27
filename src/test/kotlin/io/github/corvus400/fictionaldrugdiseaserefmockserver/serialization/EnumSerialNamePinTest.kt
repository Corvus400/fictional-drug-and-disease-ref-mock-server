package io.github.corvus400.fictionaldrugdiseaserefmockserver.serialization

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.FrequencyBand
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.HepaticSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import kotlinx.serialization.json.Json
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
        assertEquals(
            "\"tablet\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.TABLET),
            "DosageForm.TABLET",
        )
        assertEquals(
            "\"capsule\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.CAPSULE),
            "DosageForm.CAPSULE",
        )
        assertEquals(
            "\"powder\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.POWDER),
            "DosageForm.POWDER",
        )
        assertEquals(
            "\"granule\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.GRANULE),
            "DosageForm.GRANULE",
        )
        assertEquals(
            "\"liquid\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.LIQUID),
            "DosageForm.LIQUID",
        )
        assertEquals(
            "\"injection_form\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.INJECTION_FORM),
            "DosageForm.INJECTION_FORM",
        )
        assertEquals(
            "\"ointment\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.OINTMENT),
            "DosageForm.OINTMENT",
        )
        assertEquals(
            "\"cream\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.CREAM),
            "DosageForm.CREAM",
        )
        assertEquals(
            "\"patch\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.PATCH),
            "DosageForm.PATCH",
        )
        assertEquals(
            "\"eye_drops\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.EYE_DROPS),
            "DosageForm.EYE_DROPS",
        )
        assertEquals(
            "\"suppository\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.SUPPOSITORY),
            "DosageForm.SUPPOSITORY",
        )
        assertEquals(
            "\"inhaler\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.INHALER),
            "DosageForm.INHALER",
        )
        assertEquals(
            "\"nasal_spray\"",
            Json.encodeToString(DosageForm.serializer(), DosageForm.NASAL_SPRAY),
            "DosageForm.NASAL_SPRAY",
        )
    }

    @Test
    fun `DosageForm decodes from literal english snake_case`() {
        assertEquals(
            DosageForm.TABLET,
            Json.decodeFromString(DosageForm.serializer(), "\"tablet\""),
            "DosageForm.TABLET",
        )
        assertEquals(
            DosageForm.CAPSULE,
            Json.decodeFromString(DosageForm.serializer(), "\"capsule\""),
            "DosageForm.CAPSULE",
        )
        assertEquals(
            DosageForm.POWDER,
            Json.decodeFromString(DosageForm.serializer(), "\"powder\""),
            "DosageForm.POWDER",
        )
        assertEquals(
            DosageForm.GRANULE,
            Json.decodeFromString(DosageForm.serializer(), "\"granule\""),
            "DosageForm.GRANULE",
        )
        assertEquals(
            DosageForm.LIQUID,
            Json.decodeFromString(DosageForm.serializer(), "\"liquid\""),
            "DosageForm.LIQUID",
        )
        assertEquals(
            DosageForm.INJECTION_FORM,
            Json.decodeFromString(DosageForm.serializer(), "\"injection_form\""),
            "DosageForm.INJECTION_FORM",
        )
        assertEquals(
            DosageForm.OINTMENT,
            Json.decodeFromString(DosageForm.serializer(), "\"ointment\""),
            "DosageForm.OINTMENT",
        )
        assertEquals(
            DosageForm.CREAM,
            Json.decodeFromString(DosageForm.serializer(), "\"cream\""),
            "DosageForm.CREAM",
        )
        assertEquals(
            DosageForm.PATCH,
            Json.decodeFromString(DosageForm.serializer(), "\"patch\""),
            "DosageForm.PATCH",
        )
        assertEquals(
            DosageForm.EYE_DROPS,
            Json.decodeFromString(DosageForm.serializer(), "\"eye_drops\""),
            "DosageForm.EYE_DROPS",
        )
        assertEquals(
            DosageForm.SUPPOSITORY,
            Json.decodeFromString(DosageForm.serializer(), "\"suppository\""),
            "DosageForm.SUPPOSITORY",
        )
        assertEquals(
            DosageForm.INHALER,
            Json.decodeFromString(DosageForm.serializer(), "\"inhaler\""),
            "DosageForm.INHALER",
        )
        assertEquals(
            DosageForm.NASAL_SPRAY,
            Json.decodeFromString(DosageForm.serializer(), "\"nasal_spray\""),
            "DosageForm.NASAL_SPRAY",
        )
    }

    @Test
    fun `DoseUnit encodes to literal english snake_case`() {
        assertEquals(
            "\"mg\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.MG),
            "DoseUnit.MG",
        )
        assertEquals(
            "\"g\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.G),
            "DoseUnit.G",
        )
        assertEquals(
            "\"microgram\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.MICROGRAM),
            "DoseUnit.MICROGRAM",
        )
        assertEquals(
            "\"ml\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.ML),
            "DoseUnit.ML",
        )
        assertEquals(
            "\"l\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.L),
            "DoseUnit.L",
        )
        assertEquals(
            "\"iu\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.IU),
            "DoseUnit.IU",
        )
        assertEquals(
            "\"meq\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.MEQ),
            "DoseUnit.MEQ",
        )
        assertEquals(
            "\"mol\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.MOL),
            "DoseUnit.MOL",
        )
        assertEquals(
            "\"mmol\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.MMOL),
            "DoseUnit.MMOL",
        )
        assertEquals(
            "\"percent\"",
            Json.encodeToString(DoseUnit.serializer(), DoseUnit.PERCENT),
            "DoseUnit.PERCENT",
        )
    }

    @Test
    fun `DoseUnit decodes from literal english snake_case`() {
        assertEquals(
            DoseUnit.MG,
            Json.decodeFromString(DoseUnit.serializer(), "\"mg\""),
            "DoseUnit.MG",
        )
        assertEquals(
            DoseUnit.G,
            Json.decodeFromString(DoseUnit.serializer(), "\"g\""),
            "DoseUnit.G",
        )
        assertEquals(
            DoseUnit.MICROGRAM,
            Json.decodeFromString(DoseUnit.serializer(), "\"microgram\""),
            "DoseUnit.MICROGRAM",
        )
        assertEquals(
            DoseUnit.ML,
            Json.decodeFromString(DoseUnit.serializer(), "\"ml\""),
            "DoseUnit.ML",
        )
        assertEquals(
            DoseUnit.L,
            Json.decodeFromString(DoseUnit.serializer(), "\"l\""),
            "DoseUnit.L",
        )
        assertEquals(
            DoseUnit.IU,
            Json.decodeFromString(DoseUnit.serializer(), "\"iu\""),
            "DoseUnit.IU",
        )
        assertEquals(
            DoseUnit.MEQ,
            Json.decodeFromString(DoseUnit.serializer(), "\"meq\""),
            "DoseUnit.MEQ",
        )
        assertEquals(
            DoseUnit.MOL,
            Json.decodeFromString(DoseUnit.serializer(), "\"mol\""),
            "DoseUnit.MOL",
        )
        assertEquals(
            DoseUnit.MMOL,
            Json.decodeFromString(DoseUnit.serializer(), "\"mmol\""),
            "DoseUnit.MMOL",
        )
        assertEquals(
            DoseUnit.PERCENT,
            Json.decodeFromString(DoseUnit.serializer(), "\"percent\""),
            "DoseUnit.PERCENT",
        )
    }

    @Test
    fun `FrequencyBand encodes to literal english snake_case`() {
        assertEquals(
            "\"over_5_percent\"",
            Json.encodeToString(FrequencyBand.serializer(), FrequencyBand.OVER_5_PERCENT),
            "FrequencyBand.OVER_5_PERCENT",
        )
        assertEquals(
            "\"between_1_and_5_percent\"",
            Json.encodeToString(FrequencyBand.serializer(), FrequencyBand.BETWEEN_1_AND_5_PERCENT),
            "FrequencyBand.BETWEEN_1_AND_5_PERCENT",
        )
        assertEquals(
            "\"under_1_percent\"",
            Json.encodeToString(FrequencyBand.serializer(), FrequencyBand.UNDER_1_PERCENT),
            "FrequencyBand.UNDER_1_PERCENT",
        )
        assertEquals(
            "\"unknown\"",
            Json.encodeToString(FrequencyBand.serializer(), FrequencyBand.UNKNOWN),
            "FrequencyBand.UNKNOWN",
        )
    }

    @Test
    fun `FrequencyBand decodes from literal english snake_case`() {
        assertEquals(
            FrequencyBand.OVER_5_PERCENT,
            Json.decodeFromString(FrequencyBand.serializer(), "\"over_5_percent\""),
            "FrequencyBand.OVER_5_PERCENT",
        )
        assertEquals(
            FrequencyBand.BETWEEN_1_AND_5_PERCENT,
            Json.decodeFromString(FrequencyBand.serializer(), "\"between_1_and_5_percent\""),
            "FrequencyBand.BETWEEN_1_AND_5_PERCENT",
        )
        assertEquals(
            FrequencyBand.UNDER_1_PERCENT,
            Json.decodeFromString(FrequencyBand.serializer(), "\"under_1_percent\""),
            "FrequencyBand.UNDER_1_PERCENT",
        )
        assertEquals(
            FrequencyBand.UNKNOWN,
            Json.decodeFromString(FrequencyBand.serializer(), "\"unknown\""),
            "FrequencyBand.UNKNOWN",
        )
    }

    @Test
    fun `HepaticSeverity encodes to literal english snake_case`() {
        assertEquals(
            "\"mild\"",
            Json.encodeToString(HepaticSeverity.serializer(), HepaticSeverity.MILD),
            "HepaticSeverity.MILD",
        )
        assertEquals(
            "\"moderate\"",
            Json.encodeToString(HepaticSeverity.serializer(), HepaticSeverity.MODERATE),
            "HepaticSeverity.MODERATE",
        )
        assertEquals(
            "\"severe\"",
            Json.encodeToString(HepaticSeverity.serializer(), HepaticSeverity.SEVERE),
            "HepaticSeverity.SEVERE",
        )
    }

    @Test
    fun `HepaticSeverity decodes from literal english snake_case`() {
        assertEquals(
            HepaticSeverity.MILD,
            Json.decodeFromString(HepaticSeverity.serializer(), "\"mild\""),
            "HepaticSeverity.MILD",
        )
        assertEquals(
            HepaticSeverity.MODERATE,
            Json.decodeFromString(HepaticSeverity.serializer(), "\"moderate\""),
            "HepaticSeverity.MODERATE",
        )
        assertEquals(
            HepaticSeverity.SEVERE,
            Json.decodeFromString(HepaticSeverity.serializer(), "\"severe\""),
            "HepaticSeverity.SEVERE",
        )
    }

    @Test
    fun `RegulatoryClass encodes to literal english snake_case`() {
        assertEquals(
            "\"poison\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.POISON),
            "RegulatoryClass.POISON",
        )
        assertEquals(
            "\"potent\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.POTENT),
            "RegulatoryClass.POTENT",
        )
        assertEquals(
            "\"ordinary\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.ORDINARY),
            "RegulatoryClass.ORDINARY",
        )
        assertEquals(
            "\"psychotropic_1\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.PSYCHOTROPIC_1),
            "RegulatoryClass.PSYCHOTROPIC_1",
        )
        assertEquals(
            "\"psychotropic_2\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.PSYCHOTROPIC_2),
            "RegulatoryClass.PSYCHOTROPIC_2",
        )
        assertEquals(
            "\"psychotropic_3\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.PSYCHOTROPIC_3),
            "RegulatoryClass.PSYCHOTROPIC_3",
        )
        assertEquals(
            "\"narcotic\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.NARCOTIC),
            "RegulatoryClass.NARCOTIC",
        )
        assertEquals(
            "\"stimulant_precursor\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.STIMULANT_PRECURSOR),
            "RegulatoryClass.STIMULANT_PRECURSOR",
        )
        assertEquals(
            "\"biological\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.BIOLOGICAL),
            "RegulatoryClass.BIOLOGICAL",
        )
        assertEquals(
            "\"specified_biological\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.SPECIFIED_BIOLOGICAL),
            "RegulatoryClass.SPECIFIED_BIOLOGICAL",
        )
        assertEquals(
            "\"prescription_required\"",
            Json.encodeToString(RegulatoryClass.serializer(), RegulatoryClass.PRESCRIPTION_REQUIRED),
            "RegulatoryClass.PRESCRIPTION_REQUIRED",
        )
    }

    @Test
    fun `RegulatoryClass decodes from literal english snake_case`() {
        assertEquals(
            RegulatoryClass.POISON,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"poison\""),
            "RegulatoryClass.POISON",
        )
        assertEquals(
            RegulatoryClass.POTENT,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"potent\""),
            "RegulatoryClass.POTENT",
        )
        assertEquals(
            RegulatoryClass.ORDINARY,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"ordinary\""),
            "RegulatoryClass.ORDINARY",
        )
        assertEquals(
            RegulatoryClass.PSYCHOTROPIC_1,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"psychotropic_1\""),
            "RegulatoryClass.PSYCHOTROPIC_1",
        )
        assertEquals(
            RegulatoryClass.PSYCHOTROPIC_2,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"psychotropic_2\""),
            "RegulatoryClass.PSYCHOTROPIC_2",
        )
        assertEquals(
            RegulatoryClass.PSYCHOTROPIC_3,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"psychotropic_3\""),
            "RegulatoryClass.PSYCHOTROPIC_3",
        )
        assertEquals(
            RegulatoryClass.NARCOTIC,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"narcotic\""),
            "RegulatoryClass.NARCOTIC",
        )
        assertEquals(
            RegulatoryClass.STIMULANT_PRECURSOR,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"stimulant_precursor\""),
            "RegulatoryClass.STIMULANT_PRECURSOR",
        )
        assertEquals(
            RegulatoryClass.BIOLOGICAL,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"biological\""),
            "RegulatoryClass.BIOLOGICAL",
        )
        assertEquals(
            RegulatoryClass.SPECIFIED_BIOLOGICAL,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"specified_biological\""),
            "RegulatoryClass.SPECIFIED_BIOLOGICAL",
        )
        assertEquals(
            RegulatoryClass.PRESCRIPTION_REQUIRED,
            Json.decodeFromString(RegulatoryClass.serializer(), "\"prescription_required\""),
            "RegulatoryClass.PRESCRIPTION_REQUIRED",
        )
    }

    @Test
    fun `RouteOfAdministration encodes to literal english snake_case`() {
        assertEquals(
            "\"oral\"",
            Json.encodeToString(RouteOfAdministration.serializer(), RouteOfAdministration.ORAL),
            "RouteOfAdministration.ORAL",
        )
        assertEquals(
            "\"topical\"",
            Json.encodeToString(RouteOfAdministration.serializer(), RouteOfAdministration.TOPICAL),
            "RouteOfAdministration.TOPICAL",
        )
        assertEquals(
            "\"injection_route\"",
            Json.encodeToString(RouteOfAdministration.serializer(), RouteOfAdministration.INJECTION_ROUTE),
            "RouteOfAdministration.INJECTION_ROUTE",
        )
        assertEquals(
            "\"inhalation\"",
            Json.encodeToString(RouteOfAdministration.serializer(), RouteOfAdministration.INHALATION),
            "RouteOfAdministration.INHALATION",
        )
        assertEquals(
            "\"rectal\"",
            Json.encodeToString(RouteOfAdministration.serializer(), RouteOfAdministration.RECTAL),
            "RouteOfAdministration.RECTAL",
        )
        assertEquals(
            "\"ophthalmic\"",
            Json.encodeToString(RouteOfAdministration.serializer(), RouteOfAdministration.OPHTHALMIC),
            "RouteOfAdministration.OPHTHALMIC",
        )
        assertEquals(
            "\"nasal\"",
            Json.encodeToString(RouteOfAdministration.serializer(), RouteOfAdministration.NASAL),
            "RouteOfAdministration.NASAL",
        )
        assertEquals(
            "\"transdermal\"",
            Json.encodeToString(RouteOfAdministration.serializer(), RouteOfAdministration.TRANSDERMAL),
            "RouteOfAdministration.TRANSDERMAL",
        )
    }

    @Test
    fun `RouteOfAdministration decodes from literal english snake_case`() {
        assertEquals(
            RouteOfAdministration.ORAL,
            Json.decodeFromString(RouteOfAdministration.serializer(), "\"oral\""),
            "RouteOfAdministration.ORAL",
        )
        assertEquals(
            RouteOfAdministration.TOPICAL,
            Json.decodeFromString(RouteOfAdministration.serializer(), "\"topical\""),
            "RouteOfAdministration.TOPICAL",
        )
        assertEquals(
            RouteOfAdministration.INJECTION_ROUTE,
            Json.decodeFromString(RouteOfAdministration.serializer(), "\"injection_route\""),
            "RouteOfAdministration.INJECTION_ROUTE",
        )
        assertEquals(
            RouteOfAdministration.INHALATION,
            Json.decodeFromString(RouteOfAdministration.serializer(), "\"inhalation\""),
            "RouteOfAdministration.INHALATION",
        )
        assertEquals(
            RouteOfAdministration.RECTAL,
            Json.decodeFromString(RouteOfAdministration.serializer(), "\"rectal\""),
            "RouteOfAdministration.RECTAL",
        )
        assertEquals(
            RouteOfAdministration.OPHTHALMIC,
            Json.decodeFromString(RouteOfAdministration.serializer(), "\"ophthalmic\""),
            "RouteOfAdministration.OPHTHALMIC",
        )
        assertEquals(
            RouteOfAdministration.NASAL,
            Json.decodeFromString(RouteOfAdministration.serializer(), "\"nasal\""),
            "RouteOfAdministration.NASAL",
        )
        assertEquals(
            RouteOfAdministration.TRANSDERMAL,
            Json.decodeFromString(RouteOfAdministration.serializer(), "\"transdermal\""),
            "RouteOfAdministration.TRANSDERMAL",
        )
    }

    // === disease enums ===
    // worktree-B (feature/serialname-en-disease) のみがこのセクションに関数追加。
    // 追加順は alphabetical: Chronicity → ExamCategory → Icd10Chapter →
    //                       MedicalDepartment → OnsetPattern → PrevalenceUnit。
    // 各 enum で encode 関数 → decode 関数 の順。
}
