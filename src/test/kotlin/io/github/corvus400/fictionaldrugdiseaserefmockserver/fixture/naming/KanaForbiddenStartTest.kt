package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import kotlin.test.Test
import kotlin.test.fail

class KanaForbiddenStartTest {
    @Test
    fun `generated drug and disease names do not start with forbidden kana`() {
        val fixtures = buildFixtures()
        val violations =
            fixtures.drugs.flatMap { drug -> drug.nameFields() } +
                fixtures.diseases.flatMap { disease -> disease.nameFields() }

        val forbiddenViolations = violations.filter { violation ->
            val value = violation.value
            value.isEmpty() || value.first() in FORBIDDEN_START_CHARS
        }
        if (forbiddenViolations.isNotEmpty()) {
            fail(
                forbiddenViolations.joinToString(
                    separator = "\n",
                    prefix = "Generated names must not start with forbidden kana " +
                        "$FORBIDDEN_START_CHARS, but found ${forbiddenViolations.size} violations:\n",
                ) { violation -> "${violation.id}\t${violation.field}:${violation.value}" },
            )
        }
    }

    private fun buildFixtures(): GeneratedFixtures {
        val adapter = FixmergeNameAdapter()
        val diseases =
            DiseaseGenerator(
                adapter = adapter,
                placeholderDictionary = DiseasePlaceholderDictionary(),
            ).generate(blueprints = DiseaseBlueprintFactory.build())
        val drugs =
            DrugGenerator(
                adapter = adapter,
                placeholderDictionary = DrugPlaceholderDictionary(
                    nameAdapter = adapter,
                    diseases = diseases,
                ),
            ).generate(blueprints = DrugBlueprintFactory.build())
        return GeneratedFixtures(drugs = drugs, diseases = diseases)
    }

    private fun Drug.nameFields(): List<NameField> =
        buildList {
            add(NameField(id = id, field = "generic", value = genericName))
            add(NameField(id = id, field = "brand", value = brandName))
            add(NameField(id = id, field = "active", value = composition.activeIngredient))
            composition.inactiveIngredients.forEachIndexed { index, ingredient ->
                add(NameField(id = id, field = "inactive[$index]", value = ingredient))
            }
            add(NameField(id = id, field = "manu", value = manufacturer.removeSuffix(suffix = "製薬")))
        }

    private fun Disease.nameFields(): List<NameField> =
        buildList {
            add(NameField(id = id, field = "name", value = name))
            synonyms.forEachIndexed { index, synonym ->
                add(NameField(id = id, field = "syn[$index]", value = synonym))
            }
            differentialDiagnoses.forEachIndexed { index, differentialDiagnosis ->
                add(NameField(id = id, field = "diff[$index]", value = differentialDiagnosis))
            }
            complications.forEachIndexed { index, complication ->
                add(NameField(id = id, field = "comp[$index]", value = complication))
            }
        }

    private data class GeneratedFixtures(
        val drugs: List<Drug>,
        val diseases: List<Disease>,
    )

    private data class NameField(
        val id: String,
        val field: String,
        val value: String,
    )

    private companion object {
        private val FORBIDDEN_START_CHARS: Set<Char> =
            setOf('ン', 'ー', 'ッ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ', 'ョ', 'ヮ', 'ヵ', 'ヶ')
    }
}
