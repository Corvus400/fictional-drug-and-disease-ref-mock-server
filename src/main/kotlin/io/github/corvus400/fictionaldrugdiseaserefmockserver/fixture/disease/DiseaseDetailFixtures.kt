package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease

/**
 * `/diseases/{id}` 詳細エンドポイント向けの id ルックアップ Fixture。
 *
 * `FixtureProvider<T>` 契約は「シナリオ別の単一オブジェクト」を前提とするため、
 * path-param 指定の詳細取得 (80 件どの id も合法) とは噛み合わない。本クラスは
 * `FixtureProvider` 非準拠の専用 lookup として `findById(id)` のみを公開する。
 *
 * `respondWithScenario<T : Any>` の境界制約上 nullable (`Disease?`) をそのまま渡せない
 * ため、呼び出し側 (`DiseaseModule`) で null を 404 に変換した上で non-null `Disease`
 * を `resolveScenarioWithOverride` の fixtureProvider に渡す設計 (Issue #65 の
 * 採用方針: id pre-lookup + resolveScenarioWithOverride + respondWithScenario)。
 */
class DiseaseDetailFixtures(
    diseases: List<Disease>,
) {
    private val diseasesById: Map<String, Disease> = diseases.associateBy { it.id }

    fun findById(id: String): Disease? = diseasesById[id]
}
