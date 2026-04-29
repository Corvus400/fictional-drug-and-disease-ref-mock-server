package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.IsoDateFormatter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.BucketNameCoiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DiseaseCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import java.time.LocalDate

class DiseaseGenerator(
    adapter: FixmergeNameAdapter,
    private val placeholderDictionary: DiseasePlaceholderDictionary,
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
        val context = DiseaseRenderContext(selfName = name.katakana)
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
            summary = DiseaseNestedBuilders.buildSummary(
                id = diseaseId,
                dict = placeholderDictionary,
                context = context,
            ),
            epidemiology = DiseaseNestedBuilders.buildEpidemiology(
                id = diseaseId,
                chapter = blueprint.icd10Chapter,
                isRareDisease = blueprint.isRareDisease,
            ),
            etiology = DiseaseNestedBuilders.buildEtiology(
                id = diseaseId,
                dict = placeholderDictionary,
                context = context,
            ),
            symptoms = DiseaseNestedBuilders.buildSymptoms(id = diseaseId),
            diagnosticCriteria = DiseaseNestedBuilders.buildDiagnosticCriteria(
                id = diseaseId,
                dict = placeholderDictionary,
                context = context,
            ),
            requiredExams = DiseaseNestedBuilders.buildRequiredExams(
                id = diseaseId,
                chapter = blueprint.icd10Chapter,
            ),
            severityGrading = DiseaseNestedBuilders.buildSeverityGrading(
                id = diseaseId,
                dict = placeholderDictionary,
                context = context,
            ),
            differentialDiagnoses = differentials.map { it.katakana },
            complications = complications.map { it.katakana },
            treatments = DiseaseNestedBuilders.buildTreatments(
                id = diseaseId,
                dict = placeholderDictionary,
                context = context,
            ),
            prognosis = DiseaseNestedBuilders.buildPrognosis(
                id = diseaseId,
                dict = placeholderDictionary,
                context = context,
            ),
            prevention = DiseaseNestedBuilders.buildPrevention(id = diseaseId),
            relatedDrugIds = DiseaseNestedBuilders.buildRelatedDrugIds(id = diseaseId),
            relatedDiseaseIds = DiseaseNestedBuilders.buildRelatedDiseaseIds(
                id = diseaseId,
                selfIndex = blueprint.index,
            ),
            revisedAt = revisedAtFor(blueprint = blueprint),
        )
    }

    companion object {
        private const val SYNONYM_COUNT: Int = 2
        private const val DIFFERENTIAL_COUNT: Int = 2
        private const val COMPLICATION_COUNT: Int = 2
        private const val DISEASE_ID_PAD_LENGTH: Int = 4
        private val REVISED_AT_BASE: LocalDate = LocalDate.of(2026, 4, 23)
        private const val REVISED_AT_SPREAD_DAYS: Int = 90

        private fun revisedAtFor(blueprint: DiseaseBlueprint): String =
            IsoDateFormatter.formatDate(
                date = REVISED_AT_BASE.minusDays((blueprint.index % REVISED_AT_SPREAD_DAYS).toLong()),
            )
    }
}
