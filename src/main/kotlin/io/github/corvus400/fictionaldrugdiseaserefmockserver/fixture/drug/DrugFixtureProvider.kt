package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug

/**
 * 起動時に生成された医薬品 Fixture の一覧を保持し、id 検索を提供する。
 *
 * `FixtureProvider<T>` 規約は単一シナリオ対単一オブジェクトを前提とするため、
 * path-param `/drugs/{id}` ルックアップには合わない。そこで本 class は独立した
 * Provider として all リスト + getById を提供する。
 */
class DrugFixtureProvider(
    val all: List<Drug>,
) {
    fun getById(id: String): Drug? {
        TODO("not implemented: id=$id")
    }
}
