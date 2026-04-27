package io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil

/**
 * シナリオ非依存性メタテスト (grep ベース) で禁止参照として扱う文字列リスト。
 *
 * Phase 11-13 (`SearchPackageScenarioIndependenceTest`) で導入。
 * 後続 Phase 12-13 (Sort / AdditionalFilter) のメタテストでも同パターンを共有して流用する。
 */
internal val SCENARIO_COUPLING_PATTERNS: List<String> =
    listOf(
        "scenarioManager",
        "FixtureProvider",
        "getByScenario",
        "X-Mock-Scenario",
    )
