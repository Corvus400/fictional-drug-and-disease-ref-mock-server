package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.FixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation.DiseaseFixtureValidator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.DiseaseListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.DiseaseSummary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.toSummary
import kotlin.math.ceil

/**
 * `/diseases` 一覧エンドポイント向け FixtureProvider。
 *
 * - `default` シナリオは DI 経由で注入された 80 件の Disease を `DEFAULT_PAGE_SIZE` (=20) で
 *   ページングした envelope `DiseaseListResponse` を返す。
 * - `empty` シナリオは `items` が 0 件、`totalCount` / `totalPages` も 0 の envelope を返し、
 *   UI の空状態回帰を検証できる。
 *
 * 起動時に `DiseaseFixtureValidator` で fixture の整合性を fail-fast 検証する。validator が violation を
 * 検出した場合は起動をブロックし、CI で早期に気付ける。
 *
 * `scenarios` / `scenarioTitles` は `FixtureProvider<T>` 契約のためインターフェース経由でのみ読まれる。
 * IntelliJ は具象クラスの直接参照のみ辿るため誤検知が出るので `@Suppress` で固定する
 * (`.claude/rules/kotlin-inspection.md` の FixtureProvider 例外条項)。
 * `RedundantSuppression` 自体も IntelliJ 偽陽性のため同時に抑制する。
 */
@Suppress("unused", "RedundantSuppression")
class DiseaseListFixtures(
    diseases: List<Disease>,
) : FixtureProvider<DiseaseListResponse> {
    init {
        val violations = DiseaseFixtureValidator.validate(diseases = diseases)
        require(value = violations.isEmpty()) {
            "DiseaseListFixtures violations: $violations"
        }
    }

    private val defaultSummaries: List<DiseaseSummary> = diseases.map { it.toSummary() }

    /**
     * シナリオ別の `DiseaseSummary` 全件。ページング (`resolve`) の元データ。
     */
    val summariesByScenario: Map<String, List<DiseaseSummary>> = mapOf(
        "default" to defaultSummaries,
        "empty" to emptyList(),
    )

    /**
     * シナリオ別の `Disease` 全件。`/diseases` ハンドラで `DiseaseSearchService.applyKeyword`
     * を呼び出す際の元データ (Phase 11-10b)。`summariesByScenario` と同一のシナリオ集合だが、
     * `DiseaseSummary` は keyword 検索対象 (`nameKana` / `nameEnglish` / `synonyms`) を持たない
     * ため、検索段階では Disease 全体が必要。
     */
    val diseasesByScenario: Map<String, List<Disease>> = mapOf(
        "default" to diseases,
        "empty" to emptyList(),
    )

    /**
     * 指定シナリオを `page` / `pageSize` でスライスした `DiseaseListResponse` を返す。
     * `/diseases` ハンドラ (Phase 9-4b) と OpenAPI 例示 (`scenarios`) で共有される。
     */
    fun resolve(scenario: String, page: Int, pageSize: Int): DiseaseListResponse {
        val list = summariesByScenario[scenario] ?: summariesByScenario.values.first()
        return resolve(summaries = list, page = page, pageSize = pageSize)
    }

    /**
     * フィルタ・ソート済み summary 列を `page` / `pageSize` でスライスした `DiseaseListResponse` を返す。
     */
    fun resolve(summaries: List<DiseaseSummary>, page: Int, pageSize: Int): DiseaseListResponse {
        val totalCount = summaries.size
        val totalPages = if (totalCount == 0) 0 else ceil(totalCount.toDouble() / pageSize.toDouble()).toInt()
        val startIndex = (page - 1) * pageSize
        val items = if (startIndex >= totalCount) emptyList() else summaries.drop(n = startIndex).take(n = pageSize)
        return DiseaseListResponse(
            items = items,
            page = page,
            pageSize = pageSize,
            totalPages = totalPages,
            totalCount = totalCount,
        )
    }

    override val scenarios: Map<String, DiseaseListResponse> = summariesByScenario.keys.associateWith { scenario ->
        resolve(scenario = scenario, page = 1, pageSize = DEFAULT_PAGE_SIZE)
    }

    override val scenarioTitles: Map<String, String> = mapOf(
        "default" to "デフォルト (80件)",
        "empty" to "空レスポンス",
    )

    override fun describeFixture(fixture: DiseaseListResponse): String =
        "items=${fixture.items.size} of ${fixture.totalCount}"

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20
        const val MAX_PAGE_SIZE: Int = 100
    }
}
