package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DISEASE_FINAL_OVERRIDES
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseNestedBuilders
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import kotlin.test.Test
import kotlin.test.assertTrue

class DrugIndicationsSemanticTest {
    @Test
    fun `generated drug indication mentions its first related disease name`() {
        val adapter = FixmergeNameAdapter()
        val diseasePlaceholderDictionary = DiseasePlaceholderDictionary()
        val diseaseBlueprints = DiseaseBlueprintFactory.build()
        val initialDiseases =
            DiseaseGenerator(adapter = adapter, placeholderDictionary = diseasePlaceholderDictionary)
                .generate(blueprints = diseaseBlueprints)
        val drugDictionary = DrugPlaceholderDictionary(nameAdapter = adapter, diseases = initialDiseases)
        val drugs =
            DrugGenerator(
                adapter = adapter,
                placeholderDictionary = drugDictionary,
                diseases = initialDiseases,
            ).generate(blueprints = DrugBlueprintFactory.build())
        val finalDiseases =
            initialDiseases.map { disease ->
                val withRelatedDrugIds =
                    disease.copy(
                        relatedDrugIds =
                        DiseaseNestedBuilders.buildRelatedDrugIds(
                            id = disease.id,
                            chapter = disease.icd10Chapter,
                            drugFixtures = drugs,
                        ),
                    )
                DISEASE_FINAL_OVERRIDES[withRelatedDrugIds.id]?.invoke(withRelatedDrugIds) ?: withRelatedDrugIds
            }
        val diseaseById = finalDiseases.associateBy { disease -> disease.id }
        val target = drugs.first { drug -> drug.id == "drug_0001" }
        val firstRelatedDiseaseName = diseaseById.getValue(target.relatedDiseaseIds.first()).name

        assertTrue(
            actual = target.indications.first().content.contains(firstRelatedDiseaseName),
            message =
            "first indication must mention first related disease name. " +
                "drug=${target.id}, relatedDiseaseIds=${target.relatedDiseaseIds}, " +
                "diseaseName=$firstRelatedDiseaseName, indication=${target.indications.first().content}",
        )
    }
}
