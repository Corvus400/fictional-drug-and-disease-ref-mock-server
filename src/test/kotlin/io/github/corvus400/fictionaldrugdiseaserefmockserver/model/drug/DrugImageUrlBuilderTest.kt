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

    @Test
    fun `全 DosageForm の serialName は lowercase snake の SerialName 値と一致する`() {
        val expected = mapOf(
            DosageForm.TABLET to "tablet",
            DosageForm.CAPSULE to "capsule",
            DosageForm.POWDER to "powder",
            DosageForm.GRANULE to "granule",
            DosageForm.LIQUID to "liquid",
            DosageForm.INJECTION_FORM to "injection_form",
            DosageForm.OINTMENT to "ointment",
            DosageForm.CREAM to "cream",
            DosageForm.PATCH to "patch",
            DosageForm.EYE_DROPS to "eye_drops",
            DosageForm.SUPPOSITORY to "suppository",
            DosageForm.INHALER to "inhaler",
            DosageForm.NASAL_SPRAY to "nasal_spray",
        )

        assertEquals(expected, DosageForm.entries.associateWith { it.serialName })
    }
}
