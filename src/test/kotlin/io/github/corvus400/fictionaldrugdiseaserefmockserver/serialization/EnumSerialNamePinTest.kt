package io.github.corvus400.fictionaldrugdiseaserefmockserver.serialization

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
    }

    // === disease enums ===
    // worktree-B (feature/serialname-en-disease) のみがこのセクションに関数追加。
    // 追加順は alphabetical: Chronicity → ExamCategory → Icd10Chapter →
    //                       MedicalDepartment → OnsetPattern → PrevalenceUnit。
    // 各 enum で encode 関数 → decode 関数 の順。
}
