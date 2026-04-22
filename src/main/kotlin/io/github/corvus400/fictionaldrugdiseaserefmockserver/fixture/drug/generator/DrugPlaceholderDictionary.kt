package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.NumericPlaceholderRanges
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.TargetMoleculeSuffixes
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot

class DrugPlaceholderDictionary(
    private val medicalVocabulary: MedicalVocabularyDictionary = MedicalVocabularyDictionary,
    private val numericRanges: NumericPlaceholderRanges = NumericPlaceholderRanges,
    private val nameAdapter: FixmergeNameAdapter,
    private val diseaseProvider: DiseaseFixtureProvider,
) {
    fun resolve(
        key: String,
        seed: Long,
    ): String {
        val placeholderKey =
            PlaceholderKey.fromJsonKey(key)
                ?: TODO("sub-cycle 3-6 will convert unknown-key branch into a TASK ORDER VIOLATION error")
        return when (placeholderKey.category) {
            PlaceholderCategory.A_MEDICAL_VOCABULARY -> medicalVocabulary.resolve(key, seed)
            PlaceholderCategory.B_COINED_NAME -> resolveCoinedName(placeholderKey, seed)
            PlaceholderCategory.C_DISEASE_REFERENCE -> resolveDiseaseReference(seed)
            PlaceholderCategory.D_NUMERIC_RANGE -> numericRanges.resolve(key, seed)
        }
    }

    private fun resolveDiseaseReference(seed: Long): String {
        check(diseaseProvider.all.isNotEmpty()) {
            "DiseaseFixtureProvider is empty. " +
                "Generation order may be wrong — Drug must be generated after Disease, " +
                "so that {{disease}} placeholder can reference an existing disease fixture."
        }
        return ValueRangeGenerator.pickOne(seed, diseaseProvider.all).name
    }

    private fun resolveCoinedName(
        key: PlaceholderKey,
        seed: Long,
    ): String {
        val katakana = nameAdapter.coin(slot = NameSlot.DRUG_GENERIC, seed = seed).katakana
        return when (key) {
            PlaceholderKey.METABOLITE -> katakana
            PlaceholderKey.TARGET_MOLECULE -> katakana + ValueRangeGenerator.pickOne(seed, TargetMoleculeSuffixes.all)
            else -> error(
                "Unreachable: category B_COINED_NAME contains only METABOLITE and TARGET_MOLECULE, got $key",
            )
        }
    }
}
