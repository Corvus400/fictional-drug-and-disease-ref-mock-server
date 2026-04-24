package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug

/**
 * `/drugs/{id}` 詳細エンドポイント向け id → Drug ルックアップ Provider (Phase 9-5a / Issue #56)。
 *
 * `FixtureProvider<T>` 非準拠。path-param の 1:1 ルックアップのみを扱い、scenario 別分岐を
 * 持たないため。scenario override (delayMs / statusCode) は呼出し側 handler で
 * `resolveScenarioWithOverride` に委ね、本 class は id → Drug? を単純に引くだけに留める。
 */
class DrugDetailFixtures(
    drugs: List<Drug>,
) {
    private val drugsById: Map<String, Drug> = drugs.associateBy { drug -> drug.id }

    fun findById(id: String): Drug? = drugsById[id]
}
