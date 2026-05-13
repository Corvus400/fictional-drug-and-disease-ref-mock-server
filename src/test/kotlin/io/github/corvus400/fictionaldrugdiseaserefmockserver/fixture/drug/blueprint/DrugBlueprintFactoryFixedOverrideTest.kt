package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugBlueprintFactoryFixedOverrideTest {
    @Test
    fun `build assigns drug 0080 idOverride to the blueprint at index 80`() {
        val blueprints = DrugBlueprintFactory.build()
        val target: DrugBlueprint? = blueprints.firstOrNull { it.index == 80 }
        assertEquals(
            expected = FixedOverrideSnapshot(
                present = true,
                idOverride = "drug_0080",
                dosageForm = DosageForm.LIQUID,
                hasRequiredRegulatoryClass = true,
            ),
            actual = FixedOverrideSnapshot(
                present = target != null,
                idOverride = target?.idOverride,
                dosageForm = target?.dosageForm,
                hasRequiredRegulatoryClass = target?.regulatoryClasses?.contains(RegulatoryClass.POISON) == true,
            ),
            message = "blueprint at index=80 must carry the drug_0080 fixed override",
        )
    }

    @Test
    fun `build assigns drug 0089 idOverride and fixmerge-coined name to the blueprint at index 89`() {
        val blueprints = DrugBlueprintFactory.build()
        val target: DrugBlueprint? = blueprints.firstOrNull { it.index == 89 }
        val nameOverride: NameOverride? = target?.nameOverride
        assertEquals(
            expected = Drug0089OverrideSnapshot(
                present = true,
                idOverride = "drug_0089",
                dosageForm = DosageForm.LIQUID,
                hasRequiredRegulatoryClass = true,
                nameOverridePresent = true,
                brandKatakanaNonBlank = true,
                brandKatakanaAvoidsRawSeed = true,
            ),
            actual = Drug0089OverrideSnapshot(
                present = target != null,
                idOverride = target?.idOverride,
                dosageForm = target?.dosageForm,
                hasRequiredRegulatoryClass =
                target?.regulatoryClasses?.contains(RegulatoryClass.PSYCHOTROPIC_1) == true,
                nameOverridePresent = nameOverride != null,
                brandKatakanaNonBlank = !nameOverride?.brandKatakana.isNullOrBlank(),
                brandKatakanaAvoidsRawSeed = nameOverride?.brandKatakana != "アリサの睡眠薬",
            ),
            message = "blueprint at index=89 must carry the drug_0089 fixed override and coined name",
        )
    }

    private data class FixedOverrideSnapshot(
        val present: Boolean,
        val idOverride: String?,
        val dosageForm: DosageForm?,
        val hasRequiredRegulatoryClass: Boolean,
    )

    private data class Drug0089OverrideSnapshot(
        val present: Boolean,
        val idOverride: String?,
        val dosageForm: DosageForm?,
        val hasRequiredRegulatoryClass: Boolean,
        val nameOverridePresent: Boolean,
        val brandKatakanaNonBlank: Boolean,
        val brandKatakanaAvoidsRawSeed: Boolean,
    )
}
