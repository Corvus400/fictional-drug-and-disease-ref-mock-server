package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * drug と disease の name 系フィールドは共有 [FixmergeNameAdapter] singleton 経由で生成され、
 * `usedKatakanaSet` を通じて katakana 重複が排除されなければならない。
 *
 * PR 7 engine 欠陥調査 (plan `## PR 7 engine 欠陥調査結果`) で確定した解決策 C:
 * `FixmergeEngine.coinName` が `excludeKatakanaSet` を forward し、Adapter が used set を保持する。
 * 本テストは drug 一括生成 → disease 一括生成 の順で adapter を共有した時、
 * name 集合 ∩ name 集合 = ∅ が成立することを検証する (Red: 3件衝突, Green: 0件)。
 */
class DrugDiseaseDisjointnessTest {
    @Test
    fun `drug name set and disease name set are disjoint when FixmergeNameAdapter is shared`() {
        val adapter = FixmergeNameAdapter()
        val drugs = DrugGenerator(adapter = adapter).generate(blueprints = DrugBlueprintFactory.build())
        val diseases = DiseaseGenerator(adapter = adapter).generate(blueprints = DiseaseBlueprintFactory.build())

        val drugNames: Set<String> =
            drugs.flatMap {
                listOf(it.brandName, it.brandNameKana, it.genericName, it.manufacturer) +
                    it.composition.inactiveIngredients
            }.toSet()
        val diseaseNames: Set<String> =
            diseases.flatMap {
                listOf(it.name, it.nameKana) + it.synonyms + it.differentialDiagnoses + it.complications
            }.toSet()

        val intersection = drugNames intersect diseaseNames
        assertTrue(
            actual = intersection.isEmpty(),
            message = "drug/disease katakana intersection must be empty but was: $intersection",
        )
    }
}
