package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugFixtureValidatorTest {
    @Test
    fun `validate returns no violations for the full 120 drug fixture set`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val violations = DrugFixtureValidator.validate(drugs = drugs)

        assertTrue(
            actual = violations.isEmpty(),
            message = "expected no violations but got $violations",
        )
    }

    @Test
    fun `validate reports a size violation when contraindications is empty`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val corrupted = drugs.first().copy(contraindications = emptyList())
        val withCorrupted = listOf(corrupted) + drugs.drop(n = 1)

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        val violation = violations.single()
        assertEquals(expected = corrupted.id, actual = violation.drugId)
        assertEquals(expected = "contraindications", actual = violation.field)
        assertTrue(
            actual = "size >= 1" in violation.message,
            message = "expected 'size >= 1' in message but was '${violation.message}'",
        )
    }

    @Test
    fun `validate reports injection violations for missing pharmacokinetics and admPrecautions`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val injection = drugs.first { drug -> drug.dosageForm == DosageForm.INJECTION_FORM }
        val corrupted = injection.copy(
            pharmacokinetics = null,
            administrationPrecautions = emptyList(),
        )
        val withCorrupted = drugs.map { drug -> if (drug.id == injection.id) corrupted else drug }

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 2,
            actual = violations.size,
            message = "expected exactly 2 violations but got $violations",
        )
        assertTrue(
            actual = violations.all { it.drugId == injection.id },
            message = "expected all violations on ${injection.id} but got $violations",
        )
        assertTrue(
            actual = violations.any { v -> v.field == "pharmacokinetics" && "injection" in v.message },
            message = "expected pharmacokinetics violation with injection message but got $violations",
        )
        assertTrue(
            actual = violations.any { v ->
                v.field == "administrationPrecautions" && "injection" in v.message
            },
            message = "expected administrationPrecautions violation with injection message but got $violations",
        )
    }

    @Test
    fun `validate reports a sequential violation when a drug id is missing from the sequence`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val missingId = "drug_0002"
        val gapList = drugs.filter { drug -> drug.id != missingId }

        val violations = DrugFixtureValidator.validate(drugs = gapList)

        val sequentialViolations = violations.filter { v ->
            v.field == "id" && "sequential" in v.message
        }
        assertEquals(
            expected = 1,
            actual = sequentialViolations.size,
            message = "expected exactly 1 sequential violation but got $violations",
        )
        assertEquals(expected = missingId, actual = sequentialViolations.single().drugId)
    }

    @Test
    fun `validate reports an id violation when two drugs share the same id`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val duplicateId = "drug_0001"
        val duplicated = drugs[1].copy(id = duplicateId)
        val withDuplicate = drugs.map { drug ->
            if (drug.id == drugs[1].id) duplicated else drug
        } + duplicated

        val violations = DrugFixtureValidator.validate(drugs = withDuplicate)

        val idViolations = violations.filter { it.field == "id" }
        assertTrue(
            actual = idViolations.isNotEmpty(),
            message = "expected at least one id violation but got $violations",
        )
        val duplicateViolation = idViolations.firstOrNull { v -> "duplicate" in v.message }
        assertTrue(
            actual = duplicateViolation != null,
            message = "expected a duplicate id violation but got $violations",
        )
        assertEquals(expected = duplicateId, actual = duplicateViolation?.drugId)
    }

    @Test
    fun `validate reports a warning violation when poison or potent drug has empty warning`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val poisonOrPotent = drugs.first { drug ->
            RegulatoryClass.POISON in drug.regulatoryClass ||
                RegulatoryClass.POTENT in drug.regulatoryClass
        }
        val corrupted = poisonOrPotent.copy(warning = emptyList())
        val withCorrupted = drugs.map { drug ->
            if (drug.id == poisonOrPotent.id) corrupted else drug
        }

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        val violation = violations.single()
        assertEquals(expected = poisonOrPotent.id, actual = violation.drugId)
        assertEquals(expected = "warning", actual = violation.field)
        assertTrue(
            actual = "poison" in violation.message || "potent" in violation.message,
            message = "expected poison/potent hint in message but was '${violation.message}'",
        )
    }

    @Test
    fun `validate reports a size violation when packages is empty`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val corrupted = drugs.first().copy(packages = emptyList())
        val withCorrupted = listOf(corrupted) + drugs.drop(n = 1)

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        val violation = violations.single()
        assertEquals(expected = corrupted.id, actual = violation.drugId)
        assertEquals(expected = "packages", actual = violation.field)
        assertTrue(
            actual = "size >= 1" in violation.message,
            message = "expected 'size >= 1' in message but was '${violation.message}'",
        )
    }

    @Test
    fun `validate reports a size violation when indications is empty`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val corrupted = drugs.first().copy(indications = emptyList())
        val withCorrupted = listOf(corrupted) + drugs.drop(n = 1)

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        val violation = violations.single()
        assertEquals(expected = corrupted.id, actual = violation.drugId)
        assertEquals(expected = "indications", actual = violation.field)
        assertTrue(
            actual = "size >= 1" in violation.message,
            message = "expected 'size >= 1' in message but was '${violation.message}'",
        )
    }

    private companion object {
        fun buildFreshGenerator(): DrugGenerator {
            val adapter = FixmergeNameAdapter()
            return DrugGenerator(
                adapter = adapter,
                placeholderDictionary = buildTestDictionary(adapter = adapter),
            )
        }

        fun buildTestDictionary(adapter: FixmergeNameAdapter): DrugPlaceholderDictionary =
            DrugPlaceholderDictionary(
                nameAdapter = adapter,
                diseaseProvider = DiseaseFixtureProvider(all = testDiseaseFixtures()),
            )

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
