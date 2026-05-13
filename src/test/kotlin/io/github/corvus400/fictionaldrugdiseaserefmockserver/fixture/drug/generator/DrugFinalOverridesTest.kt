package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DrugFinalOverridesTest {
    @Test
    fun `drug_0080 should be overridden by DRUG_FINAL_OVERRIDES`() {
        val drug0080 = generateDrugs().first { it.id == "drug_0080" }

        assertEquals("対魔女兵器 (架空分類)", drug0080.therapeuticCategoryName)
    }

    @Test
    fun `drug_0080 should reflect tredecim narrative details`() {
        val drug0080 = generateDrugs().first { it.id == "drug_0080" }

        val violations = listOfNotNull(
            "indications must be empty (no therapeutic use)".takeUnless { drug0080.indications.isEmpty() },
            "post-mortem crystallization must be in serious adverse reactions"
                .takeUnless { drug0080.adverseReactions.serious.any { reaction -> reaction.name.contains("結晶化") } },
            "pharmacology mechanism must mention 魔女因子"
                .takeUnless { drug0080.pharmacology?.mechanism.orEmpty().contains("魔女因子") },
            "overdose symptoms must mention 致死"
                .takeUnless { drug0080.overdose?.symptoms.orEmpty().contains("致死") },
            "manufacturer must be 魔女因子研究所 but was ${drug0080.manufacturer}"
                .takeUnless { drug0080.manufacturer == "魔女因子研究所" },
            "pharmacology mechanism must end with (架空)"
                .takeUnless { drug0080.pharmacology?.mechanism.orEmpty().endsWith("(架空)") },
        )

        assertTrue(
            actual = violations.isEmpty(),
            message = "drug_0080 narrative violations: $violations",
        )
    }

    @Test
    fun `non-drug_0080 should not be affected by tredecim override`() {
        val drugs = generateDrugs()
        val drug0079 = drugs.first { it.id == "drug_0079" }

        assertNotEquals(
            "対魔女兵器 (架空分類)",
            drug0079.therapeuticCategoryName,
            "drug_0079 therapeuticCategoryName must not receive the drug_0080 override",
        )
        assertNotEquals(
            "魔女因子研究所",
            drug0079.manufacturer,
            "drug_0079 manufacturer must not receive the drug_0080 override",
        )
    }

    @Test
    fun `drug_0080 should reference witch factor syndrome disease only`() {
        val drug0080 = generateDrugs().first { it.id == "drug_0080" }

        assertEquals(listOf("disease_0079"), drug0080.relatedDiseaseIds)
    }

    @Test
    fun `drug_0089 should be overridden by DRUG_FINAL_OVERRIDES`() {
        val drug0089 = generateDrugs().first { it.id == "drug_0089" }

        assertTrue(drug0089.composition.appearance.contains("青色"))
    }

    @Test
    fun `drug_0089 should reflect arisa sleep aid narrative details`() {
        val drug0089 = generateDrugs().first { it.id == "drug_0089" }

        val violations = listOfNotNull(
            "indications must mention 不眠症"
                .takeUnless { drug0089.indications.any { indication -> indication.content.contains("不眠症") } },
            "overdose symptoms must mention 昏睡"
                .takeUnless { drug0089.overdose?.symptoms.orEmpty().contains("昏睡") },
            "overdose management must mention 24"
                .takeUnless { drug0089.overdose?.management.orEmpty().contains("24") },
            "pharmacology mechanism must mention GABA"
                .takeUnless { drug0089.pharmacology?.mechanism.orEmpty().contains("GABA") },
            "appearance must mention 蝶".takeUnless { drug0089.composition.appearance.contains("蝶") },
            "manufacturer must be 不明 but was ${drug0089.manufacturer}"
                .takeUnless { drug0089.manufacturer == "不明" },
            "packages must be non-empty".takeUnless { drug0089.packages.isNotEmpty() },
            "first package size must mention mL"
                .takeUnless { drug0089.packages.firstOrNull()?.size.orEmpty().contains("mL") },
            "handling precautions must mention ラベル"
                .takeUnless { drug0089.handlingPrecautions.any { precaution -> precaution.content.contains("ラベル") } },
            "physicochemical description must mention 青色"
                .takeUnless { drug0089.physicochemicalProperties?.description?.contains("青色") == true },
            "pharmacology mechanism must end with (架空)"
                .takeUnless { drug0089.pharmacology?.mechanism.orEmpty().endsWith("(架空)") },
        )

        assertTrue(
            actual = violations.isEmpty(),
            message = "drug_0089 narrative violations: $violations",
        )
    }

    @Test
    fun `drug_0089 should reference insomnia disease`() {
        val drug0089 = generateDrugs().first { it.id == "drug_0089" }

        assertEquals(listOf("disease_0022"), drug0089.relatedDiseaseIds)
    }

    @Test
    fun `drug_0089 and insomnia should bidirectionally cross-reference`() {
        val (diseases, drugs) = generateDiseaseAndDrugFixtures()
        val drug0089 = drugs.first { it.id == "drug_0089" }
        val insomnia = diseases.first { it.id == "disease_0022" }
        val actual = mapOf(
            "drug_0089.relatedDiseaseIds contains disease_0022" to ("disease_0022" in drug0089.relatedDiseaseIds),
            "disease_0022.relatedDrugIds contains drug_0089" to ("drug_0089" in insomnia.relatedDrugIds),
        )

        assertEquals(
            expected = actual.keys.associateWith { true },
            actual = actual,
        )
    }

    private fun generateDrugs(): List<Drug> {
        val (_, drugs) = generateDiseaseAndDrugFixtures()
        return drugs
    }

    private fun generateDiseaseAndDrugFixtures(): Pair<List<Disease>, List<Drug>> {
        val adapter = FixmergeNameAdapter()
        val diseases =
            DiseaseGenerator(
                adapter = adapter,
                placeholderDictionary = DiseasePlaceholderDictionary(),
            ).generate(blueprints = DiseaseBlueprintFactory.build())
        val drugDictionary = DrugPlaceholderDictionary(nameAdapter = adapter, diseases = diseases)
        val drugs =
            DrugGenerator(adapter = adapter, placeholderDictionary = drugDictionary)
                .generate(blueprints = DrugBlueprintFactory.build())
        return diseases to drugs
    }
}
