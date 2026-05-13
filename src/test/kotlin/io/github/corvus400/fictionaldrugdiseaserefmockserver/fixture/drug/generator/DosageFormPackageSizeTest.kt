package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
import kotlin.test.assertFalse

class DosageFormPackageSizeTest {
    @Test
    fun `pickSize for INHALER returns size that does not contain the 錠 unit`() {
        val result: String =
            DosageFormPackageSize.pickSize(form = DosageForm.INHALER, drugId = "drug_0001")
        assertFalse(
            actual = result.contains("錠"),
            message = "INHALER size must not contain '錠', got '$result'",
        )
    }
}
