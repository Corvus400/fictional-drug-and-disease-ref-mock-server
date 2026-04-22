package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DiseaseCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo

class DiseaseGenerator(
    private val adapter: FixmergeNameAdapter,
) {
    fun generate(blueprint: DiseaseBlueprint): Disease {
        val country = DiseaseCountryMapping.of(chapter = blueprint.icd10Chapter)
        val cities = CountryBucketRepository.of(country = country).cities
        val name =
            coinFromBucket(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_NAME,
                offset = 0,
            )
        val synonyms = (0 until SYNONYM_COUNT).map { offset ->
            coinFromBucket(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_ALIAS,
                offset = offset,
            )
        }
        val differentials = (0 until DIFFERENTIAL_COUNT).map { offset ->
            coinFromBucket(
                bucket = cities,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DISEASE_DIFFERENTIAL,
                offset = offset,
            )
        }
        val complications = (0 until COMPLICATION_COUNT).map { offset ->
            coinFromBucket(
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

    private fun coinFromBucket(
        bucket: List<String>,
        blueprintIndex: Int,
        slot: NameSlot,
        offset: Int,
    ): CoinedName {
        val sourceIndex = ((blueprintIndex * SEED_INDEX_PRIME) + slot.ordinal + offset).mod(other = bucket.size)
        val sourceToken = bucket[sourceIndex]
        val seed = stableHash(id = sourceToken, slot = slot.ordinal, index = 0)
        return adapter.coin(slot = slot, seed = seed)
    }

    private fun buildDisease(
        blueprint: DiseaseBlueprint,
        name: CoinedName,
        synonyms: List<CoinedName>,
        differentials: List<CoinedName>,
        complications: List<CoinedName>,
    ): Disease {
        return Disease(
            id = "disease_${blueprint.index.toString().padStart(length = DISEASE_ID_PAD_LENGTH, padChar = '0')}",
            name = name.katakana,
            nameKana = name.katakana,
            nameEnglish = name.latin,
            icd10Chapter = blueprint.icd10Chapter,
            medicalDepartment = listOf(MedicalDepartment.INTERNAL_MEDICINE),
            chronicity = blueprint.chronicity,
            infectious = blueprint.isInfectious,
            synonyms = synonyms.map { it.katakana },
            summary = DEFAULT_SUMMARY,
            etiology = DEFAULT_ETIOLOGY,
            symptoms = SymptomInfo(mainSymptoms = listOf(DEFAULT_MAIN_SYMPTOM)),
            diagnosticCriteria = DiagnosticCriteriaInfo(required = listOf(DEFAULT_DIAGNOSTIC_REQUIREMENT)),
            differentialDiagnoses = differentials.map { it.katakana },
            complications = complications.map { it.katakana },
            treatments = TreatmentInfo(),
            revisedAt = DEFAULT_REVISED_AT,
        )
    }

    companion object {
        private const val SEED_INDEX_PRIME: Int = 31
        private const val SYNONYM_COUNT: Int = 2
        private const val DIFFERENTIAL_COUNT: Int = 2
        private const val COMPLICATION_COUNT: Int = 2
        private const val DISEASE_ID_PAD_LENGTH: Int = 4
        private const val DEFAULT_SUMMARY: String = "本疾患の概要記述"
        private const val DEFAULT_ETIOLOGY: String = "病因は複合的要因によると考えられる"
        private const val DEFAULT_MAIN_SYMPTOM: String = "倦怠感"
        private const val DEFAULT_DIAGNOSTIC_REQUIREMENT: String = "臨床症状の確認"
        private const val DEFAULT_REVISED_AT: String = "2026/04/23"
    }
}
