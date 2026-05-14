package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.NumericPlaceholderRanges
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.TargetMoleculeSuffixes
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugPlaceholderDictionaryTest {
    @Test
    fun `PlaceholderKey enum contains exactly 67 keys`() {
        assertEquals(
            PLACEHOLDER_KEY_COUNT,
            PlaceholderKey.entries.size,
            "PlaceholderKey must cover exactly $PLACEHOLDER_KEY_COUNT placeholders " +
                "extracted from DrugParagraphTemplates",
        )
    }

    @Test
    fun `resolve returns non-blank value for every placeholder key`() {
        val dict = buildDict().withDosageForm(form = DosageForm.TABLET)
        PlaceholderKey.entries.forEach { key ->
            val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
            val value = dict.resolve(key.jsonKey, seed)
            assertTrue(
                value.isNotBlank(),
                "resolve('${key.jsonKey}', $seed) returned blank; " +
                    "every placeholder key must yield a non-empty substitution string",
            )
        }
    }

    @Test
    fun `resolve delegates every category-A key to MedicalVocabularyDictionary`() {
        val dict = buildDict().withDosageForm(form = DosageForm.TABLET)
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        PlaceholderKey.entries
            .filter { it.category == PlaceholderCategory.A_MEDICAL_VOCABULARY }
            .forEach { key ->
                assertEquals(
                    MedicalVocabularyDictionary.resolve(key.jsonKey, seed),
                    dict.resolve(key.jsonKey, seed),
                    "Dictionary.resolve('${key.jsonKey}', $seed) must return the same value " +
                        "as MedicalVocabularyDictionary.resolve since this is a category-A key",
                )
            }
    }

    @Test
    fun `resolve returns katakana-bearing coined name for metabolite`() {
        val dict = buildDict()
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        val value = dict.resolve("metabolite", seed)
        assertEquals(
            expected = CoinedNameSnapshot(isNotBlank = true, containsKatakana = true),
            actual = CoinedNameSnapshot(
                isNotBlank = value.isNotBlank(),
                containsKatakana = value.any { it in KATAKANA_BLOCK },
            ),
            message = "resolve('metabolite', $seed) must return a coined katakana name; got: '$value'",
        )
    }

    @Test
    fun `resolve delegates every category-D key to NumericPlaceholderRanges`() {
        val dict = buildDict()
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        PlaceholderKey.entries
            .filter { it.category == PlaceholderCategory.D_NUMERIC_RANGE }
            .forEach { key ->
                assertEquals(
                    NumericPlaceholderRanges.resolve(key.jsonKey, seed),
                    dict.resolve(key.jsonKey, seed),
                    "Dictionary.resolve('${key.jsonKey}', $seed) must return the same value " +
                        "as NumericPlaceholderRanges.resolve since this is a category-D key",
                )
            }
    }

    @Test
    fun `resolveAll substitutes every placeholder and leaves no delimiter behind`() {
        val dict = buildDict()
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        val template = "本剤投与により {{symptom}} と {{adverseReaction}} が発現することがある。"
        val result = dict.resolveAll(template = template, seed = seed)
        assertEquals(
            expected = ResolveAllSnapshot(hasRawDelimiter = false, preservesLiteralText = true),
            actual = ResolveAllSnapshot(
                hasRawDelimiter = "{{" in result || "}}" in result,
                preservesLiteralText =
                "本剤投与により " in result && " と " in result && " が発現することがある。" in result,
            ),
            message = "resolveAll must replace placeholders and preserve non-placeholder text; got: '$result'",
        )
    }

    @Test
    fun `resolveAll derives distinct seeds for repeated occurrences of the same key`() {
        val dict = buildDict()
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        val template = "{{symptom}} / {{symptom}} / {{symptom}}"
        val result = dict.resolveAll(template = template, seed = seed)
        val parts = result.split(" / ")
        assertEquals(
            expected = RepeatedPlaceholderSnapshot(segmentCount = 3, hasVariation = true),
            actual = RepeatedPlaceholderSnapshot(
                segmentCount = parts.size,
                hasVariation = parts.toSet().size >= 2,
            ),
            message = "repeated '{{symptom}}' must derive distinct seeds so substitutions vary; got: '$result'",
        )
    }

    @Test
    fun `renderField returns a fully substituted paragraph string`() {
        val dict = buildDict().withDosageForm(form = DosageForm.TABLET)
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        val rendered = dict.renderField(field = ParagraphField.STANDARD_DOSAGE, seed = seed)
        assertEquals(
            expected = RenderedFieldSnapshot(isNotBlank = true, hasRawDelimiter = false),
            actual = RenderedFieldSnapshot(
                isNotBlank = rendered.isNotBlank(),
                hasRawDelimiter = "{{" in rendered || "}}" in rendered,
            ),
            message = "renderField must return a fully substituted non-blank paragraph; got: '$rendered'",
        )
    }

    @Test
    fun `resolve throws TASK ORDER VIOLATION error for unregistered placeholder key`() {
        val dict = buildDict()
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        val exception = runCatching { dict.resolve("unknownKey", seed) }.exceptionOrNull()
        val message = exception?.message.orEmpty()
        val requiredKeywords = listOf("TASK ORDER VIOLATION", "Correct sequence", "DO NOT bypass", "unknownKey")
        assertEquals(
            expected = UnknownKeyFailureSnapshot(
                type = IllegalStateException::class.simpleName,
                missingKeywords = emptyList(),
            ),
            actual = UnknownKeyFailureSnapshot(
                type = exception?.let { it::class.simpleName },
                missingKeywords = requiredKeywords.filterNot { keyword -> keyword in message },
            ),
            message = "Unknown-placeholder error must contain ordering guidance and the offending key; got: '$message'",
        )
    }

    @Test
    fun `resolve returns a registered disease name for the disease key`() {
        val diseases = diseaseFixtures()
        val dict =
            DrugPlaceholderDictionary(
                nameAdapter = FixmergeNameAdapter(),
                diseases = diseases,
            )
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        val value = dict.resolve("disease", seed)
        val registeredNames = diseases.map { it.name }
        assertTrue(
            value in registeredNames,
            "resolve('disease', $seed) = '$value' must be one of registered disease names $registeredNames",
        )
    }

    @Test
    fun `resolve on disease key throws when disease fixture list is empty`() {
        val dict =
            DrugPlaceholderDictionary(
                nameAdapter = FixmergeNameAdapter(),
                diseases = emptyList(),
            )
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        val exception = runCatching { dict.resolve("disease", seed) }.exceptionOrNull()
        assertEquals(
            expected = DiseaseFixtureOrderFailureSnapshot(
                type = IllegalStateException::class.simpleName,
                mentionsGenerationOrderRule = true,
            ),
            actual = DiseaseFixtureOrderFailureSnapshot(
                type = exception?.let { it::class.simpleName },
                mentionsGenerationOrderRule =
                exception?.message.orEmpty().contains("Drug must be generated after Disease"),
            ),
            message = "Empty disease fixture list error must mention generation-order rule",
        )
    }

    @Test
    fun `resolve returns coined name with molecule suffix for targetMolecule`() {
        val dict = buildDict()
        val seed = stableHash(id = "drug_0002", slot = 0, index = 0)
        val value = dict.resolve("targetMolecule", seed)
        assertEquals(
            expected = TargetMoleculeSnapshot(isNotBlank = true, hasRegisteredSuffix = true),
            actual = TargetMoleculeSnapshot(
                isNotBlank = value.isNotBlank(),
                hasRegisteredSuffix = TargetMoleculeSuffixes.all.any { value.endsWith(it) },
            ),
            message = "resolve('targetMolecule', $seed) must return katakana + suffix; got: '$value'",
        )
    }

    private data class CoinedNameSnapshot(
        val isNotBlank: Boolean,
        val containsKatakana: Boolean,
    )

    private data class ResolveAllSnapshot(
        val hasRawDelimiter: Boolean,
        val preservesLiteralText: Boolean,
    )

    private data class RepeatedPlaceholderSnapshot(
        val segmentCount: Int,
        val hasVariation: Boolean,
    )

    private data class RenderedFieldSnapshot(
        val isNotBlank: Boolean,
        val hasRawDelimiter: Boolean,
    )

    private data class TargetMoleculeSnapshot(
        val isNotBlank: Boolean,
        val hasRegisteredSuffix: Boolean,
    )

    private data class UnknownKeyFailureSnapshot(
        val type: String?,
        val missingKeywords: List<String>,
    )

    private data class DiseaseFixtureOrderFailureSnapshot(
        val type: String?,
        val mentionsGenerationOrderRule: Boolean,
    )

    private fun buildDict(): DrugPlaceholderDictionary =
        DrugPlaceholderDictionary(
            nameAdapter = FixmergeNameAdapter(),
            diseases = diseaseFixtures(),
        )

    private fun diseaseFixtures(): List<Disease> =
        listOf(
            makeTestDisease(id = "disease_0000", name = "架空疾患甲"),
            makeTestDisease(id = "disease_0001", name = "架空疾患乙"),
            makeTestDisease(id = "disease_0002", name = "架空疾患丙"),
        )

    private fun makeTestDisease(
        id: String,
        name: String,
    ): Disease =
        Disease(
            id = id,
            name = name,
            nameKana = "カクウシッカン",
            icd10Chapter = Icd10Chapter.CHAPTER_X,
            medicalDepartment = listOf(MedicalDepartment.INTERNAL_MEDICINE),
            chronicity = Chronicity.CHRONIC,
            infectious = false,
            summary = "テスト用の架空疾患です。",
            etiology = "テスト用の病因です。",
            symptoms = SymptomInfo(mainSymptoms = listOf("テスト症状")),
            diagnosticCriteria = DiagnosticCriteriaInfo(required = listOf("テスト診断基準")),
            treatments = TreatmentInfo(),
            revisedAt = "2026-01-01",
        )

    private companion object {
        const val PLACEHOLDER_KEY_COUNT = 67
        val KATAKANA_BLOCK: CharRange = '゠'..'ヿ'
    }
}
