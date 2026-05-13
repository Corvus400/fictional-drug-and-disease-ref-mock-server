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
            0,
            blueprint.index,
            "contract assertion failed"
        )
        assertEquals(
            'A',
            blueprint.atcFirstLetter,
            "contract assertion failed"
        )
        assertEquals(
            DosageForm.TABLET,
            blueprint.dosageForm,
            "contract assertion failed"
        )
        assertEquals(
            setOf(RegulatoryClass.PRESCRIPTION_REQUIRED),
            blueprint.regulatoryClasses,
            "contract assertion failed"
        )
        assertEquals(
            false,
            blueprint.isBiological,
            "contract assertion failed"
        )
        assertEquals(
            true,
            blueprint.isChronicPrescription,
            "contract assertion failed"
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
            "drug_0080",
            blueprint.idOverride,
            "contract assertion failed"
        )
        assertEquals(
            "トレデキム",
            blueprint.nameOverride?.brandKatakana,
            "contract assertion failed"
        )
        assertEquals(
            "tredecim",
            blueprint.nameOverride?.genericLatin,
            "contract assertion failed"
        )
        assertEquals(
            "無色澄明の液体である。",
            blueprint.textOverride?.originalSubstanceDescription,
            "contract assertion failed"
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
            null,
            blueprint.idOverride,
            "contract assertion failed"
        )
        assertEquals(
            null,
            blueprint.nameOverride,
            "contract assertion failed"
        )
        assertEquals(
            null,
            blueprint.textOverride,
            "contract assertion failed"
        )
    }
}
