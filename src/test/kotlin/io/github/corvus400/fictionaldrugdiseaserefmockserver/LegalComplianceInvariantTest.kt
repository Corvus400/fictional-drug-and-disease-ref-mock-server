package io.github.corvus400.fictionaldrugdiseaserefmockserver

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.ForbiddenNames
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import kotlin.test.Test
import kotlin.test.assertTrue

class LegalComplianceInvariantTest {
    @Test
    fun `generated fixtures satisfy public release legal compliance invariants`() {
        val (drugs, diseases) = generateFixtures()

        val violations = buildList {
            drugs.forEach { drug ->
                if (!drug.atcCode.matches(Regex("^[A-V]99ZZ\\d{2}$"))) {
                    add("invalid ATC: ${drug.id}=${drug.atcCode}")
                }
                if (!drug.yjCode.orEmpty().matches(Regex("^999\\d{9}$"))) {
                    add("invalid YJ: ${drug.id}=${drug.yjCode}")
                }
                if (ForbiddenNames.containsClassSuffix(drug.brandName)) {
                    add("brand suffix: ${drug.id}=${drug.brandName}")
                }
                if (ForbiddenNames.containsClassSuffix(drug.genericName)) {
                    add("generic suffix: ${drug.id}=${drug.genericName}")
                }
                drug.fictionalMarkedTexts().forEach { text ->
                    if (!text.contains("(架空)")) {
                        add("drug text lacks marker: ${drug.id}: $text")
                    }
                }
            }

            diseases.forEach { disease ->
                disease.fictionalMarkedTexts().forEach { text ->
                    if (!text.contains("(架空)")) {
                        add("disease text lacks marker: ${disease.id}: $text")
                    }
                }
            }
        }

        assertTrue(
            actual = violations.isEmpty(),
            message = "Public release legal compliance violations:\n" + violations.joinToString("\n"),
        )
    }

    private fun generateFixtures(): Pair<List<Drug>, List<Disease>> {
        val adapter = FixmergeNameAdapter()
        val diseaseDictionary = DiseasePlaceholderDictionary()
        val diseases = DiseaseGenerator(adapter = adapter, placeholderDictionary = diseaseDictionary)
            .generate(blueprints = DiseaseBlueprintFactory.build())
        val drugDictionary = DrugPlaceholderDictionary(nameAdapter = adapter, diseases = diseases)
        val drugs = DrugGenerator(adapter = adapter, placeholderDictionary = drugDictionary)
            .generate(blueprints = DrugBlueprintFactory.build())
        return drugs to diseases
    }

    private fun Drug.fictionalMarkedTexts(): List<String> =
        buildList {
            addAll(warning.map { it.content })
            addAll(contraindications.map { it.content })
            addAll(indications.map { it.content })
            addAll(indicationsRelatedPrecautions.map { it.content })
            add(dosage.standardDosage)
            addAll(dosage.ageSpecificDosage.map { it.dose })
            addAll(dosage.renalAdjustment.map { it.dose })
            addAll(dosage.hepaticAdjustment.map { it.dose })
            addAll(dosageRelatedPrecautions.map { it.content })
            addAll(importantPrecautions.map { it.content })
            addAll(precautionsForSpecificPopulations.map { it.note })
            interactions?.combinationProhibited?.forEach {
                add(it.clinicalSymptom)
                add(it.mechanism)
            }
            interactions?.combinationCaution?.forEach {
                add(it.clinicalSymptom)
                add(it.mechanism)
            }
            adverseReactions.serious.forEach {
                add(it.symptom)
                add(it.initialSigns)
                add(it.countermeasure)
            }
            addAll(effectsOnLabTests.map { it.content })
            overdose?.let {
                add(it.symptoms)
                add(it.management)
            }
            addAll(administrationPrecautions.map { it.content })
            addAll(otherPrecautions.map { it.content })
            pharmacokinetics?.let {
                listOfNotNull(
                    it.bloodConcentration,
                    it.absorption,
                    it.distribution,
                    it.metabolism,
                    it.excretion,
                ).forEach(::add)
            }
            addAll(clinicalResults.map { it.content })
            pharmacology?.let {
                add(it.mechanism)
                add(it.effect)
            }
            physicochemicalProperties?.let { add(it.description) }
            addAll(handlingPrecautions.map { it.content })
            addAll(approvalConditions.map { it.content })
            addAll(insuranceNotes.map { it.content })
        }

    private fun Disease.fictionalMarkedTexts(): List<String> =
        buildList {
            add(summary)
            add(etiology)
            addAll(diagnosticCriteria.required)
            addAll(diagnosticCriteria.supporting)
            diagnosticCriteria.notes?.let(::add)
            prognosis?.let(::add)
            severityGrading?.grades?.forEach { grade ->
                add(grade.criteria)
                add(grade.recommendedAction)
            }
            treatments.pharmacological.forEach {
                add(it.indication)
                add(it.notes)
            }
            treatments.nonPharmacological.forEach {
                it.description?.let(::add)
            }
            treatments.acutePhaseProtocol.forEach {
                add(it.action)
                it.target?.let(::add)
            }
        }
}
