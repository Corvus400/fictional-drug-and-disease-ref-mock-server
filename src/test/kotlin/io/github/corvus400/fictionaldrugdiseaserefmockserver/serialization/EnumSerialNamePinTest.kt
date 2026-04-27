package io.github.corvus400.fictionaldrugdiseaserefmockserver.serialization

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
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
    }

    // === disease enums ===
    // worktree-B (feature/serialname-en-disease) のみがこのセクションに関数追加。
    // 追加順は alphabetical: Chronicity → ExamCategory → Icd10Chapter →
    //                       MedicalDepartment → OnsetPattern → PrevalenceUnit。
    // 各 enum で encode 関数 → decode 関数 の順。
}
