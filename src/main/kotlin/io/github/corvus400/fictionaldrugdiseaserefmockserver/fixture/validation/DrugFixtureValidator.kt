package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration

object DrugFixtureValidator {
    private const val ENTITY_TYPE: String = "drug"

    fun validate(drugs: List<Drug>): List<FixtureViolation> {
        val perDrugViolations = drugs.flatMap { drug ->
            fieldMinimumViolations(drug = drug) + conditionalFieldViolations(drug = drug)
        }
        return perDrugViolations +
            idUniquenessViolations(drugs = drugs) +
            idSequentialViolations(drugs = drugs)
    }

    private fun idUniquenessViolations(drugs: List<Drug>): List<FixtureViolation> {
        return drugs.groupingBy { it.id }.eachCount()
            .filter { (_, count) -> count > 1 }
            .map { (id, count) ->
                FixtureViolation(
                    entityType = ENTITY_TYPE,
                    entityId = id,
                    field = "id",
                    message = "duplicate id found: appears $count times",
                )
            }
    }

    private fun idSequentialViolations(drugs: List<Drug>): List<FixtureViolation> {
        val sequentialIndices: List<Int> = drugs.mapNotNull { drug -> extractIndex(id = drug.id) }
            .distinct()
            .sorted()
        if (sequentialIndices.isEmpty()) {
            return emptyList()
        }
        val overrideIdCount: Int = drugs.size - sequentialIndices.size
        val rangeStart: Int = sequentialIndices.first()
        val rangeEnd: Int = rangeStart + drugs.size - 1
        val expected: Set<Int> = (rangeStart..rangeEnd).toSet()
        val missing: List<Int> = (expected - sequentialIndices.toSet()).sorted()
        return missing.dropLast(n = overrideIdCount).map { missingIndex ->
            FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = formatId(index = missingIndex),
                field = "id",
                message = "id sequential violation: $missingIndex missing from sequence",
            )
        }
    }

    private fun extractIndex(id: String): Int? {
        val match = ID_PATTERN.matchEntire(input = id) ?: return null
        return match.groupValues[1].toIntOrNull()
    }

    private fun formatId(index: Int): String =
        "drug_${index.toString().padStart(length = ID_PAD_LENGTH, padChar = '0')}"

    private fun fieldMinimumViolations(drug: Drug): List<FixtureViolation> {
        val violations = mutableListOf<FixtureViolation>()
        if (drug.contraindications.isEmpty()) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "contraindications",
                message = "contraindications size >= 1 required",
            )
        }
        if (drug.regulatoryClass.isEmpty()) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "regulatoryClass",
                message = "regulatoryClass size >= 1 required (use ORDINARY for common OTC)",
            )
        }
        if (drug.indications.isEmpty() && !allowsNoTherapeuticIndication(drug = drug)) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "indications",
                message = "indications size >= 1 required",
            )
        }
        if (drug.packages.isEmpty()) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "packages",
                message = "packages size >= 1 required",
            )
        }
        return violations
    }

    private fun conditionalFieldViolations(drug: Drug): List<FixtureViolation> {
        val violations = mutableListOf<FixtureViolation>()
        if (isInjection(drug = drug)) {
            val pk = drug.pharmacokinetics
            if (pk == null) {
                violations += FixtureViolation(
                    entityType = ENTITY_TYPE,
                    entityId = drug.id,
                    field = "pharmacokinetics",
                    message = "injection requires non-null pharmacokinetics",
                )
            }
            if (pk != null && pk.bloodConcentration == null) {
                violations += FixtureViolation(
                    entityType = ENTITY_TYPE,
                    entityId = drug.id,
                    field = "pharmacokinetics.bloodConcentration",
                    message = "injection requires non-null pharmacokinetics.bloodConcentration",
                )
            }
            if (pk != null && pk.metabolism == null && pk.excretion == null) {
                violations += FixtureViolation(
                    entityType = ENTITY_TYPE,
                    entityId = drug.id,
                    field = "pharmacokinetics.metabolism|excretion",
                    message = "injection requires at least one of pharmacokinetics.metabolism or .excretion",
                )
            }
            if (drug.administrationPrecautions.isEmpty()) {
                violations += FixtureViolation(
                    entityType = ENTITY_TYPE,
                    entityId = drug.id,
                    field = "administrationPrecautions",
                    message = "injection requires administrationPrecautions size >= 1",
                )
            }
        }
        if (isPoisonOrPotent(drug = drug) && drug.warning.isEmpty()) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "warning",
                message = "poison or potent drug requires warning size >= 1",
            )
        }
        if (isExternalTopical(drug = drug) && drug.administrationPrecautions.isEmpty()) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "administrationPrecautions",
                message = "external topical requires administrationPrecautions size >= 1",
            )
        }
        if (isBiological(drug = drug) && drug.handlingPrecautions.isEmpty()) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "handlingPrecautions",
                message = "biological product requires handlingPrecautions size >= 1",
            )
        }
        if (isBiological(drug = drug) && drug.warning.isEmpty()) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "warning",
                message = "biological product requires warning size >= 1",
            )
        }
        if (isNarcoticOrPsychotropic(drug = drug) && drug.insuranceNotes.isEmpty()) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "insuranceNotes",
                message = "narcotic or psychotropic drug requires insuranceNotes size >= 1",
            )
        }
        if (isChronicPrescription(drug = drug) && drug.dosageRelatedPrecautions.isEmpty()) {
            violations += FixtureViolation(
                entityType = ENTITY_TYPE,
                entityId = drug.id,
                field = "dosageRelatedPrecautions",
                message = "chronic long-term prescription drug requires dosageRelatedPrecautions size >= 1",
            )
        }
        return violations
    }

    private fun isInjection(drug: Drug): Boolean =
        drug.dosageForm == DosageForm.INJECTION_FORM ||
            drug.routeOfAdministration == RouteOfAdministration.INJECTION_ROUTE

    private fun isPoisonOrPotent(drug: Drug): Boolean =
        RegulatoryClass.POISON in drug.regulatoryClass ||
            RegulatoryClass.POTENT in drug.regulatoryClass

    private fun isExternalTopical(drug: Drug): Boolean =
        drug.dosageForm in EXTERNAL_TOPICAL_FORMS

    private fun isBiological(drug: Drug): Boolean =
        RegulatoryClass.BIOLOGICAL in drug.regulatoryClass ||
            RegulatoryClass.SPECIFIED_BIOLOGICAL in drug.regulatoryClass

    private fun isNarcoticOrPsychotropic(drug: Drug): Boolean =
        drug.regulatoryClass.any { it in NARCOTIC_OR_PSYCHOTROPIC_CLASSES }

    private fun isChronicPrescription(drug: Drug): Boolean =
        drug.atcCode.firstOrNull() in CHRONIC_ATC_LETTERS

    private fun allowsNoTherapeuticIndication(drug: Drug): Boolean =
        drug.id in NO_THERAPEUTIC_INDICATION_DRUG_IDS

    private val NO_THERAPEUTIC_INDICATION_DRUG_IDS: Set<String> = setOf("drug_0080")

    private val CHRONIC_ATC_LETTERS: Set<Char?> = setOf('A', 'C')

    private val NARCOTIC_OR_PSYCHOTROPIC_CLASSES: Set<RegulatoryClass> =
        setOf(
            RegulatoryClass.NARCOTIC,
            RegulatoryClass.PSYCHOTROPIC_1,
            RegulatoryClass.PSYCHOTROPIC_2,
            RegulatoryClass.PSYCHOTROPIC_3,
        )

    private val EXTERNAL_TOPICAL_FORMS: Set<DosageForm> =
        setOf(
            DosageForm.OINTMENT,
            DosageForm.CREAM,
            DosageForm.PATCH,
            DosageForm.EYE_DROPS,
            DosageForm.NASAL_SPRAY,
            DosageForm.SUPPOSITORY,
        )

    private const val ID_PAD_LENGTH: Int = 4
    private val ID_PATTERN: Regex = Regex(pattern = """^drug_(\d{4})$""")
}
