package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.IsoDateFormatter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common.ValueRangeGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.BucketNameCoiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DrugCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CompositionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Dose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PhysicochemicalInfo
import java.time.LocalDate

class DrugGenerator(
    adapter: FixmergeNameAdapter,
    private val placeholderDictionary: DrugPlaceholderDictionary,
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
        return buildDrug(
            blueprint = blueprint,
            brand = brand,
            generic = generic,
            inactives = inactives,
            manufacturer = manufacturer,
            injectionFormIndices = injectionFormIndices,
        )
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
        return Drug(
            id = drugId,
            genericName = generic.katakana,
            brandName = brand.katakana,
            brandNameKana = brand.katakana,
            atcCode = buildAtcCode(blueprint = blueprint),
            yjCode = DrugMetaBuilders.buildYjCode(id = drugId),
            therapeuticCategoryName = therapeuticCategoryNameOf(atcFirstLetter = blueprint.atcFirstLetter),
            regulatoryClass = blueprint.regulatoryClasses.toList(),
            dosageForm = blueprint.dosageForm,
            routeOfAdministration = routeOf(form = blueprint.dosageForm),
            composition =
            CompositionInfo(
                activeIngredient = generic.katakana,
                activeIngredientAmount =
                Dose(
                    amount = STANDARD_DOSE_AMOUNT,
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
            warning = DrugClinicalBuilders.buildWarning(id = drugId, dict = placeholderDictionary),
            contraindications =
            DrugClinicalBuilders.buildContraindications(id = drugId, dict = placeholderDictionary),
            indications = DrugClinicalBuilders.buildIndications(id = drugId, dict = placeholderDictionary),
            indicationsRelatedPrecautions =
            DrugClinicalBuilders.buildIndicationsRelatedPrecautions(
                id = drugId,
                dict = placeholderDictionary,
            ),
            dosage = DrugClinicalBuilders.buildDosage(id = drugId, dict = placeholderDictionary),
            dosageRelatedPrecautions =
            DrugClinicalBuilders.buildDosageRelatedPrecautions(id = drugId, dict = placeholderDictionary),
            importantPrecautions =
            DrugClinicalBuilders.buildImportantPrecautions(id = drugId, dict = placeholderDictionary),
            precautionsForSpecificPopulations =
            DrugClinicalBuilders.buildPrecautionsForSpecificPopulations(
                id = drugId,
                dict = placeholderDictionary,
            ),
            interactions =
            DrugClinicalBuilders.buildInteractions(id = drugId, dict = placeholderDictionary),
            adverseReactions =
            DrugClinicalBuilders.buildAdverseReactions(id = drugId, dict = placeholderDictionary),
            effectsOnLabTests =
            DrugClinicalBuilders.buildEffectsOnLabTests(id = drugId, dict = placeholderDictionary),
            overdose = DrugClinicalBuilders.buildOverdose(id = drugId, dict = placeholderDictionary),
            administrationPrecautions =
            DrugClinicalBuilders.buildAdministrationPrecautions(id = drugId, dict = placeholderDictionary),
            otherPrecautions =
            DrugClinicalBuilders.buildOtherPrecautions(
                id = drugId,
                dict = placeholderDictionary,
                blueprint = blueprint,
            ),
            pharmacokinetics =
            DrugMetaBuilders.buildPharmacokinetics(id = drugId, dict = placeholderDictionary),
            clinicalResults =
            DrugMetaBuilders.buildClinicalResults(id = drugId, dict = placeholderDictionary),
            pharmacology = DrugMetaBuilders.buildPharmacology(id = drugId, dict = placeholderDictionary),
            physicochemicalProperties =
            PhysicochemicalInfo(
                genericNameEnglish = generic.latin,
                molecularFormula = DEFAULT_MOLECULAR_FORMULA,
                description =
                blueprint.textOverride?.originalSubstanceDescription
                    ?: DosageFormAppearance.pickOriginalSubstanceDescription(
                        form = blueprint.dosageForm,
                        drugId = drugId,
                    ),
            ),
            handlingPrecautions =
            DrugMetaBuilders.buildHandlingPrecautions(id = drugId, dict = placeholderDictionary),
            approvalConditions =
            DrugMetaBuilders.buildApprovalConditions(id = drugId, dict = placeholderDictionary),
            packages =
            DrugMetaBuilders.buildPackages(
                id = drugId,
                dosageForm = blueprint.dosageForm,
                isBiological = blueprint.isBiological,
            ),
            references = DrugMetaBuilders.buildReferences(id = drugId),
            insuranceNotes =
            DrugMetaBuilders.buildInsuranceNotes(id = drugId, dict = placeholderDictionary),
            manufacturer = manufacturer.katakana + MANUFACTURER_SUFFIX,
            revisedAt = DEFAULT_REVISED_AT,
            relatedDiseaseIds = DrugMetaBuilders.buildRelatedDiseaseIds(id = drugId),
        )
    }

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
        return "${blueprint.atcFirstLetter}01AA$suffix"
    }

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

    private fun therapeuticCategoryNameOf(atcFirstLetter: Char): String =
        when (atcFirstLetter) {
            'A' -> "消化器系および代謝"
            'B' -> "血液および造血器"
            'C' -> "循環器系"
            'D' -> "皮膚科用"
            'G' -> "泌尿生殖器系およびホルモン製剤"
            'H' -> "全身性ホルモン製剤"
            'J' -> "感染症治療薬"
            'L' -> "抗腫瘍剤および免疫調節剤"
            'M' -> "筋骨格系"
            'N' -> "神経系"
            'P' -> "抗寄生虫剤"
            'R' -> "呼吸器系"
            'S' -> "感覚器"
            'V' -> "その他"
            else -> error("unsupported ATC first letter '$atcFirstLetter'")
        }

    companion object {
        private const val INACTIVE_INGREDIENT_COUNT: Int = 3
        private const val DRUG_ID_PAD_LENGTH: Int = 4
        private const val ATC_CODE_SUFFIX_MOD: Int = 100
        private const val STANDARD_DOSE_AMOUNT: Double = 10.0
        private const val MANUFACTURER_SUFFIX: String = "製薬"
        private const val DEFAULT_MOLECULAR_FORMULA: String = "C20H25N3O"
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
        private val DEFAULT_REVISED_AT: String =
            IsoDateFormatter.formatDate(date = LocalDate.of(2026, 4, 23))
    }
}
