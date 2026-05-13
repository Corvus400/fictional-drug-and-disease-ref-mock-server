package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseMedicalVocabulary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseNumericPlaceholderRanges
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderContractMessages
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderDelimiter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash

class DiseasePlaceholderDictionary(
    private val medicalVocabulary: DiseaseMedicalVocabulary = DiseaseMedicalVocabulary,
    private val numericRanges: DiseaseNumericPlaceholderRanges = DiseaseNumericPlaceholderRanges,
    private val defaultBucketContext: BucketContextKey = BucketContextKey.Global,
) {
    fun withContext(bucketContext: BucketContextKey): DiseasePlaceholderDictionary =
        DiseasePlaceholderDictionary(
            medicalVocabulary = medicalVocabulary,
            numericRanges = numericRanges,
            defaultBucketContext = bucketContext,
        )

    fun resolve(
        key: String,
        seed: Long,
        context: DiseaseRenderContext,
        bucketContext: BucketContextKey = defaultBucketContext,
    ): String {
        val placeholderKey =
            DiseasePlaceholderKey.fromJsonKey(key)
                ?: error(DiseasePlaceholderContractMessages.unknownPlaceholderError(key = key))
        return when (placeholderKey.category) {
            DiseasePlaceholderCategory.A_MEDICAL_VOCABULARY -> medicalVocabulary.resolve(key, seed, bucketContext)
            DiseasePlaceholderCategory.B_SELF_REFERENCE -> context.selfName
            DiseasePlaceholderCategory.D_NUMERIC_RANGE -> numericRanges.resolve(key, seed)
        }
    }

    fun resolveAll(
        template: String,
        seed: Long,
        context: DiseaseRenderContext,
        bucketContext: BucketContextKey = defaultBucketContext,
    ): String {
        val result =
            DiseasePlaceholderDelimiter.REGEX.replace(template) { match ->
                val key = match.groupValues[1]
                val derivedSeed =
                    stableHash(
                        id = "$seed:$key:${match.range.first}",
                        slot = 0,
                        index = 0,
                    )
                resolve(key, derivedSeed, context, bucketContext)
            }
        check(
            DiseasePlaceholderDelimiter.OPEN !in result && DiseasePlaceholderDelimiter.CLOSE !in result,
        ) {
            "Raw placeholder delimiter survived resolveAll — a resolver branch must have returned a " +
                "string still containing '${DiseasePlaceholderDelimiter.OPEN}' or " +
                "'${DiseasePlaceholderDelimiter.CLOSE}'. Inspect resolve() outputs. result='$result'"
        }
        return result
    }

    fun renderField(
        field: DiseaseParagraphField,
        seed: Long,
        context: DiseaseRenderContext,
        bucketContext: BucketContextKey = defaultBucketContext,
    ): String =
        resolveAll(
            template = DiseaseParagraphTemplates.pickTemplate(field = field, seed = seed),
            seed = seed,
            context = context,
            bucketContext = bucketContext,
        )

    companion object {
        const val EXPECTED_KEY_COUNT: Int = 48

        init {
            check(DiseasePlaceholderKey.entries.size == EXPECTED_KEY_COUNT) {
                "DiseasePlaceholderKey must contain exactly $EXPECTED_KEY_COUNT entries"
            }
        }
    }
}
