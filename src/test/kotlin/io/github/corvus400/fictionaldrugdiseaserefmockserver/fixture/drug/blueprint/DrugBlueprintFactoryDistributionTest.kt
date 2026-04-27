package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
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
}
