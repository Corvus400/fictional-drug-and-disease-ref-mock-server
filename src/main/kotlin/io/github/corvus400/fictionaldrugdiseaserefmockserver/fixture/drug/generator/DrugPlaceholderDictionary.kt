package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.NumericPlaceholderRanges
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderDelimiter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.TargetMoleculeSuffixes
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash

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
        val placeholderKey = PlaceholderKey.fromJsonKey(key) ?: throwUnknownPlaceholderError(key)
        return when (placeholderKey.category) {
            PlaceholderCategory.A_MEDICAL_VOCABULARY -> medicalVocabulary.resolve(key, seed)
            PlaceholderCategory.B_COINED_NAME -> resolveCoinedName(placeholderKey, seed)
            PlaceholderCategory.C_DISEASE_REFERENCE -> resolveDiseaseReference(seed)
            PlaceholderCategory.D_NUMERIC_RANGE -> numericRanges.resolve(key, seed)
        }
    }

    fun resolveAll(
        template: String,
        seed: Long,
    ): String {
        val result =
            PlaceholderDelimiter.REGEX.replace(template) { match ->
                val key = match.groupValues[1]
                val derivedSeed =
                    stableHash(
                        id = "$seed:$key:${match.range.first}",
                        slot = 0,
                        index = 0,
                    )
                resolve(key, derivedSeed)
            }
        check(PlaceholderDelimiter.OPEN !in result && PlaceholderDelimiter.CLOSE !in result) {
            "Raw placeholder delimiter survived resolveAll — a resolver branch must have returned a " +
                "string still containing '${PlaceholderDelimiter.OPEN}' or '${PlaceholderDelimiter.CLOSE}'. " +
                "Inspect resolve() outputs. result='$result'"
        }
        return result
    }

    fun renderField(
        field: ParagraphField,
        seed: Long,
    ): String = resolveAll(template = DrugParagraphTemplates.pickTemplate(field, seed), seed = seed)

    private fun throwUnknownPlaceholderError(key: String): Nothing =
        error(
            """
            Unknown placeholder '{{$key}}' found in DrugParagraphTemplates but not in DrugPlaceholderDictionary.

            TASK ORDER VIOLATION:
            Placeholder keys MUST NOT be added to DrugParagraphTemplates before
            their replacement logic exists in DrugPlaceholderDictionary.

            Correct sequence:
              (1) Add resolver case for '$key' to DrugPlaceholderDictionary
              (2) Run tests and confirm resolve() succeeds
              (3) THEN add the '{{$key}}' placeholder to a template string

            DO NOT bypass this error with try/catch, runCatching, or null-fallback.
            That reintroduces PR #205's raw-placeholder leak (Issue #206).
            """.trimIndent(),
        )

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
