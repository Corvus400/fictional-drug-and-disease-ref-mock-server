package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.HepaticSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RenalSeverity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.StorageTemperature
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugFixtureCoverageTest {
    private val generator: DrugGenerator = buildFreshGenerator()

    @Test
    fun `generate covers all HepaticSeverity values`() {
        val drugs = generator.generate(blueprints = DrugBlueprintFactory.build())

        val severities =
            drugs
                .flatMap { drug -> drug.dosage.hepaticAdjustment }
                .map { hepaticDose -> hepaticDose.severity }
                .toSet()

        assertEquals(
            expected = HepaticSeverity.entries.toSet(),
            actual = severities,
        )
    }

    @Test
    fun `generate covers all RenalSeverity values`() {
        val drugs = generator.generate(blueprints = DrugBlueprintFactory.build())

        val severities =
            drugs
                .flatMap { drug -> drug.dosage.renalAdjustment }
                .map { renalDose -> renalDose.range.severity }
                .toSet()

        assertEquals(
            expected = RenalSeverity.entries.toSet(),
            actual = severities,
        )
    }

    @Test
    fun `generate aligns RenalSeverity boundary ranges with open ended CrCl semantics`() {
        val drugs = generator.generate(blueprints = DrugBlueprintFactory.build())
        val renalDoses = drugs.flatMap { drug -> drug.dosage.renalAdjustment }

        val normalDoses = renalDoses.filter { renalDose -> renalDose.range.severity == RenalSeverity.NORMAL }
        assertTrue(
            normalDoses.isNotEmpty(),
            "NORMAL renal doses must be present"
        )
        assertTrue(
            actual = normalDoses.all { renalDose -> renalDose.range.maxMlPerMin == null },
            message = "NORMAL renal doses must have no upper CrCl bound",
        )

        val endStageDoses = renalDoses.filter { renalDose -> renalDose.range.severity == RenalSeverity.END_STAGE }
        assertTrue(
            endStageDoses.isNotEmpty(),
            "END_STAGE renal doses must be present"
        )
        assertTrue(
            actual = endStageDoses.all { renalDose -> renalDose.range.minMlPerMin == null },
            message = "END_STAGE renal doses must have no lower CrCl bound",
        )
    }

    @Test
    fun `generate covers all RegulatoryClass values`() {
        val drugs = generator.generate(blueprints = DrugBlueprintFactory.build())

        val classes = drugs.flatMap { drug -> drug.regulatoryClass }.toSet()

        assertEquals(
            expected = RegulatoryClass.entries.toSet(),
            actual = classes,
        )
    }

    @Test
    fun `generate assigns stimulant precursor class to drug 0087`() {
        val drugs = generator.generate(blueprints = DrugBlueprintFactory.build())

        val drug87 = drugs.first { drug -> drug.id == "drug_0087" }

        assertTrue(
            RegulatoryClass.STIMULANT_PRECURSOR in drug87.regulatoryClass,
            "contract assertion failed"
        )
        assertTrue(
            RegulatoryClass.PRESCRIPTION_REQUIRED in drug87.regulatoryClass,
            "contract assertion failed"
        )
    }

    @Test
    fun `generate covers all StorageTemperature values`() {
        val drugs = generator.generate(blueprints = DrugBlueprintFactory.build())

        val temperatures =
            drugs
                .flatMap { drug -> drug.packages }
                .map { pkg -> pkg.storageCondition.temperature }
                .toSet()

        assertEquals(
            expected = StorageTemperature.entries.toSet(),
            actual = temperatures,
        )
    }

    @Test
    fun `generate keeps biological injection packages cold or frozen`() {
        val drugs = generator.generate(blueprints = DrugBlueprintFactory.build())

        val biologicalInjections =
            drugs.filter { drug ->
                drug.dosageForm == DosageForm.INJECTION_FORM &&
                    (
                        RegulatoryClass.BIOLOGICAL in drug.regulatoryClass ||
                            RegulatoryClass.SPECIFIED_BIOLOGICAL in drug.regulatoryClass
                        )
            }

        assertTrue(biologicalInjections.isNotEmpty(), "biological injection drugs must be present")
        biologicalInjections.forEach { drug ->
            drug.packages.forEach { pkg ->
                assertTrue(
                    actual =
                    pkg.storageCondition.temperature in
                        setOf(StorageTemperature.COLD, StorageTemperature.FROZEN),
                    message = "biological injection ${drug.id} must be stored cold or frozen",
                )
            }
        }
    }

    @Test
    fun `generate covers all DoseUnit values`() {
        val drugs = generator.generate(blueprints = DrugBlueprintFactory.build())

        val units = drugs.map { drug -> drug.composition.activeIngredientAmount.unit }.toSet()

        assertEquals(
            expected = DoseUnit.entries.toSet(),
            actual = units,
        )
    }

    @Test
    fun `generate covers injection form unique DoseUnit values among injection drugs`() {
        val drugs = generator.generate(blueprints = DrugBlueprintFactory.build())

        val injectionUnits =
            drugs
                .filter { drug -> drug.dosageForm == DosageForm.INJECTION_FORM }
                .map { drug -> drug.composition.activeIngredientAmount.unit }
                .toSet()
        val expectedUnique = setOf(DoseUnit.IU, DoseUnit.MEQ, DoseUnit.MMOL, DoseUnit.MOL, DoseUnit.L)

        assertTrue(
            actual = expectedUnique.all { unit -> unit in injectionUnits },
            message = "missing injection-specific DoseUnit values: ${expectedUnique - injectionUnits}",
        )
    }

    private companion object {
        fun buildFreshGenerator(): DrugGenerator {
            val adapter = FixmergeNameAdapter()
            return DrugGenerator(
                adapter = adapter,
                placeholderDictionary =
                DrugPlaceholderDictionary(
                    nameAdapter = adapter,
                    diseases = testDiseaseFixtures(),
                ),
            )
        }

        fun testDiseaseFixtures(): List<Disease> =
            listOf(
                makeTestDisease(id = "disease_0000", name = "架空疾患甲"),
                makeTestDisease(id = "disease_0001", name = "架空疾患乙"),
            )

        fun makeTestDisease(
            id: String,
            name: String,
        ): Disease =
            Disease(
                id = id,
                name = name,
                nameKana = "カクウシッカン",
                icd10Chapter = Icd10Chapter.CHAPTER_X,
                medicalDepartment = listOf(MedicalDepartment.INTERNAL_MEDICINE),
                chronicity = Chronicity.CHRONIC,
                infectious = false,
                summary = "テスト用の架空疾患です。",
                etiology = "テスト用の病因です。",
                symptoms = SymptomInfo(mainSymptoms = listOf("テスト症状")),
                diagnosticCriteria = DiagnosticCriteriaInfo(required = listOf("テスト診断基準")),
                treatments = TreatmentInfo(),
                revisedAt = "2026-01-01",
            )
    }
}
