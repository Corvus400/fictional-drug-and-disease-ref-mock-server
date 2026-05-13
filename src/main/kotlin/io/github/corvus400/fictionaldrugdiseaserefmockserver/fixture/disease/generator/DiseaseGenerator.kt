package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.IsoDateFormatter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.BucketNameCoiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DiseaseCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import java.time.LocalDate

class DiseaseGenerator(
    adapter: FixmergeNameAdapter,
    private val placeholderDictionary: DiseasePlaceholderDictionary,
    private val drugs: List<Drug> = emptyList(),
) {
    private val coiner: BucketNameCoiner = BucketNameCoiner(adapter = adapter)

    fun generate(blueprint: DiseaseBlueprint): Disease {
        val diseaseId =
            "disease_${blueprint.index.toString().padStart(length = DISEASE_ID_PAD_LENGTH, padChar = '0')}"
        val country = DiseaseCountryMapping.of(chapter = blueprint.icd10Chapter)
        val cities = CountryBucketRepository.of(country = country).cities
        val name =
            coiner.coin(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_NAME,
                offset = 0,
            )
        val synonyms = (0 until DiseaseSeedDerivedValues.synonymCount(id = diseaseId)).map { offset ->
            coiner.coin(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_ALIAS,
                offset = offset,
            )
        }
        val differentials = (0 until DiseaseSeedDerivedValues.differentialCount(id = diseaseId)).map { offset ->
            coiner.coin(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_DIFFERENTIAL,
                offset = offset,
            )
        }
        val complications = (0 until DiseaseSeedDerivedValues.complicationCount(id = diseaseId)).map { offset ->
            coiner.coin(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_COMPLICATION,
                offset = offset,
            )
        }
        val generated =
            buildDisease(
                blueprint = blueprint,
                name = name,
                synonyms = synonyms,
                differentials = differentials,
                complications = complications,
            )
        return DISEASE_FINAL_OVERRIDES[generated.id]?.invoke(generated) ?: generated
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
        val contextualDictionary =
            placeholderDictionary.withContext(
                bucketContext = BucketContextKey.DiseaseContext(chapter = blueprint.icd10Chapter),
            )
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
                dict = contextualDictionary,
                context = context,
            ),
            epidemiology = DiseaseNestedBuilders.buildEpidemiology(
                id = diseaseId,
                chapter = blueprint.icd10Chapter,
                isRareDisease = blueprint.isRareDisease,
            ),
            etiology = DiseaseNestedBuilders.buildEtiology(
                id = diseaseId,
                dict = contextualDictionary,
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
            severityGrading = if (blueprint.icd10Chapter in CHAPTERS_REQUIRING_SEVERITY_GRADING) {
                DiseaseNestedBuilders.buildSeverityGrading(
                    id = diseaseId,
                    chapter = blueprint.icd10Chapter,
                    dict = contextualDictionary,
                    context = context,
                )
            } else {
                null
            },
            differentialDiagnoses = differentials.map { it.katakana },
            complications = complications.map { it.katakana },
            treatments = DiseaseNestedBuilders.buildTreatments(
                id = diseaseId,
                chapter = blueprint.icd10Chapter,
                dict = contextualDictionary,
                context = context,
            ),
            prognosis = DiseaseNestedBuilders.buildPrognosis(
                id = diseaseId,
                dict = contextualDictionary,
                context = context,
            ),
            prevention = DiseaseNestedBuilders.buildPrevention(
                id = diseaseId,
                chapter = blueprint.icd10Chapter,
            ),
            relatedDrugIds = DiseaseNestedBuilders.buildRelatedDrugIds(
                id = diseaseId,
                chapter = blueprint.icd10Chapter,
                drugFixtures = drugs,
            ),
            relatedDiseaseIds = DiseaseNestedBuilders.buildRelatedDiseaseIds(
                id = diseaseId,
                selfIndex = blueprint.index,
            ),
            revisedAt = revisedAtFor(blueprint = blueprint),
        )
    }

    companion object {
        private const val DISEASE_ID_PAD_LENGTH: Int = 4
        private val REVISED_AT_BASE: LocalDate = LocalDate.of(2026, 4, 23)
        internal const val REVISED_AT_SPREAD_DAYS: Int = 90

        // 仕様: severityGrading は Ch.II (新生物) と Ch.IX (循環器系) のみ非 null。
        // 他章は Disease モデルのデフォルト値 null を維持する。
        // DiseaseFixtureValidator.chapterTwoViolations / chapterNineViolations と整合。
        private val CHAPTERS_REQUIRING_SEVERITY_GRADING: Set<Icd10Chapter> = setOf(
            Icd10Chapter.CHAPTER_II,
            Icd10Chapter.CHAPTER_IX,
        )

        private fun revisedAtFor(blueprint: DiseaseBlueprint): String =
            IsoDateFormatter.formatDate(
                date = REVISED_AT_BASE.minusDays((blueprint.index % REVISED_AT_SPREAD_DAYS).toLong()),
            )
    }
}
