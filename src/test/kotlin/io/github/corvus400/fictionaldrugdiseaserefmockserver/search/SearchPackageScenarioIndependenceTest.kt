package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.testutil.SCENARIO_COUPLING_PATTERNS
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * `search/` パッケージ内のすべての `.kt` ファイルが
 * `scenarioManager` / `FixtureProvider` / `getByScenario` / `X-Mock-Scenario`
 * を import / 参照していないことを grep ベースで検証するメタテスト。
 *
 * Phase 11-13 で導入。後続 Phase 12-13 (Sort / AdditionalFilter) でも
 * `SCENARIO_COUPLING_PATTERNS` を共有して同等のメタテストを流用する想定。
 */
class SearchPackageScenarioIndependenceTest {
    @Test
    fun `search package does not reference ScenarioManager nor FixtureProvider nor scenario-lookup APIs`() {
        val searchDir = Paths.get(
            "src/main/kotlin/io/github/corvus400/fictionaldrugdiseaserefmockserver/search",
        )
        val offenders =
            Files.walk(searchDir).use { stream ->
                stream
                    .filter { it.toString().endsWith(".kt") }
                    .flatMap { path ->
                        val content = Files.readString(path)
                        SCENARIO_COUPLING_PATTERNS
                            .filter { content.contains(it) }
                            .stream()
                            .map { pattern -> "$path contains forbidden '$pattern'" }
                    }
                    .toList()
            }
        assertTrue(
            offenders.isEmpty(),
            "Search services must be scenario-independent:\n${offenders.joinToString("\n")}",
        )
    }
}
