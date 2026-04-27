package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `pickAppearance for TABLET returns one of the TABLET variants`() {
        val tabletVariants: Set<String> =
            setOf(
                "白色と淡青色の二層フィルムコート錠",
                "白色のフィルムコート錠 (PTP 包装)",
                "淡青色の素錠 (割線あり)",
            )
        val result: String =
            DosageFormAppearance.pickAppearance(form = DosageForm.TABLET, drugId = "drug_0001")
        assertTrue(
            actual = result in tabletVariants,
            message = "pickAppearance(TABLET, _) must be one of TABLET variants, got '$result'",
        )
    }
}
