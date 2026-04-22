package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DosageFormGroup
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.CountryBucketRepository
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.country.DrugCountryMapping
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DrugGeneratorTest {
    private val adapter: FixmergeNameAdapter = FixmergeNameAdapter()
    private val generator: DrugGenerator = DrugGenerator(adapter = adapter)

    private val sampleBlueprint: DrugBlueprint =
        DrugBlueprint(
            index = 0,
            atcFirstLetter = 'A',
            dosageFormGroup = DosageFormGroup.ORAL,
            regulatoryClasses = setOf(RegulatoryClass.ORDINARY),
            isBiological = false,
            isChronicPrescription = true,
        )

    @Test
    fun `generate returns a Drug with non-blank required name fields`() {
        val drug = generator.generate(blueprint = sampleBlueprint)
        assertTrue(drug.id.isNotBlank())
        assertTrue(drug.brandName.isNotBlank())
        assertTrue(drug.brandNameKana.isNotBlank())
        assertTrue(drug.genericName.isNotBlank())
        assertTrue(drug.atcCode.isNotBlank())
        assertTrue(drug.therapeuticCategoryName.isNotBlank())
        assertTrue(drug.manufacturer.isNotBlank())
        assertTrue(drug.composition.activeIngredient.isNotBlank())
        assertTrue(drug.composition.inactiveIngredients.isNotEmpty())
        val physicochemical = assertNotNull(drug.physicochemicalProperties)
        assertTrue(physicochemical.genericNameEnglish.isNotBlank())
    }

    @Test
    fun `generate is deterministic for the same blueprint given fresh adapter instances`() {
        val first = DrugGenerator(adapter = FixmergeNameAdapter()).generate(blueprint = sampleBlueprint)
        val second = DrugGenerator(adapter = FixmergeNameAdapter()).generate(blueprint = sampleBlueprint)
        assertEquals(first, second)
    }

    @Test
    fun `genericName equals composition activeIngredient`() {
        val drug = generator.generate(blueprint = sampleBlueprint)
        assertEquals(drug.genericName, drug.composition.activeIngredient)
    }

    @Test
    fun `manufacturer ends with 製薬 and contains no beverage raw token`() {
        val drug = generator.generate(blueprint = sampleBlueprint)
        assertTrue(
            drug.manufacturer.endsWith(suffix = "製薬"),
            "manufacturer does not end with 製薬: '${drug.manufacturer}'",
        )
        val country = DrugCountryMapping.of(atcFirstLetter = sampleBlueprint.atcFirstLetter)
        val beverageBucket = CountryBucketRepository.of(country = country).beverage
        for (raw in beverageBucket) {
            assertFalse(
                drug.manufacturer.contains(other = raw),
                "manufacturer '${drug.manufacturer}' leaks beverage raw token '$raw'",
            )
        }
    }

    @Test
    fun `physicochemicalProperties genericNameEnglish pairs with genericName via same coined name`() {
        val drug = generator.generate(blueprint = sampleBlueprint)
        val physicochemical = assertNotNull(drug.physicochemicalProperties)
        assertTrue(
            physicochemical.genericNameEnglish.isNotBlank(),
            "genericNameEnglish is blank",
        )
        assertFalse(
            physicochemical.genericNameEnglish == drug.genericName,
            "genericNameEnglish should not equal genericName (one is latin, the other katakana)",
        )
    }

    @Test
    fun `generate bulk returns one drug per blueprint`() {
        val blueprints = listOf(
            sampleBlueprint,
            sampleBlueprint.copy(index = 1, atcFirstLetter = 'C'),
            sampleBlueprint.copy(index = 2, atcFirstLetter = 'N'),
        )
        val drugs = generator.generate(blueprints = blueprints)
        assertEquals(3, drugs.size)
        for ((i, drug) in drugs.withIndex()) {
            assertEquals("drug_${i.toString().padStart(4, '0')}", drug.id)
        }
    }

    @Test
    fun `generate bulk handles the full 120-drug factory inventory deterministically given fresh adapter instances`() {
        val blueprints = DrugBlueprintFactory.build()
        val first = DrugGenerator(adapter = FixmergeNameAdapter()).generate(blueprints = blueprints)
        val second = DrugGenerator(adapter = FixmergeNameAdapter()).generate(blueprints = blueprints)
        assertEquals(blueprints.size, first.size)
        assertEquals(first, second)
        assertEquals(first.size, first.map { it.id }.toSet().size, "drug ids are not unique")
        for (drug in first) {
            assertTrue(drug.brandName.isNotBlank(), "brandName blank for ${drug.id}")
            assertTrue(drug.genericName.isNotBlank(), "genericName blank for ${drug.id}")
            assertTrue(drug.manufacturer.isNotBlank(), "manufacturer blank for ${drug.id}")
        }
    }

    @Test
    fun `generate returns a Drug with all 37 top-level fields populated (non-null and non-empty)`() {
        val drug = generator.generate(blueprint = sampleBlueprint)

        // 9 文字列フィールド: 非 blank
        assertTrue(drug.id.isNotBlank(), "id blank")
        assertTrue(drug.genericName.isNotBlank(), "genericName blank")
        assertTrue(drug.brandName.isNotBlank(), "brandName blank")
        assertTrue(drug.brandNameKana.isNotBlank(), "brandNameKana blank")
        assertTrue(drug.atcCode.isNotBlank(), "atcCode blank")
        val yjCode = assertNotNull(drug.yjCode, "yjCode null")
        assertTrue(yjCode.isNotBlank(), "yjCode blank")
        assertTrue(drug.therapeuticCategoryName.isNotBlank(), "therapeuticCategoryName blank")
        assertTrue(drug.manufacturer.isNotBlank(), "manufacturer blank")
        assertTrue(drug.revisedAt.isNotBlank(), "revisedAt blank")

        // 10 非 null 構造フィールド
        assertNotNull(drug.dosageForm, "dosageForm null")
        assertNotNull(drug.routeOfAdministration, "routeOfAdministration null")
        assertNotNull(drug.composition, "composition null")
        assertNotNull(drug.dosage, "dosage null")
        assertNotNull(drug.adverseReactions, "adverseReactions null")
        assertNotNull(drug.interactions, "interactions null")
        assertNotNull(drug.overdose, "overdose null")
        assertNotNull(drug.pharmacokinetics, "pharmacokinetics null")
        assertNotNull(drug.pharmacology, "pharmacology null")
        assertNotNull(drug.physicochemicalProperties, "physicochemicalProperties null")

        // 18 List フィールド: 非 empty
        assertTrue(drug.regulatoryClass.isNotEmpty(), "regulatoryClass empty")
        assertTrue(drug.warning.isNotEmpty(), "warning empty")
        assertTrue(drug.contraindications.isNotEmpty(), "contraindications empty")
        assertTrue(drug.indications.isNotEmpty(), "indications empty")
        assertTrue(drug.indicationsRelatedPrecautions.isNotEmpty(), "indicationsRelatedPrecautions empty")
        assertTrue(drug.dosageRelatedPrecautions.isNotEmpty(), "dosageRelatedPrecautions empty")
        assertTrue(drug.importantPrecautions.isNotEmpty(), "importantPrecautions empty")
        assertTrue(
            drug.precautionsForSpecificPopulations.isNotEmpty(),
            "precautionsForSpecificPopulations empty",
        )
        assertTrue(drug.effectsOnLabTests.isNotEmpty(), "effectsOnLabTests empty")
        assertTrue(drug.administrationPrecautions.isNotEmpty(), "administrationPrecautions empty")
        assertTrue(drug.otherPrecautions.isNotEmpty(), "otherPrecautions empty")
        assertTrue(drug.clinicalResults.isNotEmpty(), "clinicalResults empty")
        assertTrue(drug.handlingPrecautions.isNotEmpty(), "handlingPrecautions empty")
        assertTrue(drug.approvalConditions.isNotEmpty(), "approvalConditions empty")
        assertTrue(drug.packages.isNotEmpty(), "packages empty")
        assertTrue(drug.references.isNotEmpty(), "references empty")
        assertTrue(drug.insuranceNotes.isNotEmpty(), "insuranceNotes empty")
        assertTrue(drug.relatedDiseaseIds.isNotEmpty(), "relatedDiseaseIds empty")
    }

    @Test
    fun `generate for injection blueprint populates pharmacokinetics and administrationPrecautions`() {
        val injectionBlueprint =
            DrugBlueprintFactory.build().first { it.dosageFormGroup == DosageFormGroup.INJECTION }
        val drug = generator.generate(blueprint = injectionBlueprint)

        assertEquals(RouteOfAdministration.INJECTION_ROUTE, drug.routeOfAdministration)
        assertNotNull(drug.pharmacokinetics, "pharmacokinetics must be non-null for injection")
        assertTrue(
            drug.administrationPrecautions.isNotEmpty(),
            "administrationPrecautions must be non-empty for injection",
        )
    }

    @Test
    fun `generate for external blueprint populates administrationPrecautions`() {
        val externalBlueprint =
            DrugBlueprintFactory.build().first { it.dosageFormGroup == DosageFormGroup.EXTERNAL }
        val drug = generator.generate(blueprint = externalBlueprint)

        assertTrue(
            drug.dosageForm in
                setOf(
                    DosageForm.OINTMENT,
                    DosageForm.CREAM,
                    DosageForm.PATCH,
                    DosageForm.EYE_DROPS,
                    DosageForm.NASAL_SPRAY,
                ),
            "dosageForm '${drug.dosageForm}' is not an external form",
        )
        assertTrue(
            drug.administrationPrecautions.isNotEmpty(),
            "administrationPrecautions must be non-empty for external",
        )
    }

    @Test
    fun `generate for biological L-group blueprint populates handlingPrecautions and warning`() {
        val biologicalBlueprint =
            DrugBlueprintFactory.build().first { it.atcFirstLetter == 'L' && it.isBiological }
        val drug = generator.generate(blueprint = biologicalBlueprint)

        assertTrue(
            drug.handlingPrecautions.isNotEmpty(),
            "handlingPrecautions must be non-empty for biological",
        )
        assertTrue(drug.warning.isNotEmpty(), "warning must be non-empty for biological")
    }

    @Test
    fun `generate for poison or potent blueprint populates warning`() {
        val blueprint =
            DrugBlueprintFactory.build().first { bp ->
                RegulatoryClass.POISON in bp.regulatoryClasses ||
                    RegulatoryClass.POTENT in bp.regulatoryClasses
            }
        val drug = generator.generate(blueprint = blueprint)

        assertTrue(drug.warning.isNotEmpty(), "warning must be non-empty for poison or potent")
    }

    @Test
    fun `generate for psychotropic or narcotic blueprint populates insuranceNotes`() {
        val blueprint =
            DrugBlueprintFactory.build().first { bp ->
                RegulatoryClass.NARCOTIC in bp.regulatoryClasses ||
                    RegulatoryClass.PSYCHOTROPIC_1 in bp.regulatoryClasses ||
                    RegulatoryClass.PSYCHOTROPIC_2 in bp.regulatoryClasses ||
                    RegulatoryClass.PSYCHOTROPIC_3 in bp.regulatoryClasses
            }
        val drug = generator.generate(blueprint = blueprint)

        assertTrue(
            drug.insuranceNotes.isNotEmpty(),
            "insuranceNotes must be non-empty for psychotropic or narcotic",
        )
    }

    @Test
    fun `generate for chronic A or C blueprint populates dosageRelatedPrecautions`() {
        val blueprint =
            DrugBlueprintFactory.build().first { bp ->
                bp.atcFirstLetter in setOf('A', 'C') && bp.isChronicPrescription
            }
        val drug = generator.generate(blueprint = blueprint)

        assertTrue(
            drug.dosageRelatedPrecautions.isNotEmpty(),
            "dosageRelatedPrecautions must be non-empty for chronic A or C",
        )
    }
}
