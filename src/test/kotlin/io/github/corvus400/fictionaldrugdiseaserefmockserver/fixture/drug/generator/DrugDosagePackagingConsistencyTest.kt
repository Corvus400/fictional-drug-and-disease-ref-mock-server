package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugDosagePackagingConsistencyTest {
    @Test
    fun `standardDosage uses packaging unit derived from dosage form`() {
        val firstViolation =
            generateDrugs()
                .firstNotNullOfOrNull { drug ->
                    dosageUnitViolation(
                        drug = drug,
                        sourceField = "standardDosage",
                        actualText = drug.dosage.standardDosage,
                    )
                }

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `ageSpecificDosage uses packaging unit derived from dosage form`() {
        val firstViolation =
            generateDrugs()
                .firstNotNullOfOrNull { drug ->
                    drug.dosage.ageSpecificDosage.firstNotNullOfOrNull { dosage ->
                        dosageUnitViolation(
                            drug = drug,
                            sourceField = "ageSpecificDosage",
                            actualText = dosage.dose,
                        )
                    }
                }

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `renalAdjustment uses packaging unit derived from dosage form`() {
        val firstViolation =
            generateDrugs()
                .firstNotNullOfOrNull { drug ->
                    drug.dosage.renalAdjustment.firstNotNullOfOrNull { dosage ->
                        dosageUnitViolation(
                            drug = drug,
                            sourceField = "renalAdjustment",
                            actualText = dosage.dose,
                        )
                    }
                }

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `hepaticAdjustment uses packaging unit derived from dosage form`() {
        val firstViolation =
            generateDrugs()
                .firstNotNullOfOrNull { drug ->
                    drug.dosage.hepaticAdjustment.firstNotNullOfOrNull { dosage ->
                        dosageUnitViolation(
                            drug = drug,
                            sourceField = "hepaticAdjustment",
                            actualText = dosage.dose,
                        )
                    }
                }

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `standardDosage administration verb is consistent with dosage form`() {
        val firstViolation =
            generateDrugs()
                .firstNotNullOfOrNull { drug ->
                    administrationVerbViolation(
                        drug = drug,
                        actualText = drug.dosage.standardDosage,
                    )
                }

        assertEquals(expected = null, actual = firstViolation)
    }

    private fun dosageUnitViolation(
        drug: Drug,
        sourceField: String,
        actualText: String,
    ): DosageUnitViolation? {
        val expectedUnit = DosageFormDoseTextUnit.unitFor(form = drug.dosageForm)
        return actualText.takeUnless { text -> expectedUnit in text }?.let {
            DosageUnitViolation(
                drugId = drug.id,
                form = drug.dosageForm,
                expectedUnit = expectedUnit,
                sourceField = sourceField,
                actualText = actualText,
            )
        }
    }

    private fun administrationVerbViolation(
        drug: Drug,
        actualText: String,
    ): AdministrationVerbViolation? {
        val expectedVerb = expectedAdministrationVerbs.getValue(drug.dosageForm)
        return actualText.takeUnless { text -> expectedVerb in text }?.let {
            AdministrationVerbViolation(
                drugId = drug.id,
                form = drug.dosageForm,
                expectedVerb = expectedVerb,
                actualText = actualText,
            )
        }
    }

    private fun generateDrugs(): List<Drug> {
        val adapter = FixmergeNameAdapter()
        val diseasePlaceholderDictionary = DiseasePlaceholderDictionary()
        val diseases =
            DiseaseGenerator(adapter = adapter, placeholderDictionary = diseasePlaceholderDictionary)
                .generate(blueprints = DiseaseBlueprintFactory.build())
        return DrugGenerator(
            adapter = adapter,
            placeholderDictionary = DrugPlaceholderDictionary(nameAdapter = adapter, diseases = diseases),
            diseases = diseases,
        ).generate(blueprints = DrugBlueprintFactory.build())
    }

    private data class DosageUnitViolation(
        val drugId: String,
        val form: DosageForm,
        val expectedUnit: String,
        val sourceField: String,
        val actualText: String,
    )

    private data class AdministrationVerbViolation(
        val drugId: String,
        val form: DosageForm,
        val expectedVerb: String,
        val actualText: String,
    )

    private companion object {
        val expectedAdministrationVerbs: Map<DosageForm, String> =
            mapOf(
                DosageForm.TABLET to "経口投与",
                DosageForm.CAPSULE to "経口投与",
                DosageForm.POWDER to "経口投与",
                DosageForm.GRANULE to "経口投与",
                DosageForm.LIQUID to "経口投与",
                DosageForm.INJECTION_FORM to "投与",
                DosageForm.OINTMENT to "塗布",
                DosageForm.CREAM to "塗布",
                DosageForm.PATCH to "貼付",
                DosageForm.EYE_DROPS to "点眼",
                DosageForm.SUPPOSITORY to "挿入",
                DosageForm.INHALER to "吸入",
                DosageForm.NASAL_SPRAY to "吸入",
            )
    }
}
