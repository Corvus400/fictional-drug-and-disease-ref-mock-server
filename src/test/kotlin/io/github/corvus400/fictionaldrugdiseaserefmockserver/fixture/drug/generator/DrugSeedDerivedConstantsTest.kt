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
import kotlin.test.assertTrue

class DrugSeedDerivedConstantsTest {
    @Test
    fun `composition dose amount varies across generated drugs and stays in dosage-form ranges`() {
        val drugs = generateDrugs()
        val amounts = drugs.map { drug -> drug.composition.activeIngredientAmount.amount }

        assertTrue(
            actual = amounts.distinct().size >= MIN_DOSE_DISTINCT_COUNT,
            message = "standard dose amount must not be a single fixed value: ${amounts.distinct()}",
        )
        assertEquals(
            expected = null,
            actual = drugs.firstOrNull { drug ->
                drug.composition.activeIngredientAmount.amount !in allowedDoseRange(form = drug.dosageForm)
            },
            message = "dose amount must stay in the dosage-form range",
        )
    }

    @Test
    fun `molecular formulas are derived per drug instead of a single fixture constant`() {
        val formulas = generateDrugs().mapNotNull { drug -> drug.physicochemicalProperties?.molecularFormula }

        assertTrue(
            actual = formulas.distinct().size >= MIN_FORMULA_DISTINCT_COUNT,
            message = "molecular formulas must vary across generated drugs: ${formulas.distinct()}",
        )
        assertEquals(
            expected = null,
            actual = formulas.firstOrNull { formula -> !MOLECULAR_FORMULA_PATTERN.matches(formula) },
            message = "molecular formula must look like a formula string",
        )
    }

    @Test
    fun `pharmacokinetic parameter values are id-seed derived and deterministic`() {
        val first = generateDrugs()
        val second = generateDrugs()
        val cmaxValues = first.mapNotNull { drug -> drug.cmaxValue() }

        assertTrue(
            actual = cmaxValues.distinct().size >= MIN_CMAX_DISTINCT_COUNT,
            message = "Cmax must not be fixed: ${cmaxValues.distinct()}",
        )
        assertEquals(
            expected = first.map { drug -> drug.pharmacokinetics?.parameters },
            actual = second.map { drug -> drug.pharmacokinetics?.parameters },
            message = "PK parameters must be deterministic for the same generated drug set",
        )
    }

    @Test
    fun `storage light and moisture protection flags vary across packages`() {
        val storagePairs =
            generateDrugs().flatMap { drug ->
                drug.packages.map { packageInfo ->
                    packageInfo.storageCondition.lightProtection to packageInfo.storageCondition.moistureProtection
                }
            }

        assertTrue(
            actual = storagePairs.distinct().size >= MIN_STORAGE_PAIR_DISTINCT_COUNT,
            message = "storage protection flags must not be all false: ${storagePairs.distinct()}",
        )
    }

    @Test
    fun `pediatric age ranges vary and stay ordered`() {
        val ranges = generateDrugs().flatMap { drug -> drug.dosage.ageSpecificDosage.map { dosage -> dosage.range } }

        assertTrue(
            actual = ranges.map { range -> range.minAgeMonths to range.maxAgeMonths }.distinct().size >=
                MIN_PEDIATRIC_RANGE_DISTINCT_COUNT,
            message = "pediatric age ranges must not all be 6-12 years: $ranges",
        )
        assertEquals(
            expected = null,
            actual = ranges.firstOrNull { range ->
                range.maxAgeMonths != null && range.minAgeMonths != null && range.minAgeMonths >= range.maxAgeMonths
            },
            message = "pediatric age range boundaries must be ordered",
        )
    }

    @Test
    fun `clinical result headings use an expanded deterministic heading pool`() {
        val headings = generateDrugs().flatMap { drug -> drug.clinicalResults.map { section -> section.heading } }

        assertTrue(
            actual = headings.distinct().size >= MIN_CLINICAL_HEADING_DISTINCT_COUNT,
            message = "clinical result headings must use more than the old three headings: ${headings.distinct()}",
        )
    }

    private fun generateDrugs(): List<Drug> {
        val diseases =
            DiseaseGenerator(
                adapter = FixmergeNameAdapter(),
                placeholderDictionary = DiseasePlaceholderDictionary(),
            ).generate(blueprints = DiseaseBlueprintFactory.build())
        return DrugGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DrugPlaceholderDictionary(nameAdapter = FixmergeNameAdapter(), diseases = diseases),
            diseases = diseases,
        ).generate(blueprints = DrugBlueprintFactory.build())
    }

    private fun Drug.cmaxValue(): String? =
        pharmacokinetics?.parameters?.firstOrNull { parameter -> parameter.name == "Cmax" }?.value

    private fun allowedDoseRange(form: DosageForm): ClosedFloatingPointRange<Double> =
        when (form) {
            DosageForm.TABLET,
            DosageForm.CAPSULE,
            DosageForm.SUPPOSITORY,
            -> 25.0..500.0
            DosageForm.POWDER,
            DosageForm.GRANULE,
            DosageForm.OINTMENT,
            DosageForm.CREAM,
            -> 0.5..10.0
            DosageForm.LIQUID,
            DosageForm.EYE_DROPS,
            DosageForm.NASAL_SPRAY,
            -> 1.0..20.0
            DosageForm.INJECTION_FORM -> 0.5..100.0
            DosageForm.PATCH,
            DosageForm.INHALER,
            -> 25.0..250.0
        }

    private companion object {
        const val MIN_DOSE_DISTINCT_COUNT: Int = 5
        const val MIN_FORMULA_DISTINCT_COUNT: Int = 10
        const val MIN_CMAX_DISTINCT_COUNT: Int = 5
        const val MIN_STORAGE_PAIR_DISTINCT_COUNT: Int = 2
        const val MIN_PEDIATRIC_RANGE_DISTINCT_COUNT: Int = 3
        const val MIN_CLINICAL_HEADING_DISTINCT_COUNT: Int = 4
        val MOLECULAR_FORMULA_PATTERN: Regex = Regex("""^C\d+H\d+(N\d+)?O\d+(S\d+)?(Cl\d+)?$""")
    }
}
