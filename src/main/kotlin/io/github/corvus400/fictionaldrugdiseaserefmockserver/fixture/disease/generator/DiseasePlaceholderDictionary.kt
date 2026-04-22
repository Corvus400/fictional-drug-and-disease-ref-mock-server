package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseMedicalVocabulary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext

class DiseasePlaceholderDictionary(
    private val medicalVocabulary: DiseaseMedicalVocabulary = DiseaseMedicalVocabulary,
) {
    fun resolve(
        key: String,
        seed: Long,
        context: DiseaseRenderContext,
    ): String {
        val placeholderKey =
            DiseasePlaceholderKey.fromJsonKey(key)
                ?: TODO(
                    "Unknown-placeholder error path not implemented yet (key=$key seed=$seed selfName=${context.selfName})",
                )
        return when (placeholderKey.category) {
            DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY -> medicalVocabulary.resolve(key, seed)
            DiseasePlaceholderCategory.B_SELF_REFERENCE -> context.selfName
            DiseasePlaceholderCategory.D_NUMERIC_RANGE ->
                TODO("D_NUMERIC_RANGE not implemented yet (key=$key)")
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
