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

    @Test
    fun `drug_0089 は dosageForm に関わらず drug override 画像 URL を返す`() {
        assertEquals(
            "/images/drug/drug_0089?size=Original",
            buildDrugImageUrl(drugId = "drug_0089", dosageForm = DosageForm.TABLET),
        )
    }

    @Test
    fun `drug_0080 は drug override 画像 URL を返す`() {
        assertEquals(
            "/images/drug/drug_0080?size=Original",
            buildDrugImageUrl(drugId = "drug_0080", dosageForm = DosageForm.LIQUID),
        )
    }
}
