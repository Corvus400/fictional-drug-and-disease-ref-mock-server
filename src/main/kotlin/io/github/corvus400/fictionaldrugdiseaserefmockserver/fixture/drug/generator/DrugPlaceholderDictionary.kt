package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.NumericPlaceholderRanges
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderContractMessages
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderDelimiter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.TargetMoleculeSuffixes
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm

class DrugPlaceholderDictionary(
    private val medicalVocabulary: MedicalVocabularyDictionary = MedicalVocabularyDictionary,
    private val numericRanges: NumericPlaceholderRanges = NumericPlaceholderRanges,
    private val nameAdapter: FixmergeNameAdapter,
    private val diseases: List<Disease>,
    private val defaultContext: BucketContextKey = BucketContextKey.Global,
    private val diseaseNameOverride: String? = null,
    private val dosageForm: DosageForm? = null,
) {
    fun withContext(context: BucketContextKey): DrugPlaceholderDictionary =
        DrugPlaceholderDictionary(
            medicalVocabulary = medicalVocabulary,
            numericRanges = numericRanges,
            nameAdapter = nameAdapter,
            diseases = diseases,
            defaultContext = context,
            diseaseNameOverride = diseaseNameOverride,
            dosageForm = dosageForm,
        )

    fun withDiseaseNameOverride(diseaseName: String): DrugPlaceholderDictionary =
        DrugPlaceholderDictionary(
            medicalVocabulary = medicalVocabulary,
            numericRanges = numericRanges,
            nameAdapter = nameAdapter,
            diseases = diseases,
            defaultContext = defaultContext,
            diseaseNameOverride = diseaseName,
            dosageForm = dosageForm,
        )

    fun withDosageForm(form: DosageForm): DrugPlaceholderDictionary =
        DrugPlaceholderDictionary(
            medicalVocabulary = medicalVocabulary,
            numericRanges = numericRanges,
            nameAdapter = nameAdapter,
            diseases = diseases,
            defaultContext = defaultContext,
            diseaseNameOverride = diseaseNameOverride,
            dosageForm = form,
        )

    fun resolve(
        key: String,
        seed: Long,
        context: BucketContextKey = defaultContext,
    ): String {
        val placeholderKey = PlaceholderKey.fromJsonKey(key) ?: throwUnknownPlaceholderError(key)
        return when (placeholderKey.category) {
            PlaceholderCategory.A_MEDICAL_VOCABULARY -> medicalVocabulary.resolve(key, seed, context)
            PlaceholderCategory.B_COINED_NAME -> resolveCoinedName(placeholderKey, seed)
            PlaceholderCategory.C_DISEASE_REFERENCE -> resolveDiseaseReference(seed)
            PlaceholderCategory.D_NUMERIC_RANGE -> numericRanges.resolve(key, seed)
            PlaceholderCategory.E_DOSAGE_FORM -> resolveDosageFormPlaceholder(placeholderKey, seed)
        }
    }

    fun resolveAll(
        template: String,
        seed: Long,
        context: BucketContextKey = defaultContext,
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
                resolve(key, derivedSeed, context)
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
        context: BucketContextKey = defaultContext,
    ): String = resolveAll(template = DrugParagraphTemplates.pickTemplate(field, seed), seed = seed, context = context)

    private fun throwUnknownPlaceholderError(key: String): Nothing =
        error(PlaceholderContractMessages.unknownPlaceholderError(key = key))

    private fun resolveDiseaseReference(seed: Long): String {
        diseaseNameOverride?.let { diseaseName -> return diseaseName }
        check(diseases.isNotEmpty()) {
            "Disease fixture list is empty. " +
                "Generation order may be wrong — Drug must be generated after Disease, " +
                "so that {{disease}} placeholder can reference an existing disease fixture."
        }
        return ValueRangeGenerator.pickOne(seed, diseases).name
    }

    private fun resolveDosageFormPlaceholder(
        key: PlaceholderKey,
        seed: Long,
    ): String {
        val form = dosageForm ?: error("Dosage form is required to resolve '${key.jsonKey}' placeholder")
        return when (key) {
            PlaceholderKey.ADMINISTRATION_VERB -> DosageFormDoseTextUnit.administrationVerb(form = form)
            PlaceholderKey.MAX_DAILY_DOSE -> DosageFormDoseTextUnit.maxDailyDose(form = form, seed = seed)
            PlaceholderKey.PACKAGING_UNIT -> DosageFormDoseTextUnit.unitFor(form = form)
            else -> error("Unreachable: category E_DOSAGE_FORM contains only dosage form placeholders, got $key")
        }
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
