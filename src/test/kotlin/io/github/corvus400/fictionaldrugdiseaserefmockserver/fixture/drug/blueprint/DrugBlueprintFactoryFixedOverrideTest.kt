package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DrugBlueprintFactoryFixedOverrideTest {
    @Test
    fun `build assigns drug 0080 idOverride to the blueprint at index 80`() {
        val blueprints = DrugBlueprintFactory.build()
        val target: DrugBlueprint = assertNotNull(blueprints.firstOrNull { it.index == 80 })
        assertEquals(
            expected = "drug_0080",
            actual = target.idOverride,
            message = "blueprint at index=80 must carry drug_0080 as fixed idOverride",
        )
        assertEquals(
            expected = DosageForm.LIQUID,
            actual = target.dosageForm,
            message = "drug_0080 must be assigned LIQUID dosage form",
        )
        assertTrue(
            actual = RegulatoryClass.POISON in target.regulatoryClasses,
            message = "drug_0080 must keep POISON in regulatoryClasses, " +
                "got ${target.regulatoryClasses}",
        )
    }

    @Test
    fun `build assigns drug 0089 idOverride and fixmerge-coined name to the blueprint at index 89`() {
        val blueprints = DrugBlueprintFactory.build()
        val target: DrugBlueprint = assertNotNull(blueprints.firstOrNull { it.index == 89 })
        assertEquals(
            expected = "drug_0089",
            actual = target.idOverride,
            message = "blueprint at index=89 must carry drug_0089 as fixed idOverride",
        )
        assertEquals(
            expected = DosageForm.LIQUID,
            actual = target.dosageForm,
            message = "drug_0089 must be assigned LIQUID dosage form",
        )
        assertTrue(
            actual = RegulatoryClass.PSYCHOTROPIC_1 in target.regulatoryClasses,
            message = "drug_0089 must keep PSYCHOTROPIC_1 in regulatoryClasses, " +
                "got ${target.regulatoryClasses}",
        )
        val nameOverride: NameOverride = assertNotNull(
            actual = target.nameOverride,
            message = "drug_0089 must carry a fixmerge-coined nameOverride",
        )
        assertTrue(
            actual = nameOverride.brandKatakana.isNotBlank(),
            message = "drug_0089 brandKatakana must be non-blank",
        )
        assertNotEquals(
            illegal = "アリサの睡眠薬",
            actual = nameOverride.brandKatakana,
            message = "drug_0089 brandKatakana must be the fixmerge-coined form, " +
                "not the raw seed token",
        )
    }
}
