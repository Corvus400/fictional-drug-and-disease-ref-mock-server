package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderDelimiter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DiseasePlaceholderDictionaryTest {
    @Test
    fun `DiseasePlaceholderKey enum contains exactly 48 keys`() {
        assertEquals(
            DISEASE_PLACEHOLDER_KEY_COUNT,
            DiseasePlaceholderKey.entries.size,
            "DiseasePlaceholderKey must cover exactly $DISEASE_PLACEHOLDER_KEY_COUNT placeholders " +
                "extracted from DiseaseParagraphTemplates",
        )
    }

    @Test
    fun `resolve returns non-blank value for every placeholder key`() {
        val dict = buildDict()
        val context = DiseaseRenderContext(selfName = "架空疾患テスト甲")
        DiseasePlaceholderKey.entries.forEach { key ->
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
        assertEquals(
            expected = listOf("A", "B"),
            actual = listOf(first, second),
            message = "disease self-reference placeholder must resolve to each context selfName",
        )
    }

    @Test
    fun `resolveAll substitutes every placeholder and leaves no delimiter`() {
        val dict = buildDict()
        val context = DiseaseRenderContext(selfName = "架空疾患テスト甲")
        val template = "{{disease}}は、{{mainFeature}}を特徴とする{{chronicity}}疾患である。"
        val result = dict.resolveAll(template, seed = 42L, context = context)
        assertEquals(
            expected = ResolveAllSnapshot(hasRawDelimiter = false, containsSelfName = true),
            actual = ResolveAllSnapshot(
                hasRawDelimiter = result.contains(DiseasePlaceholderDelimiter.OPEN) ||
                    result.contains(DiseasePlaceholderDelimiter.CLOSE),
                containsSelfName = result.contains("架空疾患テスト甲"),
            ),
            message = "resolveAll must substitute placeholders and preserve disease self-reference; got: $result",
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
        assertEquals(
            expected = RepeatedPlaceholderSnapshot(segmentCount = 3, hasVariation = true),
            actual = RepeatedPlaceholderSnapshot(
                segmentCount = occurrences.size,
                hasVariation = occurrences.toSet().size >= 2,
            ),
            message = "same-key placeholders at different match positions must derive seeds independently; " +
                "got: $result",
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
        assertEquals(
            expected = RenderedFieldSnapshot(isNotBlank = true, hasRawDelimiter = false),
            actual = RenderedFieldSnapshot(
                isNotBlank = rendered.isNotBlank(),
                hasRawDelimiter = rendered.contains(DiseasePlaceholderDelimiter.OPEN) ||
                    rendered.contains(DiseasePlaceholderDelimiter.CLOSE),
            ),
            message = "renderField must produce a substituted non-empty paragraph; got: $rendered",
        )
    }

    private data class ResolveAllSnapshot(
        val hasRawDelimiter: Boolean,
        val containsSelfName: Boolean,
    )

    private data class RepeatedPlaceholderSnapshot(
        val segmentCount: Int,
        val hasVariation: Boolean,
    )

    private data class RenderedFieldSnapshot(
        val isNotBlank: Boolean,
        val hasRawDelimiter: Boolean,
    )

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
