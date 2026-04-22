package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease

/**
 * 起動時に生成された疾患 Fixture の一覧を保持し、id 検索を提供する。
 *
 * `FixtureProvider<T>` 規約は単一シナリオ対単一オブジェクトを前提とするため、
 * path-param `/diseases/{id}` ルックアップには合わない。そこで本 class は独立した
 * Provider として all リスト + getById を提供する。
 */
class DiseaseFixtureProvider(
    val all: List<Disease>,
) {
    private val byId: Map<String, Disease> = all.associateBy { it.id }

    fun getById(id: String): Disease? = byId[id]
}
