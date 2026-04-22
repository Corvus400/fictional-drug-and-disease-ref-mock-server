package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiseasePlaceholderDictionaryTest {
    @Test
    fun `DiseasePlaceholderKey enum contains exactly 48 keys`() {
        assertEquals(
            DISEASE_PLACEHOLDER_KEY_COUNT,
            DiseasePlaceholderKey.values().size,
            "DiseasePlaceholderKey must cover exactly $DISEASE_PLACEHOLDER_KEY_COUNT placeholders " +
                "extracted from DiseaseParagraphTemplates",
        )
    }

    @Test
    fun `resolve returns non-blank value for every placeholder key`() {
        val dict = buildDict()
        val context = DiseaseRenderContext(selfName = "架空疾患テスト甲")
        DiseasePlaceholderKey.values().forEach { key ->
            val seed = stableHash(id = "disease_0001:${key.jsonKey}", slot = 0, index = 0)
            val value = dict.resolve(key.jsonKey, seed, context)
            assertTrue(
                value.isNotBlank(),
                "resolve('${key.jsonKey}', $seed, context) returned blank; " +
                    "every placeholder key must yield a non-empty substitution string",
            )
        }
    }

    private fun buildDict(): DiseasePlaceholderDictionary = DiseasePlaceholderDictionary()

    companion object {
        private const val DISEASE_PLACEHOLDER_KEY_COUNT: Int = 48
    }
}
