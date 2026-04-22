package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext

class DiseasePlaceholderDictionary {
    fun resolve(
        key: String,
        seed: Long,
        context: DiseaseRenderContext,
    ): String {
        TODO(
            "DiseasePlaceholderDictionary.resolve not implemented yet " +
                "(called with key=$key seed=$seed selfName=${context.selfName}). " +
                "Category routing (A/B/D) must be added before placeholders can be substituted.",
        )
    }

    companion object {
        @Suppress("unused")
        const val EXPECTED_KEY_COUNT: Int = 48

        init {
            check(DiseasePlaceholderKey.values().size == EXPECTED_KEY_COUNT) {
                "DiseasePlaceholderKey must contain exactly $EXPECTED_KEY_COUNT entries"
            }
        }
    }
}
