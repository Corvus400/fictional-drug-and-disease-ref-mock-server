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
        assertEquals(0, blueprint.index)
        assertEquals('A', blueprint.atcFirstLetter)
        assertEquals(DosageForm.TABLET, blueprint.dosageForm)
        assertEquals(setOf(RegulatoryClass.PRESCRIPTION_REQUIRED), blueprint.regulatoryClasses)
        assertEquals(false, blueprint.isBiological)
        assertEquals(true, blueprint.isChronicPrescription)
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
                idOverride = "LIQUID_SP_TREDECIM",
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
        assertEquals("LIQUID_SP_TREDECIM", blueprint.idOverride)
        assertEquals("トレデキム", blueprint.nameOverride?.brandKatakana)
        assertEquals("tredecim", blueprint.nameOverride?.genericLatin)
        assertEquals("無色澄明の液体である。", blueprint.textOverride?.originalSubstanceDescription)
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
        assertEquals(null, blueprint.idOverride)
        assertEquals(null, blueprint.nameOverride)
        assertEquals(null, blueprint.textOverride)
    }
}
