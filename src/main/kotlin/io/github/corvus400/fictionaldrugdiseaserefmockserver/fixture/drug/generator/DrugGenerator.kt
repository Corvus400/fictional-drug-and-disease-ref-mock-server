package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DosageFormGroup
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.BucketNameCoiner
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DrugCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.coinage.CoinedName
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DoseUnit
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.CompositionInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.Dose
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.nested.PhysicochemicalInfo

class DrugGenerator(
    adapter: FixmergeNameAdapter,
) {
    private val coiner: BucketNameCoiner = BucketNameCoiner(adapter = adapter)

    fun generate(blueprint: DrugBlueprint): Drug {
        val country = DrugCountryMapping.of(atcFirstLetter = blueprint.atcFirstLetter)
        val bucket = CountryBucketRepository.of(country = country)
        val brand =
            coiner.coin(
                bucket = bucket.cuisine,
                blueprintIndex = blueprint.index,
                slot = NameSlot.DRUG_BRAND,
                offset = 0,
            )
        val generic =
            coiner.coin(
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
        )
    }

    fun generate(blueprints: List<DrugBlueprint>): List<Drug> {
        return blueprints.map { generate(blueprint = it) }
    }

    private fun buildDrug(
        blueprint: DrugBlueprint,
        brand: CoinedName,
        generic: CoinedName,
        inactives: List<CoinedName>,
        manufacturer: CoinedName,
    ): Drug {
        val drugId =
            "drug_${blueprint.index.toString().padStart(length = DRUG_ID_PAD_LENGTH, padChar = '0')}"
        return Drug(
            id = drugId,
            genericName = generic.katakana,
            brandName = brand.katakana,
            brandNameKana = brand.katakana,
            atcCode = buildAtcCode(blueprint = blueprint),
            yjCode = DrugMetaBuilders.buildYjCode(id = drugId),
            therapeuticCategoryName = therapeuticCategoryNameOf(atcFirstLetter = blueprint.atcFirstLetter),
            regulatoryClass = blueprint.regulatoryClasses.toList(),
            dosageForm = dosageFormOf(group = blueprint.dosageFormGroup),
            routeOfAdministration = routeOf(group = blueprint.dosageFormGroup),
            composition =
            CompositionInfo(
                activeIngredient = generic.katakana,
                activeIngredientAmount = Dose(amount = STANDARD_DOSE_AMOUNT, unit = DoseUnit.MG),
                inactiveIngredients = inactives.map { it.katakana },
                appearance = APPEARANCE_DESCRIPTION,
            ),
            warning = DrugClinicalBuilders.buildWarning(id = drugId),
            contraindications = DrugClinicalBuilders.buildContraindications(id = drugId),
            indications = DrugClinicalBuilders.buildIndications(id = drugId),
            indicationsRelatedPrecautions =
            DrugClinicalBuilders.buildIndicationsRelatedPrecautions(id = drugId),
            dosage = DrugClinicalBuilders.buildDosage(id = drugId),
            dosageRelatedPrecautions = DrugClinicalBuilders.buildDosageRelatedPrecautions(id = drugId),
            importantPrecautions = DrugClinicalBuilders.buildImportantPrecautions(id = drugId),
            precautionsForSpecificPopulations =
            DrugClinicalBuilders.buildPrecautionsForSpecificPopulations(id = drugId),
            interactions = DrugClinicalBuilders.buildInteractions(id = drugId),
            adverseReactions = DrugClinicalBuilders.buildAdverseReactions(id = drugId),
            effectsOnLabTests = DrugClinicalBuilders.buildEffectsOnLabTests(id = drugId),
            overdose = DrugClinicalBuilders.buildOverdose(id = drugId),
            administrationPrecautions = DrugClinicalBuilders.buildAdministrationPrecautions(id = drugId),
            otherPrecautions =
            DrugClinicalBuilders.buildOtherPrecautions(id = drugId, blueprint = blueprint),
            pharmacokinetics = DrugMetaBuilders.buildPharmacokinetics(id = drugId),
            clinicalResults = DrugMetaBuilders.buildClinicalResults(id = drugId),
            pharmacology = DrugMetaBuilders.buildPharmacology(id = drugId),
            physicochemicalProperties =
            PhysicochemicalInfo(
                genericNameEnglish = generic.latin,
                molecularFormula = DEFAULT_MOLECULAR_FORMULA,
                description = MOLECULAR_DESCRIPTION,
            ),
            handlingPrecautions = DrugMetaBuilders.buildHandlingPrecautions(id = drugId),
            approvalConditions = DrugMetaBuilders.buildApprovalConditions(id = drugId),
            packages = DrugMetaBuilders.buildPackages(id = drugId),
            references = DrugMetaBuilders.buildReferences(id = drugId),
            insuranceNotes = DrugMetaBuilders.buildInsuranceNotes(id = drugId),
            manufacturer = manufacturer.katakana + MANUFACTURER_SUFFIX,
            revisedAt = DEFAULT_REVISED_AT,
            relatedDiseaseIds = DrugMetaBuilders.buildRelatedDiseaseIds(id = drugId),
        )
    }

    private fun buildAtcCode(blueprint: DrugBlueprint): String {
        val suffix = (blueprint.index % ATC_CODE_SUFFIX_MOD).toString().padStart(length = 2, padChar = '0')
        return "${blueprint.atcFirstLetter}01AA$suffix"
    }

    private fun dosageFormOf(group: DosageFormGroup): DosageForm =
        when (group) {
            DosageFormGroup.ORAL -> DosageForm.TABLET
            DosageFormGroup.EXTERNAL -> DosageForm.OINTMENT
            DosageFormGroup.INJECTION -> DosageForm.INJECTION_FORM
            DosageFormGroup.INHALATION -> DosageForm.INHALER
            DosageFormGroup.OPHTHALMIC -> DosageForm.EYE_DROPS
        }

    private fun routeOf(group: DosageFormGroup): RouteOfAdministration =
        when (group) {
            DosageFormGroup.ORAL -> RouteOfAdministration.ORAL
            DosageFormGroup.EXTERNAL -> RouteOfAdministration.TOPICAL
            DosageFormGroup.INJECTION -> RouteOfAdministration.INJECTION_ROUTE
            DosageFormGroup.INHALATION -> RouteOfAdministration.INHALATION
            DosageFormGroup.OPHTHALMIC -> RouteOfAdministration.OPHTHALMIC
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
        private const val APPEARANCE_DESCRIPTION: String = "白色の錠剤"
        private const val DEFAULT_MOLECULAR_FORMULA: String = "C20H25N3O"
        private const val MOLECULAR_DESCRIPTION: String = "白色の結晶性粉末である。"
        private const val DEFAULT_REVISED_AT: String = "2026/04/23"
    }
}
