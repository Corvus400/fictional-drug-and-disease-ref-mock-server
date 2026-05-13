package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderContractMessages
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderDelimiter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DiseaseCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DiseaseGeneratorTest {
    private val adapter: FixmergeNameAdapter = FixmergeNameAdapter()
    private val placeholderDictionary: DiseasePlaceholderDictionary = DiseasePlaceholderDictionary()
    private val generator: DiseaseGenerator =
        DiseaseGenerator(adapter = adapter, placeholderDictionary = placeholderDictionary)

    private val sampleBlueprint: DiseaseBlueprint =
        DiseaseBlueprint(
            index = 0,
            icd10Chapter = Icd10Chapter.CHAPTER_I,
            chronicity = Chronicity.ACUTE,
            isInfectious = true,
            isMentalDisorder = false,
            isRareDisease = false,
        )

    @Test
    fun `generate returns a Disease with non-blank required name fields`() {
        val disease = generator.generate(blueprint = sampleBlueprint)
        val firstViolation = listOfNotNull(
            "id blank".takeIf { disease.id.isBlank() },
            "name blank".takeIf { disease.name.isBlank() },
            "nameKana blank".takeIf { disease.nameKana.isBlank() },
            "nameEnglish blank or null".takeIf { disease.nameEnglish.isNullOrBlank() },
        ).firstOrNull()

        assertEquals(expected = null, actual = firstViolation)
    }

    @Test
    fun `generate returns revisedAt in ISO 8601 YYYY-MM-DD form`() {
        val disease = generator.generate(blueprint = sampleBlueprint)
        assertTrue(
            actual = disease.revisedAt.matches(ISO_8601_DATE_PATTERN),
            message = "revisedAt must be ISO 8601 YYYY-MM-DD but was '${disease.revisedAt}'",
        )
    }

    @Test
    fun `generate is deterministic for the same blueprint given fresh adapter instances`() {
        val first = DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        ).generate(blueprint = sampleBlueprint)
        val second = DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        ).generate(blueprint = sampleBlueprint)
        assertEquals(first, second)
    }

    @Test
    fun `name equals nameKana because both derive from the same CoinedName katakana`() {
        val disease = generator.generate(blueprint = sampleBlueprint)
        assertEquals(disease.name, disease.nameKana)
    }

    @Test
    fun `nameEnglish is latin and differs from the katakana name`() {
        val disease = generator.generate(blueprint = sampleBlueprint)
        val english = disease.nameEnglish
        assertEquals(
            expected = NameEnglishSnapshot(nonBlank = true, differsFromName = true),
            actual = NameEnglishSnapshot(
                nonBlank = !english.isNullOrBlank(),
                differsFromName = english != disease.name,
            ),
            message = "nameEnglish must be non-blank latin text distinct from the katakana name",
        )
    }

    @Test
    fun `synonyms differentials and complications contain no cuisine or beverage raw tokens`() {
        val disease = generator.generate(blueprint = sampleBlueprint)
        val country = DiseaseCountryMapping.of(chapter = sampleBlueprint.icd10Chapter)
        val bucket = CountryBucketRepository.of(country = country)
        val collected: List<String> =
            listOf(disease.name, disease.nameKana) +
                disease.synonyms +
                disease.differentialDiagnoses +
                disease.complications
        for (raw in bucket.cuisine + bucket.beverage) {
            for (value in collected) {
                assertFalse(
                    value.contains(other = raw),
                    "disease value '$value' leaks non-cities raw token '$raw'",
                )
            }
        }
    }

    @Test
    fun `generate bulk returns one disease per blueprint with sequential ids`() {
        val blueprints =
            listOf(
                sampleBlueprint,
                sampleBlueprint.copy(index = 1, icd10Chapter = Icd10Chapter.CHAPTER_II),
                sampleBlueprint.copy(index = 2, icd10Chapter = Icd10Chapter.CHAPTER_V),
            )
        val diseases = generator.generate(blueprints = blueprints)
        assertEquals(
            expected = listOf("disease_0000", "disease_0001", "disease_0002"),
            actual = diseases.map { disease -> disease.id },
        )
    }

    @Test
    fun `generate bulk handles the full disease factory inventory deterministically given fresh adapter instances`() {
        val blueprints = DiseaseBlueprintFactory.build()
        val first = DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        ).generate(blueprints = blueprints)
        val second = DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        ).generate(blueprints = blueprints)
        val firstViolation = listOfNotNull(
            "expected ${blueprints.size} diseases but got ${first.size}".takeIf { first.size != blueprints.size },
            "fresh generators produced different disease inventories".takeIf { first != second },
            "disease ids are not unique".takeIf { first.map { disease -> disease.id }.toSet().size != first.size },
            first.firstOrNull { disease -> disease.name.isBlank() }?.let { disease -> "name blank for ${disease.id}" },
            first.firstOrNull { disease -> disease.nameKana.isBlank() }
                ?.let { disease -> "nameKana blank for ${disease.id}" },
        ).firstOrNull()
        assertTrue(
            actual = firstViolation == null,
            message = firstViolation ?: "full inventory determinism/populated-field contract passed",
        )
    }

    // Red-1: 全 25 フィールド populated (sample blueprint + 80 件全件)
    @Test
    fun `generate returns a Disease with all 25 top-level fields populated (non-null and non-empty)`() {
        val disease = generator.generate(blueprint = sampleBlueprint)

        val blueprints = DiseaseBlueprintFactory.build()
        val diseases = generator.generate(blueprints = blueprints)
        val firstViolation = (
            populatedFieldViolations(disease = disease) +
                diseases.flatMap { generated -> populatedFieldViolations(disease = generated) }
            ).firstOrNull()

        assertEquals(expected = null, actual = firstViolation)
    }

    // Red-2: Ch.I 感染症・寄生虫症 条件必須
    @Test
    fun `generate for CHAPTER_I blueprints populates infectious epidemiology riskFactors and prevention`() {
        val blueprints =
            DiseaseBlueprintFactory.build().filter { it.icd10Chapter == Icd10Chapter.CHAPTER_I }
        val firstViolation = "no CHAPTER_I blueprint present in factory".takeIf { blueprints.isEmpty() }
            ?: blueprints.firstNotNullOfOrNull { blueprint ->
                val disease = generator.generate(blueprint = blueprint)
                listOfNotNull(
                    "CHAPTER_I disease ${disease.id} must be infectious".takeIf { !disease.infectious },
                    "CHAPTER_I disease ${disease.id} must have epidemiology".takeIf { disease.epidemiology == null },
                    "CHAPTER_I disease ${disease.id} must have epidemiology.riskFactors"
                        .takeIf { disease.epidemiology?.riskFactors.isNullOrEmpty() },
                    "CHAPTER_I disease ${disease.id} must have prevention items".takeIf {
                        disease.prevention.isEmpty()
                    },
                    "CHAPTER_I disease ${disease.id} must have symptoms.onsetPattern"
                        .takeIf { disease.symptoms.onsetPattern == null },
                ).firstOrNull()
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    // Red-3: Ch.II 新生物 条件必須
    @Test
    fun `generate for CHAPTER_II blueprints populates severityGrading and prognosis`() {
        val blueprints =
            DiseaseBlueprintFactory.build().filter { it.icd10Chapter == Icd10Chapter.CHAPTER_II }
        val firstViolation = "no CHAPTER_II blueprint present in factory".takeIf { blueprints.isEmpty() }
            ?: blueprints.firstNotNullOfOrNull { blueprint ->
                val disease = generator.generate(blueprint = blueprint)
                listOfNotNull(
                    "CHAPTER_II disease ${disease.id} must have severityGrading"
                        .takeIf { disease.severityGrading == null },
                    "CHAPTER_II disease ${disease.id} prognosis must be non-blank"
                        .takeIf { disease.prognosis.isNullOrBlank() },
                ).firstOrNull()
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    // Red-4: Ch.IV 内分泌・栄養・代謝疾患 条件必須 (慢性)
    @Test
    fun `generate for CHAPTER_IV CHRONIC blueprints populates pharmacological treatments and requiredExams`() {
        val blueprints = DiseaseBlueprintFactory.build()
            .filter {
                it.icd10Chapter == Icd10Chapter.CHAPTER_IV && it.chronicity == Chronicity.CHRONIC
            }
        val firstViolation = "no CHAPTER_IV + CHRONIC blueprint present in factory".takeIf { blueprints.isEmpty() }
            ?: blueprints.firstNotNullOfOrNull { blueprint ->
                val disease = generator.generate(blueprint = blueprint)
                listOfNotNull(
                    "CHAPTER_IV+CHRONIC disease ${disease.id} must have pharmacological treatments"
                        .takeIf { disease.treatments.pharmacological.isEmpty() },
                    (
                        "CHAPTER_IV+CHRONIC disease ${disease.id} must have at least " +
                            "$MIN_CHAPTER_IV_EXAM_COUNT requiredExams, got ${disease.requiredExams.size}"
                        ).takeIf { disease.requiredExams.size < MIN_CHAPTER_IV_EXAM_COUNT },
                ).firstOrNull()
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    // Red-5: Ch.V 精神・行動の障害 条件必須
    @Test
    fun `generate for CHAPTER_V blueprints populates diagnosticCriteria and at least three mainSymptoms`() {
        val blueprints =
            DiseaseBlueprintFactory.build().filter { it.icd10Chapter == Icd10Chapter.CHAPTER_V }
        val firstViolation = "no CHAPTER_V blueprint present in factory".takeIf { blueprints.isEmpty() }
            ?: blueprints.firstNotNullOfOrNull { blueprint ->
                val disease = generator.generate(blueprint = blueprint)
                listOfNotNull(
                    "CHAPTER_V disease ${disease.id} must have diagnosticCriteria.required"
                        .takeIf { disease.diagnosticCriteria.required.isEmpty() },
                    (
                        "CHAPTER_V disease ${disease.id} must have at least " +
                            "$MIN_CHAPTER_V_MAIN_SYMPTOMS mainSymptoms, got ${disease.symptoms.mainSymptoms.size}"
                        ).takeIf { disease.symptoms.mainSymptoms.size < MIN_CHAPTER_V_MAIN_SYMPTOMS },
                    "CHAPTER_V disease ${disease.id} must have relatedDrugIds"
                        .takeIf { disease.relatedDrugIds.isEmpty() },
                ).firstOrNull()
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    // Red-6: Ch.IX 循環器系疾患 条件必須
    @Test
    fun `generate for CHAPTER_IX blueprints populates severityGrading and imaging requiredExams`() {
        val blueprints =
            DiseaseBlueprintFactory.build().filter { it.icd10Chapter == Icd10Chapter.CHAPTER_IX }
        val firstViolation = "no CHAPTER_IX blueprint present in factory".takeIf { blueprints.isEmpty() }
            ?: blueprints.firstNotNullOfOrNull { blueprint ->
                val disease = generator.generate(blueprint = blueprint)
                listOfNotNull(
                    "CHAPTER_IX disease ${disease.id} must have severityGrading"
                        .takeIf { disease.severityGrading == null },
                    "CHAPTER_IX disease ${disease.id} must include at least one IMAGING exam"
                        .takeIf { disease.requiredExams.none { exam -> exam.category == ExamCategory.IMAGING } },
                ).firstOrNull()
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    // Red-7: Ch.XV 妊娠・分娩・産褥 条件必須
    @Test
    fun `generate for CHAPTER_XV blueprints populates epidemiology onsetAgeRange and sexRatio`() {
        val blueprints =
            DiseaseBlueprintFactory.build().filter { it.icd10Chapter == Icd10Chapter.CHAPTER_XV }
        val firstViolation = "no CHAPTER_XV blueprint present in factory".takeIf { blueprints.isEmpty() }
            ?: blueprints.firstNotNullOfOrNull { blueprint ->
                val disease = generator.generate(blueprint = blueprint)
                listOfNotNull(
                    "CHAPTER_XV disease ${disease.id} must have epidemiology".takeIf { disease.epidemiology == null },
                    "CHAPTER_XV disease ${disease.id} must have epidemiology.onsetAgeRange"
                        .takeIf { disease.epidemiology?.onsetAgeRange == null },
                    "CHAPTER_XV disease ${disease.id} must have epidemiology.sexRatio"
                        .takeIf { disease.epidemiology?.sexRatio == null },
                ).firstOrNull()
            }
        assertEquals(expected = null, actual = firstViolation)
    }

    private fun MutableList<String>.addIf(message: String, predicate: () -> Boolean) {
        if (predicate()) {
            add(message)
        }
    }

    private fun populatedFieldViolations(disease: Disease): List<String> =
        buildList {
            val prefix = disease.id
            addIf("$prefix id blank") { disease.id.isBlank() }
            addIf("$prefix name blank") { disease.name.isBlank() }
            addIf("$prefix nameKana blank") { disease.nameKana.isBlank() }
            addIf("$prefix nameEnglish null or blank") { disease.nameEnglish.isNullOrBlank() }
            addIf("$prefix summary blank") { disease.summary.isBlank() }
            addIf("$prefix etiology blank") { disease.etiology.isBlank() }
            addIf("$prefix revisedAt blank") { disease.revisedAt.isBlank() }
            addIf("$prefix disclaimer blank") { disease.disclaimer.isBlank() }
            addIf("$prefix mainSymptoms empty") { disease.symptoms.mainSymptoms.isEmpty() }
            addIf("$prefix diagnosticCriteria.required empty") { disease.diagnosticCriteria.required.isEmpty() }
            addIf("$prefix prognosis null or blank") { disease.prognosis.isNullOrBlank() }
            addIf("$prefix epidemiology null") { disease.epidemiology == null }
            // severityGrading は章別必須 (Ch.II/IX のみ) のため共通 populate 検証から除外。
            // 章別の非 null 検証は同ファイルの CHAPTER_II / CHAPTER_IX 専用テストで担保されている。
            addIf("$prefix medicalDepartment empty") { disease.medicalDepartment.isEmpty() }
            addIf("$prefix synonyms empty") { disease.synonyms.isEmpty() }
            addIf("$prefix requiredExams empty") { disease.requiredExams.isEmpty() }
            addIf("$prefix differentialDiagnoses empty") { disease.differentialDiagnoses.isEmpty() }
            addIf("$prefix complications empty") { disease.complications.isEmpty() }
            // treatments.pharmacological は章別必須 (Ch.IV のみ) のため共通 populate 検証から除外。
            // 章別の非 empty 検証は同ファイルの CHAPTER_IV 専用テストで担保されている。
            addIf("$prefix prevention empty") { disease.prevention.isEmpty() }
            addIf("$prefix relatedDrugIds empty") { disease.relatedDrugIds.isEmpty() }
            // disease_0079 は作中ロア上の対応関連疾患がないため、final override で明示的に空リストへ固定する。
            addIf("$prefix relatedDiseaseIds empty") {
                disease.relatedDiseaseIds.isEmpty() && disease.id != "disease_0079"
            }
        }

    @Test
    fun `no raw placeholder delimiters survive in any generated disease JSON`() {
        val diseases = generator.generate(blueprints = DiseaseBlueprintFactory.build())
        val json = Json.encodeToString(diseases)
        val residualPlaceholders =
            DiseasePlaceholderDelimiter.REGEX.findAll(input = json).map { it.value }.toList()
        assertTrue(
            actual = residualPlaceholders.isEmpty(),
            message =
            DiseasePlaceholderContractMessages.residualDelimiterDetected(
                pattern = DiseasePlaceholderDelimiter.REGEX.pattern,
                firstOccurrences = residualPlaceholders.take(n = RESIDUAL_SAMPLE_LIMIT),
            ),
        )
    }

    companion object {
        private const val MIN_CHAPTER_IV_EXAM_COUNT: Int = 2
        private const val MIN_CHAPTER_V_MAIN_SYMPTOMS: Int = 3
        private const val RESIDUAL_SAMPLE_LIMIT: Int = 10
        private val ISO_8601_DATE_PATTERN: Regex = Regex("""^\d{4}-\d{2}-\d{2}$""")
    }

    private data class NameEnglishSnapshot(
        val nonBlank: Boolean,
        val differsFromName: Boolean,
    )
}
