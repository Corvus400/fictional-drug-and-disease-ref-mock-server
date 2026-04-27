package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
import kotlin.test.assertEquals

class DosageFormAppearanceTest {
    @Test
    fun `pickAppearance returns the same String for the same form and drugId`() {
        val firstCall: String =
            DosageFormAppearance.pickAppearance(form = DosageForm.TABLET, drugId = "drug_0001")
        val secondCall: String =
            DosageFormAppearance.pickAppearance(form = DosageForm.TABLET, drugId = "drug_0001")
        assertEquals(
            expected = firstCall,
            actual = secondCall,
            message = "pickAppearance must be deterministic for the same (form, drugId)",
        )
    }
}
