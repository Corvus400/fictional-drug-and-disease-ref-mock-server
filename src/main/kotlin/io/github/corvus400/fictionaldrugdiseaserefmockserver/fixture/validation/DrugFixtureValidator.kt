package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration

data class Violation(
    val drugId: String,
    val field: String,
    val message: String,
)

object DrugFixtureValidator {
    fun validate(drugs: List<Drug>): List<Violation> {
        val perDrugViolations = drugs.flatMap { drug ->
            fieldMinimumViolations(drug = drug) + conditionalFieldViolations(drug = drug)
        }
        return perDrugViolations +
            idUniquenessViolations(drugs = drugs) +
            idSequentialViolations(drugs = drugs)
    }

    private fun idUniquenessViolations(drugs: List<Drug>): List<Violation> {
        return drugs.groupingBy { it.id }.eachCount()
            .filter { (_, count) -> count > 1 }
            .map { (id, count) ->
                Violation(
                    drugId = id,
                    field = "id",
                    message = "duplicate id found: appears $count times",
                )
            }
    }

    private fun idSequentialViolations(drugs: List<Drug>): List<Violation> {
        val indices = drugs.mapNotNull { drug -> extractIndex(id = drug.id) }
            .distinct()
            .sorted()
        if (indices.isEmpty()) {
            return emptyList()
        }
        val expected = (indices.first()..indices.last()).toSet()
        val missing = expected - indices.toSet()
        return missing.sorted().map { missingIndex ->
            Violation(
                drugId = formatId(index = missingIndex),
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

    private fun fieldMinimumViolations(drug: Drug): List<Violation> {
        val violations = mutableListOf<Violation>()
        if (drug.contraindications.isEmpty()) {
            violations += Violation(
                drugId = drug.id,
                field = "contraindications",
                message = "contraindications size >= 1 required",
            )
        }
        if (drug.indications.isEmpty()) {
            violations += Violation(
                drugId = drug.id,
                field = "indications",
                message = "indications size >= 1 required",
            )
        }
        if (drug.packages.isEmpty()) {
            violations += Violation(
                drugId = drug.id,
                field = "packages",
                message = "packages size >= 1 required",
            )
        }
        return violations
    }

    private fun conditionalFieldViolations(drug: Drug): List<Violation> {
        val violations = mutableListOf<Violation>()
        if (isInjection(drug = drug)) {
            if (drug.pharmacokinetics == null) {
                violations += Violation(
                    drugId = drug.id,
                    field = "pharmacokinetics",
                    message = "injection requires non-null pharmacokinetics",
                )
            }
            if (drug.administrationPrecautions.isEmpty()) {
                violations += Violation(
                    drugId = drug.id,
                    field = "administrationPrecautions",
                    message = "injection requires administrationPrecautions size >= 1",
                )
            }
        }
        if (isPoisonOrPotent(drug = drug) && drug.warning.isEmpty()) {
            violations += Violation(
                drugId = drug.id,
                field = "warning",
                message = "poison or potent drug requires warning size >= 1",
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

    private const val ID_PAD_LENGTH: Int = 4
    private val ID_PATTERN: Regex = Regex(pattern = """^drug_(\d{4})$""")
}
