package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DrugBlueprintFactoryFixedOverrideTest {
    @Test
    fun `build assigns LIQUID_SP_TREDECIM idOverride to the blueprint at index 80`() {
        val blueprints = DrugBlueprintFactory.build()
        val target: DrugBlueprint = assertNotNull(blueprints.firstOrNull { it.index == 80 })
        assertEquals(
            expected = "LIQUID_SP_TREDECIM",
            actual = target.idOverride,
            message = "blueprint at index=80 must carry LIQUID_SP_TREDECIM as fixed idOverride",
        )
        assertEquals(
            expected = DosageForm.LIQUID,
            actual = target.dosageForm,
            message = "LIQUID_SP_TREDECIM must be assigned LIQUID dosage form",
        )
        assertTrue(
            actual = RegulatoryClass.POISON in target.regulatoryClasses,
            message = "LIQUID_SP_TREDECIM must keep POISON in regulatoryClasses, " +
                "got ${target.regulatoryClasses}",
        )
    }
}
