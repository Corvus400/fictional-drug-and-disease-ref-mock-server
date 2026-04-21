package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta

/**
 * Fixture実装を強制するインターフェース
 *
 * 全てのFixture objectはこのインターフェースを実装する。
 * scenarioRoute()ヘルパー関数はFixtureProvider<T>を要求するため、
 * 未実装のFixtureはコンパイルエラーになる。
 *
 * [scenarios] Mapが全シナリオの単一情報源。OpenAPIドキュメント生成にも使用される。
 * [getByScenario] はMapからの参照にフォールバックするデフォルト実装を持つ。
 * [scenarioTitles] シナリオの静的タイトル（数値を含めない安定ラベル）。各Fixtureでoverrideする。
 * [describeFixture] Fixtureデータからキーファクトを動的生成する。各Fixtureでoverrideする。
 * [scenarioMetas] カタログ表示用メタデータ。scenarioTitles + describeFixture から自動計算。
 *
 * @see io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.scenarioRoute
 */
interface FixtureProvider<T> {
    /**
     * scenarioRoute()・OpenApiDocHelper・CatalogHtmlRenderer・テストから
     * インターフェース型経由でポリモーフィックにアクセスされる。
     * IntelliJは具象クラスの直接参照のみ追跡するため、実装クラス側で偽陽性が出る。
     * RedundantSuppression自体もIntelliJ偽陽性。
     */
    @Suppress("unused", "RedundantSuppression")
    val scenarios: Map<String, T>
    fun getByScenario(scenario: String): T = scenarios[scenario] ?: scenarios.values.first()

    /** @see scenarios — 同様の理由でIntelliJ偽陽性が出る。RedundantSuppression自体もIntelliJ偽陽性。 */
    @Suppress("unused", "RedundantSuppression")
    val scenarioTitles: Map<String, String>
        get() = emptyMap()

    fun describeFixture(fixture: T): String = ""

    val scenarioMetas: Map<String, ScenarioMeta>
        get() = scenarios.map { (name, fixture) ->
            name to ScenarioMeta(
                name = name,
                title = scenarioTitles[name] ?: name,
                description = describeFixture(fixture),
            )
        }.toMap()
}
