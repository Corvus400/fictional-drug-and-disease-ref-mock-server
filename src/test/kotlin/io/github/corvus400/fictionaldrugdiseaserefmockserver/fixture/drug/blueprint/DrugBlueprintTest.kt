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
                dosageFormGroup = DosageFormGroup.ORAL,
                regulatoryClasses = setOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
                isBiological = false,
                isChronicPrescription = true,
            )
        assertEquals(0, blueprint.index)
        assertEquals('A', blueprint.atcFirstLetter)
        assertEquals(DosageFormGroup.ORAL, blueprint.dosageFormGroup)
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
                dosageFormGroup = DosageFormGroup.ORAL,
                regulatoryClasses = setOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
                isBiological = false,
                isChronicPrescription = false,
            )
        }
    }

    @Test
    fun `DrugBlueprint exposes dosageForm with the value passed to the constructor`() {
        val blueprint =
            DrugBlueprint(
                index = 0,
                atcFirstLetter = 'A',
                dosageFormGroup = DosageFormGroup.ORAL,
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
                dosageFormGroup = DosageFormGroup.ORAL,
                regulatoryClasses = setOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
                isBiological = false,
                isChronicPrescription = false,
            )
        }
    }
}
