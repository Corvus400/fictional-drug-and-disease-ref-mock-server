package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DrugBlueprintFactoryDistributionTest {
    @Test
    fun `DOSAGE_FORMS_BY_ATC contains key 'A' mapped to a non-empty list of DosageForm`() {
        val forms: List<DosageForm> = DrugBlueprintFactory.DOSAGE_FORMS_BY_ATC.getValue('A')
        assertTrue(
            actual = forms.isNotEmpty(),
            message = "'A' must map to a non-empty list of DosageForm",
        )
    }

    @Test
    fun `DOSAGE_FORMS_BY_ATC values cover all 13 DosageForm enums`() {
        val coveredForms: Set<DosageForm> =
            DrugBlueprintFactory.DOSAGE_FORMS_BY_ATC.values.flatten().toSet()
        assertEquals(
            expected = DosageForm.entries.toSet(),
            actual = coveredForms,
            message = "DOSAGE_FORMS_BY_ATC must cover all 13 DosageForm values, missing: " +
                (DosageForm.entries.toSet() - coveredForms),
        )
    }

    @Test
    fun `deriveDosageForm returns the same DosageForm for the same atcLetter and index`() {
        val firstCall: DosageForm = DrugBlueprintFactory.deriveDosageForm(atcLetter = 'A', index = 0)
        val secondCall: DosageForm = DrugBlueprintFactory.deriveDosageForm(atcLetter = 'A', index = 0)
        assertEquals(
            expected = firstCall,
            actual = secondCall,
            message = "deriveDosageForm must be deterministic for the same (atcLetter, index)",
        )
    }

    @Test
    fun `deriveDosageForm rotates through the ATC list as index advances`() {
        val firstForm: DosageForm = DrugBlueprintFactory.deriveDosageForm(atcLetter = 'R', index = 0)
        val secondForm: DosageForm = DrugBlueprintFactory.deriveDosageForm(atcLetter = 'R', index = 1)
        assertNotEquals(
            illegal = firstForm,
            actual = secondForm,
            message = "deriveDosageForm must rotate through the ATC list (R 群: INHALER → NASAL_SPRAY)",
        )
    }
}
