package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderDelimiter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
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

    @Test
    fun `resolve returns context selfName for disease self-reference placeholder`() {
        val dict = buildDict()
        val context = DiseaseRenderContext(selfName = "架空疾患テスト甲")
        val value = dict.resolve("disease", seed = 0L, context = context)
        assertEquals(
            "架空疾患テスト甲",
            value,
            "category B_SELF_REFERENCE must return the current disease's selfName verbatim",
        )
    }

    @Test
    fun `resolve for disease placeholder varies when selfName changes`() {
        val dict = buildDict()
        val first = dict.resolve("disease", seed = 0L, context = DiseaseRenderContext(selfName = "A"))
        val second = dict.resolve("disease", seed = 0L, context = DiseaseRenderContext(selfName = "B"))
        assertEquals("A", first)
        assertEquals("B", second)
    }

    @Test
    fun `resolveAll substitutes every placeholder and leaves no delimiter`() {
        val dict = buildDict()
        val context = DiseaseRenderContext(selfName = "架空疾患テスト甲")
        val template = "{{disease}}は、{{mainFeature}}を特徴とする{{chronicity}}疾患である。"
        val result = dict.resolveAll(template, seed = 42L, context = context)
        assertFalse(
            result.contains(DiseasePlaceholderDelimiter.OPEN) ||
                result.contains(DiseasePlaceholderDelimiter.CLOSE),
            "resolveAll must consume every placeholder; got: $result",
        )
        assertTrue(
            result.contains("架空疾患テスト甲"),
            "disease self-reference must be present in resolved output; got: $result",
        )
    }

    @Test
    fun `resolveAll is deterministic for the same seed and template`() {
        val dict = buildDict()
        val context = DiseaseRenderContext(selfName = "架空疾患テスト甲")
        val template = "{{mainFeature}} + {{mainSymptom}} + {{prognosisRate}}"
        val first = dict.resolveAll(template, seed = 42L, context = context)
        val second = dict.resolveAll(template, seed = 42L, context = context)
        assertEquals(first, second)
    }

    @Test
    fun `resolveAll varies substitutions across match positions for the same key`() {
        val dict = buildDict()
        val context = DiseaseRenderContext(selfName = "架空疾患テスト甲")
        val template = "{{mainSymptom}} / {{mainSymptom}} / {{mainSymptom}}"
        val result = dict.resolveAll(template, seed = 42L, context = context)
        val occurrences = result.split(" / ")
        assertEquals(3, occurrences.size, "template must yield exactly 3 segments")
        val distinctCount = occurrences.toSet().size
        assertTrue(
            distinctCount >= 2,
            "same-key placeholders at different match positions must derive seeds independently; " +
                "got identical substitutions at every position: $result",
        )
    }

    @Test
    fun `renderField picks a template from DiseaseParagraphTemplates and substitutes placeholders`() {
        val dict = buildDict()
        val context = DiseaseRenderContext(selfName = "架空疾患テスト甲")
        val rendered =
            dict.renderField(
                field = DiseaseParagraphField.OVERVIEW_DESCRIPTION,
                seed = 42L,
                context = context,
            )
        assertTrue(rendered.isNotBlank(), "renderField must produce a non-empty paragraph")
        assertFalse(
            rendered.contains(DiseasePlaceholderDelimiter.OPEN) ||
                rendered.contains(DiseasePlaceholderDelimiter.CLOSE),
            "renderField must consume every placeholder in the picked template; got: $rendered",
        )
    }

    @Test
    fun `resolve throws TASK ORDER VIOLATION for unknown placeholder key`() {
        val dict = buildDict()
        val context = DiseaseRenderContext(selfName = "架空疾患テスト甲")
        val error =
            assertFailsWith<IllegalStateException> {
                dict.resolve("unknownPlaceholderKey", seed = 0L, context = context)
            }
        val message = error.message.orEmpty()
        listOf(
            "unknownPlaceholderKey",
            "TASK ORDER VIOLATION",
            "DiseaseParagraphTemplates",
            "DiseasePlaceholderDictionary",
            "DO NOT bypass",
        ).forEach { keyword ->
            assertTrue(
                message.contains(keyword),
                "error message must mention '$keyword' to direct developers to the correct fix; got: $message",
            )
        }
    }

    private fun buildDict(): DiseasePlaceholderDictionary = DiseasePlaceholderDictionary()

    companion object {
        private const val DISEASE_PLACEHOLDER_KEY_COUNT: Int = 48
    }
}
