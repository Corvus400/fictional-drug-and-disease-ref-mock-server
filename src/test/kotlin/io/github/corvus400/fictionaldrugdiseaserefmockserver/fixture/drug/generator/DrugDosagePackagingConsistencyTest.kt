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

    @Test
    fun `dosage paragraphs do not duplicate frequency suffix`() {
        val firstViolation =
            generateDrugs()
                .firstNotNullOfOrNull { drug ->
                    frequencySuffixDuplicationViolation(drug = drug)
                }

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `max daily dose unit matches standardDosage body unit per drug`() {
        val firstViolation =
            generateDrugs()
                .firstNotNullOfOrNull { drug ->
                    maxDailyDoseUnitViolation(drug = drug)
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

    private fun frequencySuffixDuplicationViolation(drug: Drug): FrequencySuffixDuplicationViolation? =
        dosageParagraphs(drug = drug)
            .firstOrNull { paragraph -> duplicatedFrequencySuffix.containsMatchIn(paragraph.text) }
            ?.let { paragraph ->
                FrequencySuffixDuplicationViolation(
                    drugId = drug.id,
                    form = drug.dosageForm,
                    sourceField = paragraph.sourceField,
                    actualText = paragraph.text,
                )
            }

    private fun maxDailyDoseUnitViolation(drug: Drug): MaxDailyDoseUnitViolation? {
        val standardUnit = dosageTextUnit.find(drug.dosage.standardDosage)?.groupValues?.get(1) ?: return null
        val upperBoundParagraph =
            drug.dosageRelatedPrecautions
                .firstOrNull { paragraph -> maxDailyDoseUnit.containsMatchIn(paragraph.content) }
                ?: return null
        val upperBoundUnit = maxDailyDoseUnit.find(upperBoundParagraph.content)?.groupValues?.get(1) ?: return null
        return upperBoundUnit.takeUnless { unit -> unit == standardUnit }?.let {
            MaxDailyDoseUnitViolation(
                drugId = drug.id,
                form = drug.dosageForm,
                standardUnit = standardUnit,
                upperBoundUnit = upperBoundUnit,
                sampleText = upperBoundParagraph.content,
            )
        }
    }

    private fun dosageParagraphs(drug: Drug): List<DosageParagraph> =
        listOf(DosageParagraph(sourceField = "standardDosage", text = drug.dosage.standardDosage)) +
            drug.dosage.ageSpecificDosage.map { dosage ->
                DosageParagraph(sourceField = "ageSpecificDosage", text = dosage.dose)
            } +
            drug.dosage.renalAdjustment.map { dosage ->
                DosageParagraph(sourceField = "renalAdjustment", text = dosage.dose)
            } +
            drug.dosage.hepaticAdjustment.map { dosage ->
                DosageParagraph(sourceField = "hepaticAdjustment", text = dosage.dose)
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

    private data class MaxDailyDoseUnitViolation(
        val drugId: String,
        val form: DosageForm,
        val standardUnit: String,
        val upperBoundUnit: String,
        val sampleText: String,
    )

    private data class FrequencySuffixDuplicationViolation(
        val drugId: String,
        val form: DosageForm,
        val sourceField: String,
        val actualText: String,
    )

    private data class DosageParagraph(
        val sourceField: String,
        val text: String,
    )

    private companion object {
        val duplicatedFrequencySuffix: Regex = Regex("""\d+\s*回\s+回""")
        val dosageTextUnit: Regex = Regex("""\d+(?:\.\d+)?\s*(錠|カプセル|包|g|mL|枚|滴|噴霧|個|mg|本|袋)""")
        val maxDailyDoseUnit: Regex = Regex("""1 日 \d+(?:\.\d+)?\s*(錠|カプセル|包|g|mL|枚|滴|噴霧|個|mg|本|袋) を超えないこと""")

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
