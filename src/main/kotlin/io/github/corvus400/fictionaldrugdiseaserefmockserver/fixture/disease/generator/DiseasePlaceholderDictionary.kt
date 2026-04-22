package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseMedicalVocabulary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseNumericPlaceholderRanges
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderContractMessages
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext

class DiseasePlaceholderDictionary(
    private val medicalVocabulary: DiseaseMedicalVocabulary = DiseaseMedicalVocabulary,
    private val numericRanges: DiseaseNumericPlaceholderRanges = DiseaseNumericPlaceholderRanges,
) {
    fun resolve(
        key: String,
        seed: Long,
        context: DiseaseRenderContext,
    ): String {
        val placeholderKey =
            DiseasePlaceholderKey.fromJsonKey(key)
                ?: error(DiseasePlaceholderContractMessages.unknownPlaceholderError(key = key))
        return when (placeholderKey.category) {
            DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY -> medicalVocabulary.resolve(key, seed)
            DiseasePlaceholderCategory.B_SELF_REFERENCE -> context.selfName
            DiseasePlaceholderCategory.D_NUMERIC_RANGE -> numericRanges.resolve(key, seed)
        }
    }

    companion object {
        const val EXPECTED_KEY_COUNT: Int = 48

        init {
            check(DiseasePlaceholderKey.values().size == EXPECTED_KEY_COUNT) {
                "DiseasePlaceholderKey must contain exactly $EXPECTED_KEY_COUNT entries"
            }
        }
    }
}
