package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugImageUrlBuilderTest {
    @Test
    fun `通常 drug は dosage form 画像 URL を返す`() {
        assertEquals(
            "/images/dosage_form/tablet?size=Original",
            buildDrugImageUrl(drugId = "drug_0001", dosageForm = DosageForm.TABLET),
        )
    }
}
