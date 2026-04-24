package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.FixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation.DrugFixtureValidator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugSummary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.toSummary
import kotlin.math.ceil

/**
 * `/drugs` 一覧エンドポイント向け FixtureProvider。
 *
 * - `default` シナリオは DI 経由で注入された 120 件の Drug を `DEFAULT_PAGE_SIZE` (=20) で
 *   ページングした envelope `DrugListResponse` を返す。
 * - `empty` シナリオは `items` が 0 件、`totalCount` / `totalPages` も 0 の envelope を返し、
 *   UI の空状態回帰を検証できる。
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

    /**
     * シナリオ別の `DrugSummary` 全件。ページング (`resolve`) の元データ。
     */
    val summariesByScenario: Map<String, List<DrugSummary>> = mapOf(
        "default" to summaries,
        "empty" to emptyList(),
    )

    /**
     * 指定シナリオを `page` / `pageSize` でスライスした `DrugListResponse` を返す。
     * `/drugs` ハンドラ (Phase 9-4a) と OpenAPI 例示 (`scenarios`) で共有される。
     *
     * `atcPrefix` を非 null で渡すと、pagination 前に `Drug.atcCode.startsWith(atcPrefix)` で
     * 絞り込む (Phase 9-7a)。`null` の場合は従来通り全件を対象とする。
     */
    fun resolve(
        scenario: String,
        page: Int,
        pageSize: Int,
        atcPrefix: String? = null,
    ): DrugListResponse {
        val list = summariesByScenario[scenario] ?: summariesByScenario.values.first()
        val filtered = applyFilters(summaries = list, atcPrefix = atcPrefix)
        val totalCount = filtered.size
        val totalPages = if (totalCount == 0) 0 else ceil(totalCount.toDouble() / pageSize.toDouble()).toInt()
        val startIndex = (page - 1) * pageSize
        val items = if (startIndex >= totalCount) emptyList() else filtered.drop(n = startIndex).take(n = pageSize)
        return DrugListResponse(
            items = items,
            page = page,
            pageSize = pageSize,
            totalPages = totalPages,
            totalCount = totalCount,
        )
    }

    /**
     * `/drugs` 一覧クエリフィルタを pagination 前に適用する。
     *
     * Phase 9-7a 時点では `atcPrefix` のみ対応。後続 Phase で `regulatoryClass` / `route` /
     * `dosageForm` 等のフィルタを追加する際はここにパラメータを足していく。
     */
    private fun applyFilters(
        summaries: List<DrugSummary>,
        atcPrefix: String?,
    ): List<DrugSummary> {
        if (atcPrefix == null) {
            return summaries
        }
        return summaries.filter { summary ->
            allDrugsById[summary.id]?.atcCode?.startsWith(prefix = atcPrefix) == true
        }
    }

    override val scenarios: Map<String, DrugListResponse> = summariesByScenario.keys.associateWith { scenario ->
        resolve(scenario = scenario, page = 1, pageSize = DEFAULT_PAGE_SIZE)
    }

    override val scenarioTitles: Map<String, String> = mapOf(
        "default" to "デフォルト (120件)",
        "empty" to "空レスポンス",
    )

    override fun describeFixture(fixture: DrugListResponse): String =
        "items=${fixture.items.size} of ${fixture.totalCount}"

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20
        const val MAX_PAGE_SIZE: Int = 100
    }
}
