package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.BucketNameCoiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DiseaseCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease

class DiseaseGenerator(
    adapter: FixmergeNameAdapter,
) {
    private val coiner: BucketNameCoiner = BucketNameCoiner(adapter = adapter)

    fun generate(blueprint: DiseaseBlueprint): Disease {
        val country = DiseaseCountryMapping.of(chapter = blueprint.icd10Chapter)
        val cities = CountryBucketRepository.of(country = country).cities
        val name =
            coiner.coin(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_NAME,
                offset = 0,
            )
        val synonyms = (0 until SYNONYM_COUNT).map { offset ->
            coiner.coin(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_ALIAS,
                offset = offset,
            )
        }
        val differentials = (0 until DIFFERENTIAL_COUNT).map { offset ->
            coiner.coin(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_DIFFERENTIAL,
                offset = offset,
            )
        }
        val complications = (0 until COMPLICATION_COUNT).map { offset ->
            coiner.coin(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_COMPLICATION,
                offset = offset,
            )
        }
        return buildDisease(
            blueprint = blueprint,
            name = name,
            synonyms = synonyms,
            differentials = differentials,
            complications = complications,
        )
    }

    fun generate(blueprints: List<DiseaseBlueprint>): List<Disease> {
        return blueprints.map { generate(blueprint = it) }
    }

    private fun buildDisease(
        blueprint: DiseaseBlueprint,
        name: CoinedName,
        synonyms: List<CoinedName>,
        differentials: List<CoinedName>,
        complications: List<CoinedName>,
    ): Disease {
        val diseaseId =
            "disease_${blueprint.index.toString().padStart(length = DISEASE_ID_PAD_LENGTH, padChar = '0')}"
        return Disease(
            id = diseaseId,
            name = name.katakana,
            nameKana = name.katakana,
            nameEnglish = name.latin,
            icd10Chapter = blueprint.icd10Chapter,
            medicalDepartment = DiseaseNestedBuilders.buildMedicalDepartment(
                id = diseaseId,
                chapter = blueprint.icd10Chapter,
            ),
            chronicity = blueprint.chronicity,
            infectious = blueprint.isInfectious,
            synonyms = synonyms.map { it.katakana },
            summary = DiseaseNestedBuilders.buildSummary(id = diseaseId),
            epidemiology = DiseaseNestedBuilders.buildEpidemiology(id = diseaseId),
            etiology = DiseaseNestedBuilders.buildEtiology(id = diseaseId),
            symptoms = DiseaseNestedBuilders.buildSymptoms(id = diseaseId),
            diagnosticCriteria = DiseaseNestedBuilders.buildDiagnosticCriteria(id = diseaseId),
            requiredExams = DiseaseNestedBuilders.buildRequiredExams(
                id = diseaseId,
                chapter = blueprint.icd10Chapter,
            ),
            severityGrading = DiseaseNestedBuilders.buildSeverityGrading(id = diseaseId),
            differentialDiagnoses = differentials.map { it.katakana },
            complications = complications.map { it.katakana },
            treatments = DiseaseNestedBuilders.buildTreatments(id = diseaseId),
            prognosis = DiseaseNestedBuilders.buildPrognosis(id = diseaseId),
            prevention = DiseaseNestedBuilders.buildPrevention(id = diseaseId),
            relatedDrugIds = DiseaseNestedBuilders.buildRelatedDrugIds(id = diseaseId),
            relatedDiseaseIds = DiseaseNestedBuilders.buildRelatedDiseaseIds(
                id = diseaseId,
                selfIndex = blueprint.index,
            ),
            revisedAt = DEFAULT_REVISED_AT,
        )
    }

    companion object {
        private const val SYNONYM_COUNT: Int = 2
        private const val DIFFERENTIAL_COUNT: Int = 2
        private const val COMPLICATION_COUNT: Int = 2
        private const val DISEASE_ID_PAD_LENGTH: Int = 4
        private const val DEFAULT_REVISED_AT: String = "2026/04/23"
    }
}
