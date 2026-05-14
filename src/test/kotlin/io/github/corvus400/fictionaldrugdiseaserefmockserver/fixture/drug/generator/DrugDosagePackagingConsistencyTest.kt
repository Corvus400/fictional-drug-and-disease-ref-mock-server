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

    private fun dosageUnitViolation(
        drug: Drug,
        sourceField: String,
        actualText: String,
    ): DosageUnitViolation? {
        val expectedUnit = expectedDoseTextUnitFor(form = drug.dosageForm)
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

    private fun expectedDoseTextUnitFor(form: DosageForm): String =
        when (form) {
            DosageForm.TABLET -> "錠"
            DosageForm.CAPSULE -> "カプセル"
            DosageForm.POWDER -> "包"
            DosageForm.GRANULE -> "包"
            DosageForm.LIQUID -> "mL"
            DosageForm.INJECTION_FORM -> "mL"
            DosageForm.OINTMENT -> "g"
            DosageForm.CREAM -> "g"
            DosageForm.PATCH -> "枚"
            DosageForm.EYE_DROPS -> "滴"
            DosageForm.SUPPOSITORY -> "個"
            DosageForm.INHALER -> "噴霧"
            DosageForm.NASAL_SPRAY -> "噴霧"
        }

    private data class DosageUnitViolation(
        val drugId: String,
        val form: DosageForm,
        val expectedUnit: String,
        val sourceField: String,
        val actualText: String,
    )
}
