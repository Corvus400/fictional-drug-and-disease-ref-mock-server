package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DrugBlueprintTest {
    @Test
    fun `DrugBlueprint holds index and atcFirstLetter and other classification axes`() {
        val blueprint =
            DrugBlueprint(
                index = 0,
                atcFirstLetter = 'A',
                regulatoryClasses = setOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
                isBiological = false,
                isChronicPrescription = true,
                dosageForm = DosageForm.TABLET,
            )
        assertEquals(
            expected = DrugBlueprintSnapshot(
                index = 0,
                atcFirstLetter = 'A',
                dosageForm = DosageForm.TABLET,
                regulatoryClasses = setOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
                isBiological = false,
                isChronicPrescription = true,
            ),
            actual = blueprint.snapshot(),
        )
    }

    @Test
    fun `DrugBlueprint rejects negative index`() {
        assertFailsWith<IllegalArgumentException> {
            DrugBlueprint(
                index = -1,
                atcFirstLetter = 'A',
                regulatoryClasses = setOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
                isBiological = false,
                isChronicPrescription = false,
                dosageForm = DosageForm.TABLET,
            )
        }
    }

    @Test
    fun `DrugBlueprint exposes dosageForm with the value passed to the constructor`() {
        val blueprint =
            DrugBlueprint(
                index = 0,
                atcFirstLetter = 'A',
                regulatoryClasses = setOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
                isBiological = false,
                isChronicPrescription = true,
                dosageForm = DosageForm.CAPSULE,
            )
        assertEquals(DosageForm.CAPSULE, blueprint.dosageForm)
    }

    @Test
    fun `DrugBlueprint rejects atcFirstLetter outside ATC first-level classes`() {
        assertFailsWith<IllegalArgumentException> {
            DrugBlueprint(
                index = 0,
                atcFirstLetter = 'Z',
                regulatoryClasses = setOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
                isBiological = false,
                isChronicPrescription = false,
                dosageForm = DosageForm.TABLET,
            )
        }
    }

    @Test
    fun `DrugBlueprint exposes idOverride and nameOverride and textOverride passed to constructor`() {
        val blueprint =
            DrugBlueprint(
                index = 80,
                atcFirstLetter = 'L',
                regulatoryClasses = setOf(RegulatoryClass.POISON),
                isBiological = false,
                isChronicPrescription = false,
                dosageForm = DosageForm.LIQUID,
                idOverride = "drug_0080",
                nameOverride =
                NameOverride(
                    brandKatakana = "トレデキム",
                    genericKatakana = "トレデキム",
                    genericLatin = "tredecim",
                ),
                textOverride =
                FixedDrugTextOverride(
                    appearance = "無色澄明の液体を充填した透明ガラスバイアル",
                    originalSubstanceDescription = "無色澄明の液体である。",
                ),
            )
        assertEquals(
            expected = DrugBlueprintOverrideSnapshot(
                idOverride = "drug_0080",
                brandKatakana = "トレデキム",
                genericLatin = "tredecim",
                originalSubstanceDescription = "無色澄明の液体である。",
            ),
            actual = DrugBlueprintOverrideSnapshot(
                idOverride = blueprint.idOverride,
                brandKatakana = blueprint.nameOverride?.brandKatakana,
                genericLatin = blueprint.nameOverride?.genericLatin,
                originalSubstanceDescription = blueprint.textOverride?.originalSubstanceDescription,
            ),
        )
    }

    @Test
    fun `DrugBlueprint defaults idOverride and nameOverride and textOverride to null`() {
        val blueprint =
            DrugBlueprint(
                index = 0,
                atcFirstLetter = 'A',
                regulatoryClasses = setOf(RegulatoryClass.ORDINARY),
                isBiological = false,
                isChronicPrescription = false,
                dosageForm = DosageForm.TABLET,
            )
        assertEquals(
            expected = DrugBlueprintDefaultOverrideSnapshot(
                idOverride = null,
                nameOverride = null,
                textOverride = null,
            ),
            actual = DrugBlueprintDefaultOverrideSnapshot(
                idOverride = blueprint.idOverride,
                nameOverride = blueprint.nameOverride,
                textOverride = blueprint.textOverride,
            ),
        )
    }

    private fun DrugBlueprint.snapshot(): DrugBlueprintSnapshot =
        DrugBlueprintSnapshot(
            index = index,
            atcFirstLetter = atcFirstLetter,
            dosageForm = dosageForm,
            regulatoryClasses = regulatoryClasses,
            isBiological = isBiological,
            isChronicPrescription = isChronicPrescription,
        )

    private data class DrugBlueprintSnapshot(
        val index: Int,
        val atcFirstLetter: Char,
        val dosageForm: DosageForm,
        val regulatoryClasses: Set<RegulatoryClass>,
        val isBiological: Boolean,
        val isChronicPrescription: Boolean,
    )

    private data class DrugBlueprintOverrideSnapshot(
        val idOverride: String?,
        val brandKatakana: String?,
        val genericLatin: String?,
        val originalSubstanceDescription: String?,
    )

    private data class DrugBlueprintDefaultOverrideSnapshot(
        val idOverride: String?,
        val nameOverride: NameOverride?,
        val textOverride: FixedDrugTextOverride?,
    )
}
