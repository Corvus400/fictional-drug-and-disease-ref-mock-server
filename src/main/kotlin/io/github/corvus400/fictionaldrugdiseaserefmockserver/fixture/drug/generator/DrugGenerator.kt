package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.IsoDateFormatter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.BucketNameCoiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.bucket.BucketContextKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DrugCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.TherapeuticCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CompositionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Dose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PhysicochemicalInfo
import java.time.LocalDate

class DrugGenerator(
    adapter: FixmergeNameAdapter,
    private val placeholderDictionary: DrugPlaceholderDictionary,
    private val diseases: List<Disease> = emptyList(),
) {
    private val coiner: BucketNameCoiner = BucketNameCoiner(adapter = adapter)

    fun generate(blueprint: DrugBlueprint): Drug {
        return generate(
            blueprint = blueprint,
            injectionFormIndices = emptyList(),
        )
    }

    fun generate(blueprints: List<DrugBlueprint>): List<Drug> {
        val injectionFormIndices =
            blueprints
                .filter { blueprint -> blueprint.dosageForm == DosageForm.INJECTION_FORM }
                .map { blueprint -> blueprint.index }
                .sorted()
        return blueprints.map { blueprint ->
            generate(blueprint = blueprint, injectionFormIndices = injectionFormIndices)
        }
    }

    private fun generate(
        blueprint: DrugBlueprint,
        injectionFormIndices: List<Int>,
    ): Drug {
        val country = DrugCountryMapping.of(atcFirstLetter = blueprint.atcFirstLetter)
        val bucket = CountryBucketRepository.of(country = country)
        val brand: CoinedName =
            blueprint.nameOverride?.toBrandCoinedName()
                ?: coiner.coin(
                    bucket = bucket.cuisine,
                    blueprintIndex = blueprint.index,
                    slot = NameSlot.DRUG_BRAND,
                    offset = 0,
                )
        val generic: CoinedName =
            blueprint.nameOverride?.toGenericCoinedName()
                ?: coiner.coin(
                    bucket = bucket.cuisine,
                    blueprintIndex = blueprint.index,
                    slot = NameSlot.DRUG_GENERIC,
                    offset = 0,
                )
        val inactives = (0 until INACTIVE_INGREDIENT_COUNT).map { offset ->
            coiner.coin(
                bucket = bucket.beverage,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DRUG_INACTIVE_INGREDIENT,
                offset = offset,
            )
        }
        val manufacturer =
            coiner.coin(
                bucket = bucket.beverage,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DRUG_MANUFACTURER,
                offset = 0,
            )
        val generated =
            buildDrug(
                blueprint = blueprint,
                brand = brand,
                generic = generic,
                inactives = inactives,
                manufacturer = manufacturer,
                injectionFormIndices = injectionFormIndices,
            )
        return DRUG_FINAL_OVERRIDES[generated.id]?.invoke(generated) ?: generated
    }

    private fun buildDrug(
        blueprint: DrugBlueprint,
        brand: CoinedName,
        generic: CoinedName,
        inactives: List<CoinedName>,
        manufacturer: CoinedName,
        injectionFormIndices: List<Int>,
    ): Drug {
        val drugId: String =
            blueprint.idOverride
                ?: "drug_${blueprint.index.toString().padStart(length = DRUG_ID_PAD_LENGTH, padChar = '0')}"
        val therapeuticCategory = therapeuticCategoryOf(atcFirstLetter = blueprint.atcFirstLetter)
        val contextualDictionary =
            placeholderDictionary.withContext(
                context = BucketContextKey.DrugContext(atcInitial = blueprint.atcFirstLetter),
            )
        val relatedDiseaseIds =
            DrugMetaBuilders.buildRelatedDiseaseIds(
                id = drugId,
                therapeuticCategory = therapeuticCategory,
                diseaseFixtures = diseases,
            )
        return Drug(
            id = drugId,
            genericName = generic.katakana,
            brandName = brand.katakana,
            brandNameKana = brand.katakana,
            atcCode = buildAtcCode(blueprint = blueprint),
            yjCode = DrugMetaBuilders.buildYjCode(id = drugId),
            therapeuticCategoryName = therapeuticCategory.displayName,
            regulatoryClass = blueprint.regulatoryClasses.toList(),
            dosageForm = blueprint.dosageForm,
            routeOfAdministration = routeOf(form = blueprint.dosageForm),
            composition =
            CompositionInfo(
                activeIngredient = generic.katakana,
                activeIngredientAmount =
                Dose(
                    amount =
                    DrugSeedDerivedValues.standardDoseAmount(
                        id = drugId,
                        form = blueprint.dosageForm,
                    ),
                    unit =
                    pickDoseUnit(
                        form = blueprint.dosageForm,
                        drugId = drugId,
                        drugIndex = blueprint.index,
                        injectionFormIndices = injectionFormIndices,
                    ),
                ),
                inactiveIngredients = inactives.map { it.katakana },
                appearance =
                blueprint.textOverride?.appearance
                    ?: DosageFormAppearance.pickAppearance(
                        form = blueprint.dosageForm,
                        drugId = drugId,
                    ),
            ),
            warning = DrugClinicalBuilders.buildWarning(id = drugId, dict = contextualDictionary),
            contraindications =
            DrugClinicalBuilders.buildContraindications(id = drugId, dict = contextualDictionary),
            indications =
            DrugClinicalBuilders.buildIndications(
                id = drugId,
                dict = contextualDictionary,
                relatedDiseaseIds = relatedDiseaseIds,
                diseaseNameResolver = ::resolveDiseaseName,
            ),
            indicationsRelatedPrecautions =
            DrugClinicalBuilders.buildIndicationsRelatedPrecautions(
                id = drugId,
                dict = contextualDictionary,
            ),
            dosage =
            DrugClinicalBuilders.buildDosage(
                id = drugId,
                dict = contextualDictionary,
                dosageForm = blueprint.dosageForm,
            ),
            dosageRelatedPrecautions =
            DrugClinicalBuilders.buildDosageRelatedPrecautions(id = drugId, dict = contextualDictionary),
            importantPrecautions =
            DrugClinicalBuilders.buildImportantPrecautions(id = drugId, dict = contextualDictionary),
            precautionsForSpecificPopulations =
            DrugClinicalBuilders.buildPrecautionsForSpecificPopulations(
                id = drugId,
                dict = contextualDictionary,
            ),
            interactions =
            DrugClinicalBuilders.buildInteractions(
                id = drugId,
                dict = contextualDictionary,
                atcInitial = blueprint.atcFirstLetter,
            ),
            adverseReactions =
            DrugClinicalBuilders.buildAdverseReactions(
                id = drugId,
                dict = contextualDictionary,
                atcInitial = blueprint.atcFirstLetter,
            ),
            effectsOnLabTests =
            DrugClinicalBuilders.buildEffectsOnLabTests(id = drugId, dict = contextualDictionary),
            overdose = DrugClinicalBuilders.buildOverdose(id = drugId, dict = contextualDictionary),
            administrationPrecautions =
            DrugClinicalBuilders.buildAdministrationPrecautions(id = drugId, dict = contextualDictionary),
            otherPrecautions =
            DrugClinicalBuilders.buildOtherPrecautions(
                id = drugId,
                dict = contextualDictionary,
                blueprint = blueprint,
            ),
            pharmacokinetics =
            DrugMetaBuilders.buildPharmacokinetics(id = drugId, dict = contextualDictionary),
            clinicalResults =
            DrugMetaBuilders.buildClinicalResults(id = drugId, dict = contextualDictionary),
            pharmacology = DrugMetaBuilders.buildPharmacology(id = drugId, dict = contextualDictionary),
            physicochemicalProperties =
            PhysicochemicalInfo(
                genericNameEnglish = generic.latin,
                molecularFormula =
                DrugSeedDerivedValues.molecularFormula(
                    id = drugId,
                    atcInitial = blueprint.atcFirstLetter,
                ),
                description =
                ensureFictionalMarker(
                    blueprint.textOverride?.originalSubstanceDescription
                        ?: DosageFormAppearance.pickOriginalSubstanceDescription(
                            form = blueprint.dosageForm,
                            drugId = drugId,
                        ),
                ),
            ),
            handlingPrecautions =
            DrugMetaBuilders.buildHandlingPrecautions(id = drugId, dict = contextualDictionary),
            approvalConditions =
            DrugMetaBuilders.buildApprovalConditions(id = drugId, dict = contextualDictionary),
            packages =
            DrugMetaBuilders.buildPackages(
                id = drugId,
                dosageForm = blueprint.dosageForm,
                isBiological = blueprint.isBiological,
            ),
            references = DrugMetaBuilders.buildReferences(id = drugId),
            insuranceNotes =
            DrugMetaBuilders.buildInsuranceNotes(id = drugId, dict = contextualDictionary),
            manufacturer = manufacturer.katakana + MANUFACTURER_SUFFIX,
            revisedAt = revisedAtFor(blueprint = blueprint),
            relatedDiseaseIds = relatedDiseaseIds,
        )
    }

    private fun resolveDiseaseName(diseaseId: String): String? =
        diseases.firstOrNull { disease -> disease.id == diseaseId }?.name

    private fun pickDoseUnit(
        form: DosageForm,
        drugId: String,
        drugIndex: Int,
        injectionFormIndices: List<Int>,
    ): DoseUnit =
        if (form == DosageForm.INJECTION_FORM) {
            val sequence = injectionFormIndices.indexOf(drugIndex).takeIf { index -> index >= 0 } ?: 0
            INJECTION_FORM_DOSE_UNITS[sequence % INJECTION_FORM_DOSE_UNITS.size]
        } else {
            val seed = stableHash(id = drugId, slot = DrugFieldSlot.DOSE_UNIT_PICK.ordinal, index = 0)
            ValueRangeGenerator.pickOne(seed = seed, candidates = doseUnitCandidates(form = form))
        }

    private fun doseUnitCandidates(form: DosageForm): List<DoseUnit> =
        when (form) {
            DosageForm.TABLET -> listOf(DoseUnit.MG, DoseUnit.MICROGRAM)
            DosageForm.CAPSULE -> listOf(DoseUnit.MG, DoseUnit.MICROGRAM)
            DosageForm.POWDER -> listOf(DoseUnit.MG, DoseUnit.G)
            DosageForm.GRANULE -> listOf(DoseUnit.MG, DoseUnit.G)
            DosageForm.LIQUID -> listOf(DoseUnit.ML, DoseUnit.MG, DoseUnit.PERCENT)
            DosageForm.INJECTION_FORM -> INJECTION_FORM_DOSE_UNITS
            DosageForm.OINTMENT -> listOf(DoseUnit.G, DoseUnit.PERCENT)
            DosageForm.CREAM -> listOf(DoseUnit.G, DoseUnit.PERCENT)
            DosageForm.PATCH -> listOf(DoseUnit.MG, DoseUnit.MICROGRAM)
            DosageForm.EYE_DROPS -> listOf(DoseUnit.ML, DoseUnit.PERCENT)
            DosageForm.SUPPOSITORY -> listOf(DoseUnit.MG)
            DosageForm.INHALER -> listOf(DoseUnit.MICROGRAM, DoseUnit.MG)
            DosageForm.NASAL_SPRAY -> listOf(DoseUnit.ML, DoseUnit.MICROGRAM, DoseUnit.PERCENT)
        }

    private fun buildAtcCode(blueprint: DrugBlueprint): String {
        val suffix = (blueprint.index % ATC_CODE_SUFFIX_MOD).toString().padStart(length = 2, padChar = '0')
        return "${blueprint.atcFirstLetter}$ATC_FICTIONAL_NAMESPACE$suffix"
    }

    private fun ensureFictionalMarker(value: String): String =
        if (value.contains("(架空)")) value else "$value (架空)"

    private fun routeOf(form: DosageForm): RouteOfAdministration =
        when (form) {
            DosageForm.TABLET,
            DosageForm.CAPSULE,
            DosageForm.POWDER,
            DosageForm.GRANULE,
            DosageForm.LIQUID,
            -> RouteOfAdministration.ORAL
            DosageForm.INJECTION_FORM -> RouteOfAdministration.INJECTION_ROUTE
            DosageForm.OINTMENT,
            DosageForm.CREAM,
            -> RouteOfAdministration.TOPICAL
            DosageForm.PATCH -> RouteOfAdministration.TRANSDERMAL
            DosageForm.EYE_DROPS -> RouteOfAdministration.OPHTHALMIC
            DosageForm.SUPPOSITORY -> RouteOfAdministration.RECTAL
            DosageForm.INHALER -> RouteOfAdministration.INHALATION
            DosageForm.NASAL_SPRAY -> RouteOfAdministration.NASAL
        }

    private fun therapeuticCategoryOf(atcFirstLetter: Char): TherapeuticCategory =
        TherapeuticCategory.fromAtcInitial(initial = atcFirstLetter)
            ?: error("unsupported ATC first letter '$atcFirstLetter'")

    companion object {
        private const val INACTIVE_INGREDIENT_COUNT: Int = 3
        private const val DRUG_ID_PAD_LENGTH: Int = 4
        private const val ATC_FICTIONAL_NAMESPACE: String = "99ZZ"
        private const val ATC_CODE_SUFFIX_MOD: Int = 100
        private const val MANUFACTURER_SUFFIX: String = "製薬"
        private val REVISED_AT_BASE: LocalDate = LocalDate.of(2026, 4, 23)
        internal const val REVISED_AT_SPREAD_DAYS: Int = 90
        private val INJECTION_FORM_DOSE_UNITS: List<DoseUnit> =
            listOf(
                DoseUnit.IU,
                DoseUnit.MEQ,
                DoseUnit.MMOL,
                DoseUnit.MOL,
                DoseUnit.L,
                DoseUnit.ML,
                DoseUnit.MG,
                DoseUnit.PERCENT,
            )

        private fun revisedAtFor(blueprint: DrugBlueprint): String =
            IsoDateFormatter.formatDate(
                date = REVISED_AT_BASE.minusDays((blueprint.index % REVISED_AT_SPREAD_DAYS).toLong()),
            )
    }
}
