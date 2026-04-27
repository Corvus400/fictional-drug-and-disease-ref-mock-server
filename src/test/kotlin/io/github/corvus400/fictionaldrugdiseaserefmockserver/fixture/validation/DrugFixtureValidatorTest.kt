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
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = corrupted.id,
                field = "contraindications",
                message = "contraindications size >= 1 required",
            ),
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
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = injection.id,
                field = "pharmacokinetics",
                message = "injection requires non-null pharmacokinetics",
            ),
        )
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = injection.id,
                field = "administrationPrecautions",
                message = "injection requires administrationPrecautions size >= 1",
            ),
        )
    }

    @Test
    fun `validate reports a sequential violation when a drug id is missing from the sequence`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val missingId = "drug_0002"
        val gapList = drugs.filter { drug -> drug.id != missingId }

        val violations = DrugFixtureValidator.validate(drugs = gapList)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = missingId,
                field = "id",
                message = "id sequential violation: 2 missing from sequence",
            ),
        )
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

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = duplicateId,
                field = "id",
                message = "duplicate id found: appears 2 times",
            ),
        )
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
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = poisonOrPotent.id,
                field = "warning",
                message = "poison or potent drug requires warning size >= 1",
            ),
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
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = corrupted.id,
                field = "packages",
                message = "packages size >= 1 required",
            ),
        )
    }

    @Test
    fun `validate reports an external violation for missing administrationPrecautions`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val external = drugs.first { drug -> drug.dosageForm == DosageForm.OINTMENT }
        val corrupted = external.copy(administrationPrecautions = emptyList())
        val withCorrupted = drugs.map { drug -> if (drug.id == external.id) corrupted else drug }

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = external.id,
                field = "administrationPrecautions",
                message = "external topical requires administrationPrecautions size >= 1",
            ),
        )
    }

    @Test
    fun `validate reports an external violation when SUPPOSITORY has empty administrationPrecautions`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val suppository = drugs.first { drug -> drug.dosageForm == DosageForm.SUPPOSITORY }
        val corrupted = suppository.copy(administrationPrecautions = emptyList())
        val withCorrupted = drugs.map { drug -> if (drug.id == suppository.id) corrupted else drug }

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = suppository.id,
                field = "administrationPrecautions",
                message = "external topical requires administrationPrecautions size >= 1",
            ),
        )
    }

    @Test
    fun `validate reports a biological violation when handlingPrecautions is empty`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val base = drugs.first()
        val corrupted = base.copy(
            regulatoryClass = listOf(RegulatoryClass.BIOLOGICAL),
            handlingPrecautions = emptyList(),
        )
        val withCorrupted = drugs.map { drug -> if (drug.id == base.id) corrupted else drug }

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = base.id,
                field = "handlingPrecautions",
                message = "biological product requires handlingPrecautions size >= 1",
            ),
        )
    }

    @Test
    fun `validate reports a biological violation when warning is empty`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val base = drugs.first()
        val corrupted = base.copy(
            regulatoryClass = listOf(RegulatoryClass.SPECIFIED_BIOLOGICAL),
            warning = emptyList(),
        )
        val withCorrupted = drugs.map { drug -> if (drug.id == base.id) corrupted else drug }

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = base.id,
                field = "warning",
                message = "biological product requires warning size >= 1",
            ),
        )
    }

    @Test
    fun `validate reports a narcotic or psychotropic violation when insuranceNotes is empty`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val controlled = drugs.first { drug ->
            RegulatoryClass.NARCOTIC in drug.regulatoryClass ||
                RegulatoryClass.PSYCHOTROPIC_1 in drug.regulatoryClass ||
                RegulatoryClass.PSYCHOTROPIC_2 in drug.regulatoryClass ||
                RegulatoryClass.PSYCHOTROPIC_3 in drug.regulatoryClass
        }
        val corrupted = controlled.copy(insuranceNotes = emptyList())
        val withCorrupted = drugs.map { drug -> if (drug.id == controlled.id) corrupted else drug }

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = controlled.id,
                field = "insuranceNotes",
                message = "narcotic or psychotropic drug requires insuranceNotes size >= 1",
            ),
        )
    }

    @Test
    fun `validate reports a chronic violation when dosageRelatedPrecautions is empty`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())
        val chronic = drugs.first { drug ->
            drug.atcCode.firstOrNull() == 'A' || drug.atcCode.firstOrNull() == 'C'
        }
        val corrupted = chronic.copy(dosageRelatedPrecautions = emptyList())
        val withCorrupted = drugs.map { drug -> if (drug.id == chronic.id) corrupted else drug }

        val violations = DrugFixtureValidator.validate(drugs = withCorrupted)

        assertEquals(
            expected = 1,
            actual = violations.size,
            message = "expected exactly 1 violation but got $violations",
        )
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = chronic.id,
                field = "dosageRelatedPrecautions",
                message = "chronic long-term prescription drug requires dosageRelatedPrecautions size >= 1",
            ),
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
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DRUG,
                entityId = corrupted.id,
                field = "indications",
                message = "indications size >= 1 required",
            ),
        )
    }

    private companion object {
        const val ENTITY_TYPE_DRUG: String = "drug"

        fun assertContainsFixtureViolation(
            violations: List<*>,
            expected: FixtureViolation,
        ) {
            assertTrue(
                actual = violations.any { violation -> violation == expected },
                message = "expected $expected to be present but got $violations",
            )
        }

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
