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
    fun `PrecautionPopulationCategory encodes to literal english snake_case`() {
        assertEquals(
            "\"comorbidity\"",
            Json.encodeToString(
                PrecautionPopulationCategory.serializer(),
                PrecautionPopulationCategory.COMORBIDITY,
            ),
            "PrecautionPopulationCategory.COMORBIDITY",
        )
        assertEquals(
            "\"renal_impairment\"",
            Json.encodeToString(
                PrecautionPopulationCategory.serializer(),
                PrecautionPopulationCategory.RENAL_IMPAIRMENT,
            ),
            "PrecautionPopulationCategory.RENAL_IMPAIRMENT",
        )
        assertEquals(
            "\"hepatic_impairment\"",
            Json.encodeToString(
                PrecautionPopulationCategory.serializer(),
                PrecautionPopulationCategory.HEPATIC_IMPAIRMENT,
            ),
            "PrecautionPopulationCategory.HEPATIC_IMPAIRMENT",
        )
        assertEquals(
            "\"reproductive_potential\"",
            Json.encodeToString(
                PrecautionPopulationCategory.serializer(),
                PrecautionPopulationCategory.REPRODUCTIVE_POTENTIAL,
            ),
            "PrecautionPopulationCategory.REPRODUCTIVE_POTENTIAL",
        )
        assertEquals(
            "\"pregnant\"",
            Json.encodeToString(
                PrecautionPopulationCategory.serializer(),
                PrecautionPopulationCategory.PREGNANT,
            ),
            "PrecautionPopulationCategory.PREGNANT",
        )
        assertEquals(
            "\"lactating\"",
            Json.encodeToString(
                PrecautionPopulationCategory.serializer(),
                PrecautionPopulationCategory.LACTATING,
            ),
            "PrecautionPopulationCategory.LACTATING",
        )
        assertEquals(
            "\"pediatric\"",
            Json.encodeToString(
                PrecautionPopulationCategory.serializer(),
                PrecautionPopulationCategory.PEDIATRIC,
            ),
            "PrecautionPopulationCategory.PEDIATRIC",
        )
        assertEquals(
            "\"geriatric\"",
            Json.encodeToString(
                PrecautionPopulationCategory.serializer(),
                PrecautionPopulationCategory.GERIATRIC,
            ),
            "PrecautionPopulationCategory.GERIATRIC",
        )
    }

    @Test
    fun `PrecautionPopulationCategory decodes from literal english snake_case`() {
        assertEquals(
            PrecautionPopulationCategory.COMORBIDITY,
            Json.decodeFromString(PrecautionPopulationCategory.serializer(), "\"comorbidity\""),
            "PrecautionPopulationCategory.COMORBIDITY",
        )
        assertEquals(
            PrecautionPopulationCategory.RENAL_IMPAIRMENT,
            Json.decodeFromString(PrecautionPopulationCategory.serializer(), "\"renal_impairment\""),
            "PrecautionPopulationCategory.RENAL_IMPAIRMENT",
        )
        assertEquals(
            PrecautionPopulationCategory.HEPATIC_IMPAIRMENT,
            Json.decodeFromString(PrecautionPopulationCategory.serializer(), "\"hepatic_impairment\""),
            "PrecautionPopulationCategory.HEPATIC_IMPAIRMENT",
        )
        assertEquals(
            PrecautionPopulationCategory.REPRODUCTIVE_POTENTIAL,
            Json.decodeFromString(PrecautionPopulationCategory.serializer(), "\"reproductive_potential\""),
            "PrecautionPopulationCategory.REPRODUCTIVE_POTENTIAL",
        )
        assertEquals(
            PrecautionPopulationCategory.PREGNANT,
            Json.decodeFromString(PrecautionPopulationCategory.serializer(), "\"pregnant\""),
            "PrecautionPopulationCategory.PREGNANT",
        )
        assertEquals(
            PrecautionPopulationCategory.LACTATING,
            Json.decodeFromString(PrecautionPopulationCategory.serializer(), "\"lactating\""),
            "PrecautionPopulationCategory.LACTATING",
        )
        assertEquals(
            PrecautionPopulationCategory.PEDIATRIC,
            Json.decodeFromString(PrecautionPopulationCategory.serializer(), "\"pediatric\""),
            "PrecautionPopulationCategory.PEDIATRIC",
        )
        assertEquals(
            PrecautionPopulationCategory.GERIATRIC,
            Json.decodeFromString(PrecautionPopulationCategory.serializer(), "\"geriatric\""),
            "PrecautionPopulationCategory.GERIATRIC",
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
    fun `RenalSeverity encodes to literal english snake_case`() {
        assertEquals(
            "\"normal\"",
            Json.encodeToString(RenalSeverity.serializer(), RenalSeverity.NORMAL),
            "RenalSeverity.NORMAL",
        )
        assertEquals(
            "\"mild\"",
            Json.encodeToString(RenalSeverity.serializer(), RenalSeverity.MILD),
            "RenalSeverity.MILD",
        )
        assertEquals(
            "\"moderate\"",
            Json.encodeToString(RenalSeverity.serializer(), RenalSeverity.MODERATE),
            "RenalSeverity.MODERATE",
        )
        assertEquals(
            "\"severe\"",
            Json.encodeToString(RenalSeverity.serializer(), RenalSeverity.SEVERE),
            "RenalSeverity.SEVERE",
        )
        assertEquals(
            "\"end_stage\"",
            Json.encodeToString(RenalSeverity.serializer(), RenalSeverity.END_STAGE),
            "RenalSeverity.END_STAGE",
        )
    }

    @Test
    fun `RenalSeverity decodes from literal english snake_case`() {
        assertEquals(
            RenalSeverity.NORMAL,
            Json.decodeFromString(RenalSeverity.serializer(), "\"normal\""),
            "RenalSeverity.NORMAL",
        )
        assertEquals(
            RenalSeverity.MILD,
            Json.decodeFromString(RenalSeverity.serializer(), "\"mild\""),
            "RenalSeverity.MILD",
        )
        assertEquals(
            RenalSeverity.MODERATE,
            Json.decodeFromString(RenalSeverity.serializer(), "\"moderate\""),
            "RenalSeverity.MODERATE",
        )
        assertEquals(
            RenalSeverity.SEVERE,
            Json.decodeFromString(RenalSeverity.serializer(), "\"severe\""),
            "RenalSeverity.SEVERE",
        )
        assertEquals(
            RenalSeverity.END_STAGE,
            Json.decodeFromString(RenalSeverity.serializer(), "\"end_stage\""),
            "RenalSeverity.END_STAGE",
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

    @Test
    fun `StorageTemperature encodes to literal english snake_case`() {
        assertEquals(
            "\"room_temperature\"",
            Json.encodeToString(StorageTemperature.serializer(), StorageTemperature.ROOM_TEMPERATURE),
            "StorageTemperature.ROOM_TEMPERATURE",
        )
        assertEquals(
            "\"cold\"",
            Json.encodeToString(StorageTemperature.serializer(), StorageTemperature.COLD),
            "StorageTemperature.COLD",
        )
        assertEquals(
            "\"frozen\"",
            Json.encodeToString(StorageTemperature.serializer(), StorageTemperature.FROZEN),
            "StorageTemperature.FROZEN",
        )
    }

    @Test
    fun `StorageTemperature decodes from literal english snake_case`() {
        assertEquals(
            StorageTemperature.ROOM_TEMPERATURE,
            Json.decodeFromString(StorageTemperature.serializer(), "\"room_temperature\""),
            "StorageTemperature.ROOM_TEMPERATURE",
        )
        assertEquals(
            StorageTemperature.COLD,
            Json.decodeFromString(StorageTemperature.serializer(), "\"cold\""),
            "StorageTemperature.COLD",
        )
        assertEquals(
            StorageTemperature.FROZEN,
            Json.decodeFromString(StorageTemperature.serializer(), "\"frozen\""),
            "StorageTemperature.FROZEN",
        )
    }

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
        assertEquals("\"intermittent\"", AppJson.encodeToString(OnsetPattern.INTERMITTENT))
        assertEquals("\"relapsing\"", AppJson.encodeToString(OnsetPattern.RELAPSING))
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
        assertEquals(
            OnsetPattern.INTERMITTENT,
            AppJson.decodeFromString<OnsetPattern>("\"intermittent\""),
        )
        assertEquals(
            OnsetPattern.RELAPSING,
            AppJson.decodeFromString<OnsetPattern>("\"relapsing\""),
        )
    }

    @Test
    fun `PrevalenceUnit encodes to literal english snake_case`() {
        assertEquals("\"per_population\"", AppJson.encodeToString(PrevalenceUnit.PER_POPULATION))
        assertEquals("\"per_patient\"", AppJson.encodeToString(PrevalenceUnit.PER_PATIENT))
        assertEquals("\"per_birth\"", AppJson.encodeToString(PrevalenceUnit.PER_BIRTH))
    }

    @Test
    fun `PrevalenceUnit decodes from literal english snake_case`() {
        assertEquals(
            PrevalenceUnit.PER_POPULATION,
            AppJson.decodeFromString<PrevalenceUnit>("\"per_population\""),
        )
        assertEquals(
            PrevalenceUnit.PER_PATIENT,
            AppJson.decodeFromString<PrevalenceUnit>("\"per_patient\""),
        )
        assertEquals(
            PrevalenceUnit.PER_BIRTH,
            AppJson.decodeFromString<PrevalenceUnit>("\"per_birth\""),
        )
    }
}
