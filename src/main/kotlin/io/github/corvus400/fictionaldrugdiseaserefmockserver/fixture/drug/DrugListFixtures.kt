package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.FixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation.DrugFixtureValidator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugSummary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.toSummary

/**
 * `/drugs` 一覧エンドポイント向け FixtureProvider。
 *
 * - `default` シナリオは DI 経由で注入された 120 件の Drug を envelope `DrugListResponse` に包む。
 * - `empty` シナリオは空配列 envelope を返し、UI の空状態回帰を検証できる。
 *
 * 起動時に `DrugFixtureValidator` で fixture の整合性を fail-fast 検証する。validator が violation を
 * 検出した場合は起動をブロックし、CI で早期に気付ける。
 *
 * `scenarios` / `scenarioTitles` は `FixtureProvider<T>` 契約のためインターフェース経由でのみ読まれる。
 * IntelliJ は具象クラスの直接参照のみ辿るため誤検知が出るので `@Suppress` で固定する
 * (`.claude/rules/kotlin-inspection.md` の FixtureProvider 例外条項)。
 * `RedundantSuppression` 自体も IntelliJ 偽陽性のため同時に抑制する。
 */
@Suppress("unused", "RedundantSuppression")
class DrugListFixtures(
    drugs: List<Drug>,
) : FixtureProvider<DrugListResponse> {
    init {
        val violations = DrugFixtureValidator.validate(drugs = drugs)
        require(value = violations.isEmpty()) {
            "DrugListFixtures violations: $violations"
        }
    }

    private val summaries: List<DrugSummary> = drugs.map { drug -> drug.toSummary() }

    /**
     * `/drugs/{id}` 詳細用の id → Drug 参照テーブル。
     *
     * Phase 9-5a / 9-6a で `DrugListFixtures` 経由の詳細解決に切り替える際に利用する
     * (Issue #54)。
     */
    val allDrugsById: Map<String, Drug> = drugs.associateBy { drug -> drug.id }

    override val scenarios: Map<String, DrugListResponse> = mapOf(
        "default" to DrugListResponse(items = summaries),
        "empty" to DrugListResponse(items = emptyList()),
    )

    override val scenarioTitles: Map<String, String> = mapOf(
        "default" to "デフォルト (120件)",
        "empty" to "空レスポンス",
    )

    override fun describeFixture(fixture: DrugListResponse): String =
        "items=${fixture.items.size}"
}
