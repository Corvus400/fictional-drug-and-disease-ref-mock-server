package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.categories

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * §基本方針 9 (シナリオ非依存原則) の静的検証 (meta test)。
 *
 * `routes/categories/` 配下のソースには、シナリオ管理オブジェクトのパラメータ識別子
 * (`scenarioManager`)、`FixtureProvider` 系クラス参照、シナリオ切替ヘッダ名 (`X-Mock-Scenario`)
 * のいずれの文字列も出現してはならない。これらが混入した瞬間に CategoriesModule が
 * シナリオ依存になり得ることを示すサインなので、ビルド時に物理的に阻止する。
 *
 * Issue #89 完了条件:
 * `grep -rn 'scenarioManager\|FixtureProvider\|X-Mock-Scenario'
 * src/main/kotlin/.../routes/categories/` が 0 件であること。
 */
class CategoriesModuleSourceTest {
    @Test
    fun `routes_categories source directory exists`() {
        val routesDir = File(
            "src/main/kotlin/io/github/corvus400/fictionaldrugdiseaserefmockserver/routes/categories",
        )
        assertTrue(
            actual = routesDir.isDirectory,
            message = "expected routes/categories source directory to exist at ${routesDir.absolutePath}; " +
                "test must run from the gradle project root (cwd at projectDir)",
        )
    }

    @Test
    fun `routes_categories sources contain no scenarioManager FixtureProvider or X-Mock-Scenario references`() {
        val routesDir = File(
            "src/main/kotlin/io/github/corvus400/fictionaldrugdiseaserefmockserver/routes/categories",
        )
        val pattern = Regex(pattern = "scenarioManager|FixtureProvider|X-Mock-Scenario")
        val hits: List<String> = routesDir.walkTopDown()
            .filter { file -> file.isFile && file.extension == "kt" }
            .flatMap { file ->
                file.readLines().withIndex()
                    .filter { (_, line) -> pattern.containsMatchIn(input = line) }
                    .map { (lineIndex, line) -> "${file.name}:${lineIndex + 1}: ${line.trim()}" }
            }
            .toList()
        assertTrue(
            actual = hits.isEmpty(),
            message = "routes/categories must not reference scenarioManager / FixtureProvider / X-Mock-Scenario " +
                "(scenario-independence per §基本方針 9). Hits:\n" + hits.joinToString(separator = "\n"),
        )
    }
}
