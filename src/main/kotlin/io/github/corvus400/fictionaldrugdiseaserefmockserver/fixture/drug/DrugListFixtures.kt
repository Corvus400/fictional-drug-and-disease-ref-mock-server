package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.FixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation.DrugFixtureValidator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.DrugSummary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.toSummary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DrugSearchService
import kotlinx.serialization.serializer
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
     * 絞り込む (Phase 9-7a)。`regulatoryClassSerialName` を非 null で渡すと、
     * `Drug.regulatoryClass` リストが指定された `@SerialName` 値
     * (例: `RegulatoryClass.PRESCRIPTION_REQUIRED.serialName` = `prescription_required`) を
     * 含むものに絞り込む (Phase 9-8a)。`routeOfAdministrationSerialName` を非 null で渡すと、
     * `Drug.routeOfAdministration` の `@SerialName` 値が指定値
     * (例: `RouteOfAdministration.ORAL.serialName` = `oral`) に一致するものに絞り込む
     * (Phase 9-9a)。`dosageFormSerialName` を非 null で渡すと、
     * `Drug.dosageForm` の `@SerialName` 値が指定値
     * (例: `DosageForm.TABLET.serialName` = `tablet`) に一致するものに絞り込む
     * (Phase 9-10a)。`DrugListQuery.categoryName` を非 null で渡すと、
     * `DrugSummary.therapeuticCategoryName` が指定値 (例: `消化器系および代謝`) に完全一致する
     * ものに絞り込む (Phase 10-1b)。`DrugListQuery.keyword` を非 null かつ非空白で渡すと、
     * `keywordMatch` (PARTIAL/PREFIX) と `keywordTarget` (GENERIC/BRAND/BOTH) に従って
     * `DrugSearchService.applyKeyword` で絞り込む (Phase 11-10a)。複数指定時は AND 結合。
     * いずれも `null` の場合は従来通り全件を対象とする。
     */
    fun resolve(
        scenario: String,
        page: Int,
        pageSize: Int,
        query: DrugListQuery = DrugListQuery(),
    ): DrugListResponse {
        val list = summariesByScenario[scenario] ?: summariesByScenario.values.first()
        val filtered = applyFilters(summaries = list, query = query)
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
     * 複数フィルタが指定された場合は AND 結合 (filter chain) で順次絞り込む。
     * 後続 Phase で更にフィルタを追加する際はここにパラメータを足していく。
     */
    private fun applyFilters(
        summaries: List<DrugSummary>,
        query: DrugListQuery,
    ): List<DrugSummary> {
        var filtered: List<DrugSummary> = summaries
        val atcPrefix = query.atcPrefix
        if (atcPrefix != null) {
            filtered = filtered.filter { summary ->
                allDrugsById[summary.id]?.atcCode?.startsWith(prefix = atcPrefix) == true
            }
        }
        val regulatoryClassSerialName = query.regulatoryClassSerialName
        if (regulatoryClassSerialName != null) {
            val matched = regulatoryClassBySerialName[regulatoryClassSerialName]
            filtered = if (matched == null) {
                emptyList()
            } else {
                filtered.filter { summary ->
                    allDrugsById[summary.id]?.regulatoryClass?.contains(element = matched) == true
                }
            }
        }
        val routeOfAdministrationSerialName = query.routeOfAdministrationSerialName
        if (routeOfAdministrationSerialName != null) {
            val matched = routeOfAdministrationBySerialName[routeOfAdministrationSerialName]
            filtered = if (matched == null) {
                emptyList()
            } else {
                filtered.filter { summary ->
                    allDrugsById[summary.id]?.routeOfAdministration == matched
                }
            }
        }
        val dosageFormSerialName = query.dosageFormSerialName
        if (dosageFormSerialName != null) {
            val matched = dosageFormBySerialName[dosageFormSerialName]
            filtered = if (matched == null) {
                emptyList()
            } else {
                filtered.filter { summary ->
                    allDrugsById[summary.id]?.dosageForm == matched
                }
            }
        }
        val categoryName = query.categoryName
        if (categoryName != null) {
            filtered = filtered.filter { summary ->
                summary.therapeuticCategoryName == categoryName
            }
        }
        val keyword = query.keyword
        if (!keyword.isNullOrBlank()) {
            val candidateDrugs = filtered.mapNotNull { summary -> allDrugsById[summary.id] }
            val matchedDrugs = DrugSearchService.applyKeyword(
                items = candidateDrugs,
                keyword = keyword,
                match = query.keywordMatch,
                target = query.keywordTarget,
            )
            val matchedIds = matchedDrugs.map { drug -> drug.id }.toSet()
            filtered = filtered.filter { summary -> summary.id in matchedIds }
        }
        return filtered
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

        /**
         * `RegulatoryClass` の `@SerialName` 値 → enum 定数の索引。
         *
         * `/drugs?regulatory_class=<RegulatoryClass.PRESCRIPTION_REQUIRED.serialName>`
         * (= `prescription_required`) のようにクエリで `@SerialName` 値が
         * 渡される (Phase 9-8a)。`enumValues<RegulatoryClass>()` に対し
         * `serializer().descriptor.getElementName(ordinal)` で `@SerialName` が
         * 指定されていればその値、なければ enum 定数名を解決する。
         */
        private val regulatoryClassBySerialName: Map<String, RegulatoryClass> = run {
            val descriptor = serializer<RegulatoryClass>().descriptor
            enumValues<RegulatoryClass>().associateBy { value ->
                descriptor.getElementName(value.ordinal)
            }
        }

        /**
         * `RouteOfAdministration` の `@SerialName` 値 → enum 定数の索引。
         *
         * `/drugs?route=<RouteOfAdministration.ORAL.serialName>` (= `oral`) のように
         * クエリで `@SerialName` 値が渡される (Phase 9-9a)。
         * 解決方法は `regulatoryClassBySerialName` と同様。
         */
        private val routeOfAdministrationBySerialName: Map<String, RouteOfAdministration> = run {
            val descriptor = serializer<RouteOfAdministration>().descriptor
            enumValues<RouteOfAdministration>().associateBy { value ->
                descriptor.getElementName(value.ordinal)
            }
        }

        /**
         * `DosageForm` の `@SerialName` 値 → enum 定数の索引。
         *
         * `/drugs?dosage_form=<DosageForm.TABLET.serialName>` (= `tablet`) のように
         * クエリで `@SerialName` 値が渡される (Phase 9-10a)。
         * 解決方法は `regulatoryClassBySerialName` と同様。
         */
        private val dosageFormBySerialName: Map<String, DosageForm> = run {
            val descriptor = serializer<DosageForm>().descriptor
            enumValues<DosageForm>().associateBy { value ->
                descriptor.getElementName(value.ordinal)
            }
        }
    }
}
