package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
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

        assertTrue(drug0080.indications.isEmpty(), "indications must be empty (no therapeutic use)")
        assertTrue(
            drug0080.adverseReactions.serious.any { reaction -> reaction.name.contains("結晶化") },
            "post-mortem crystallization must be in serious adverse reactions",
        )
        assertTrue(drug0080.pharmacology?.mechanism.orEmpty().contains("魔女因子"))
        assertTrue(drug0080.overdose?.symptoms.orEmpty().contains("致死"))
        assertEquals("魔女因子研究所", drug0080.manufacturer)
        assertTrue(drug0080.pharmacology?.mechanism.orEmpty().endsWith("(架空)"))
    }

    @Test
    fun `non-drug_0080 should not be affected by tredecim override`() {
        val drugs = generateDrugs()
        val drug0079 = drugs.first { it.id == "drug_0079" }

        assertNotEquals("対魔女兵器 (架空分類)", drug0079.therapeuticCategoryName)
        assertNotEquals("魔女因子研究所", drug0079.manufacturer)
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

        assertTrue(drug0089.indications.any { indication -> indication.content.contains("不眠症") })
        assertTrue(drug0089.overdose?.symptoms.orEmpty().contains("昏睡"))
        assertTrue(drug0089.overdose?.management.orEmpty().contains("24"))
        assertTrue(drug0089.pharmacology?.mechanism.orEmpty().contains("GABA"))
        assertTrue(drug0089.composition.appearance.contains("蝶"))
        assertEquals("不明", drug0089.manufacturer)
        assertTrue(drug0089.packages.isNotEmpty())
        assertTrue(drug0089.packages[0].size.contains("mL"))
        assertTrue(drug0089.handlingPrecautions.any { precaution -> precaution.content.contains("ラベル") })
        assertTrue(drug0089.physicochemicalProperties?.description?.contains("青色") == true)
        assertTrue(drug0089.pharmacology?.mechanism.orEmpty().endsWith("(架空)"))
    }

    private fun generateDrugs(): List<Drug> {
        val adapter = FixmergeNameAdapter()
        val diseases =
            DiseaseGenerator(
                adapter = adapter,
                placeholderDictionary = DiseasePlaceholderDictionary(),
            ).generate(blueprints = DiseaseBlueprintFactory.build())
        val drugDictionary = DrugPlaceholderDictionary(nameAdapter = adapter, diseases = diseases)
        return DrugGenerator(adapter = adapter, placeholderDictionary = drugDictionary)
            .generate(blueprints = DrugBlueprintFactory.build())
    }
}
